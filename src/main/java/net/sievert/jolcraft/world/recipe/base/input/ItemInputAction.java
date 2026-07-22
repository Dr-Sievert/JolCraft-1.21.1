package net.sievert.jolcraft.world.recipe.base.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record ItemInputAction(
        @NotNull Type type,
        @Nullable Integer amount
) {

    public static final ItemInputAction CATALYST =
            new ItemInputAction(Type.CATALYST, null);

    public ItemInputAction {
        Objects.requireNonNull(type, "type");

        if (type == Type.CATALYST) {
            amount = null;
        }
    }

    public static final Codec<ItemInputAction> CODEC =
            RecordCodecBuilder.<ItemInputAction>create(instance ->
                    instance.group(
                            Type.CODEC
                                    .optionalFieldOf(
                                            JolCraftDictionary.TYPE,
                                            Type.CATALYST
                                    )
                                    .forGetter(ItemInputAction::type),

                            Codec.INT
                                    .optionalFieldOf(
                                            JolCraftDictionary.AMOUNT
                                    )
                                    .forGetter(action ->
                                            Optional.ofNullable(
                                                    action.amount()
                                            )
                                    )
                    ).apply(
                            instance,
                            (type, amount) ->
                                    new ItemInputAction(
                                            type,
                                            amount.orElse(null)
                                    )
                    )
            ).validate(ItemInputAction::validateAndNormalize);

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            ItemInputAction
            > STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            action -> action.type().getSerializedName(),

            ByteBufCodecs.VAR_INT,
            action -> action.amount() == null
                    ? 0
                    : Math.max(0, action.amount()),

            (typeId, encodedAmount) -> {
                Type type = Type.byId(typeId);

                if (type == Type.CATALYST) {
                    return CATALYST;
                }

                return new ItemInputAction(
                        type,
                        Math.max(1, encodedAmount)
                );
            }
    );

    public static ItemInputAction consume() {
        return consume(1);
    }

    public static ItemInputAction consume(int amount) {
        return new ItemInputAction(Type.CONSUME, amount);
    }

    public static ItemInputAction damage() {
        return damage(1);
    }

    public static ItemInputAction damage(int amount) {
        return new ItemInputAction(Type.DAMAGE, amount);
    }

    public int resolvedAmount() {
        return amount == null || amount < 1
                ? 1
                : amount;
    }

    public boolean isSatisfied(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return switch (type) {
            case CATALYST -> true;
            case CONSUME ->
                    stack.getCount() >= resolvedAmount();
            case DAMAGE ->
                    stack.isDamageableItem();
        };
    }

    public void apply(
            ServerLevel level,
            @Nullable ServerPlayer player,
            ItemStack stack
    ) {
        if (type == Type.CATALYST || !isSatisfied(stack)) {
            return;
        }

        if (player != null && player.isCreative()) {
            return;
        }

        switch (type) {
            case CONSUME ->
                    stack.shrink(resolvedAmount());

            case DAMAGE ->
                    stack.hurtAndBreak(
                            resolvedAmount(),
                            level,
                            player,
                            brokenItem -> {
                                if (player != null) {
                                    level.playSound(
                                            null,
                                            player.blockPosition(),
                                            brokenItem.getBreakingSound(),
                                            player.getSoundSource(),
                                            1.0F,
                                            1.0F
                                    );
                                }
                            }
                    );
        }
    }

    public DataResult<ItemInputAction> validate() {
        return validateAndNormalize(this);
    }

    private static DataResult<ItemInputAction> validateAndNormalize(
            ItemInputAction action
    ) {
        if (action.type() == Type.CATALYST) {
            return DataResult.success(CATALYST);
        }

        if (action.amount() == null) {
            return DataResult.success(
                    new ItemInputAction(action.type(), 1)
            );
        }

        if (action.amount() < 1) {
            return DataResult.error(() ->
                    JolCraftDictionary.AMOUNT
                            + " must be at least 1"
            );
        }

        return DataResult.success(action);
    }

    public enum Type implements StringRepresentable {
        CONSUME(JolCraftDictionary.CONSUME),
        CATALYST(JolCraftDictionary.CATALYST),
        DAMAGE(JolCraftDictionary.DAMAGE);

        public static final Codec<Type> CODEC =
                StringRepresentable.fromEnum(Type::values);

        private final String serializedName;

        Type(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public @NotNull String getSerializedName() {
            return serializedName;
        }

        public static Type byId(@Nullable String id) {
            if (id != null) {
                for (Type type : values()) {
                    if (type.serializedName.equals(id)) {
                        return type;
                    }
                }
            }

            return CATALYST;
        }
    }
}
