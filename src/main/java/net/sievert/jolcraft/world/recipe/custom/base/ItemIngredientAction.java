package net.sievert.jolcraft.world.recipe.custom.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Side-effect to apply to a matched ingredient stack.
 *
 * Fail-closed:
 * - Missing/invalid type => CATALYST
 * - For CONSUME/DAMAGE: missing amount defaults to 1
 * - Amount < 1 => validation error (and stream decode clamps to 1)
 *
 * JSON examples:
 * { "type": "consume" }
 * { "type": "consume", "amount": 3 }
 * { "type": "damage",  "amount": 2 }
 * { "type": "catalyst" }
 */
public record ItemIngredientAction(
        @NotNull Type type,
        @Nullable Integer amount
) {

    // ---------------------------------------------------------------------
    // Sentinels
    // ---------------------------------------------------------------------

    public static final ItemIngredientAction CATALYST =
            new ItemIngredientAction(Type.CATALYST, null);

    // ---------------------------------------------------------------------
    // Type
    // ---------------------------------------------------------------------

    public enum Type implements JolCraftEnumHelper.StringId {
        CONSUME(JolCraftDictionary.CONSUME),
        CATALYST(JolCraftDictionary.CATALYST),
        DAMAGE(JolCraftDictionary.DAMAGE);

        private final String id;

        Type(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public static @NotNull Type byId(@Nullable String id) {
            return JolCraftEnumHelper.byStringId(Type.class, id, CATALYST);
        }
    }

    // ---------------------------------------------------------------------
    // Codec
    // ---------------------------------------------------------------------

    public static final Codec<Type> TYPE_CODEC =
            Codec.STRING.xmap(Type::byId, Type::getId);

    public static final Codec<ItemIngredientAction> CODEC =
            RecordCodecBuilder.create((RecordCodecBuilder.Instance<ItemIngredientAction> inst) ->
                    inst.group(
                            TYPE_CODEC
                                    .optionalFieldOf(JolCraftDictionary.TYPE, Type.CATALYST)
                                    .forGetter(ItemIngredientAction::type),

                            Codec.INT
                                    .optionalFieldOf(JolCraftDictionary.AMOUNT)
                                    .forGetter(a -> a.amount() == null
                                            ? Optional.empty()
                                            : Optional.of(a.amount()))
                    ).apply(inst, (Type t, Optional<Integer> amtOpt) ->
                            new ItemIngredientAction(t, amtOpt.orElse(null)))
            ).validate(ItemIngredientAction::validateAndNormalize);

    /**
     * Stream contract:
     * - amount encoded as VAR_INT
     * - 0 means "not present"
     * - decode normalizes defaults (consume/damage => 1, catalyst => null)
     * - decode clamps invalid amounts to 1 (fail-closed)
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemIngredientAction> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, (ItemIngredientAction a) -> a.type().getId(),
                    ByteBufCodecs.VAR_INT, (ItemIngredientAction a) -> {
                        Integer amt = a.amount();
                        if (amt == null) return 0;
                        return Math.max(0, amt);
                    },
                    (String typeId, Integer amtRaw) -> {
                        Type t = Type.byId(typeId);

                        int raw = amtRaw == null ? 0 : Math.max(0, amtRaw);
                        Integer amt = raw > 0 ? raw : null;

                        if (t == Type.CATALYST) {
                            return CATALYST;
                        }

                        if (amt == null) {
                            amt = 1;
                        }

                        return new ItemIngredientAction(t, amt);
                    }
            );

    // ---------------------------------------------------------------------
    // Runtime helpers
    // ---------------------------------------------------------------------

    public static int amountOrDefault(@NotNull ItemIngredientAction action) {
        Integer a = action.amount();
        return (a == null || a < 1) ? 1 : a;
    }

    /**
     * Runtime satisfiable check for a concrete stack.
     *
     * Contract:
     * - CATALYST => true
     * - CONSUME(n) => stack count >= n
     * - DAMAGE(n)  => stack.isDamageableItem()
     */
    public static boolean isSatisfied(@NotNull ItemStack stack, @NotNull ItemIngredientAction action) {
        if (stack.isEmpty()) return false;

        Type t = action.type();
        if (t == Type.CATALYST) {
            return true;
        }

        int amt = amountOrDefault(action);

        if (t == Type.CONSUME) {
            return stack.getCount() >= amt;
        }

        if (t == Type.DAMAGE) {
            return stack.isDamageableItem();
        }

        return false;
    }

    /**
     * Apply the action to the given stack.
     *
     * Contract:
     * - Creative players => no-op
     * - Fail-closed: empty stack => no-op
     * - Missing player for player-dependent behavior => no-op
     * - CONSUME shrinks by amount (clamped)
     * - DAMAGE uses vanilla hurtAndBreak (break is allowed)
     * - CATALYST => no-op
     */
    public static void apply(
            @NotNull WorldContext ctx,
            @NotNull ItemStack stack,
            @NotNull ItemIngredientAction action
    ) {
        if (stack.isEmpty()) {
            return;
        }

        Type t = action.type();
        if (t == Type.CATALYST) {
            return;
        }

        Player player = ctx.player();
        if (player != null && player.isCreative()) {
            return;
        }

        int amt = amountOrDefault(action);

        if (t == Type.CONSUME) {
            int shrink = Math.min(amt, stack.getCount());
            if (shrink > 0) {
                stack.shrink(shrink);
            }
            return;
        }

        if (t == Type.DAMAGE) {
            if (!stack.isDamageableItem()) {
                return;
            }

            if (!(player instanceof ServerPlayer sp)) {
                return;
            }

            stack.hurtAndBreak(
                    amt,
                    ctx.level(),
                    sp,
                    brokenItem -> ctx.level().playSound(
                            null,
                            sp.blockPosition(),
                            brokenItem.getBreakingSound(),
                            sp.getSoundSource(),
                            1.0F,
                            1.0F
                    )
            );
        }
    }

    // ---------------------------------------------------------------------
    // Validation / normalization
    // ---------------------------------------------------------------------

    public @NotNull DataResult<ItemIngredientAction> validate() {
        return validateAndNormalize(this);
    }

    private static @NotNull DataResult<ItemIngredientAction> validateAndNormalize(@NotNull ItemIngredientAction a) {
        Type t = a.type();
        Integer amt = a.amount();

        if (t == Type.CATALYST) {
            return DataResult.success(amt == null ? a : CATALYST);
        }

        if (amt == null) {
            return DataResult.success(new ItemIngredientAction(t, 1));
        }

        if (amt < 1) {
            return DataResult.error(() -> JolCraftDictionary.AMOUNT);
        }

        return DataResult.success(a);
    }
}