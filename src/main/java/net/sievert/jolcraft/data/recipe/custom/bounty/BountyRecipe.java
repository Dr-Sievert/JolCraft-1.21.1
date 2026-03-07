package net.sievert.jolcraft.data.recipe.custom.bounty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Shared helpers for bounty-related recipes (task + reward).
 * Keeps validation/codecs and common stack decoding in one place.
 */
public final class BountyRecipe {

    private BountyRecipe() {}

    public static final String TYPE_KEY = JolCraftStrings.underscored(JolCraftDictionary.BOUNTY, JolCraftDictionary.TYPE); // "bounty_type"
    public static final String TIER_KEY = JolCraftStrings.underscored(JolCraftDictionary.BOUNTY, JolCraftDictionary.TIER); // "bounty_tier"

    // -------------------------------------------------------------------------
    // Canonical component handles
    // -------------------------------------------------------------------------

    private static final DataComponentType<String> BOUNTY_TYPE_COMP = JolCraftDataComponents.BOUNTY_TYPE.get();
    private static final DataComponentType<Integer> BOUNTY_TIER_COMP = JolCraftDataComponents.BOUNTY_TIER.get();

    // -------------------------------------------------------------------------
    // Codecs
    // -------------------------------------------------------------------------

    /** Strict codec for bounty type: rejects UNKNOWN. */
    public static final Codec<BountyType> BOUNTY_TYPE_CODEC =
            Codec.STRING.comapFlatMap(
                    raw -> validateType(BountyType.fromString(raw), raw),
                    BountyType::getId
            );

    /** Strict codec for bounty tier: must map to a known tier (not UNKNOWN). */
    public static final Codec<BountyTier> BOUNTY_TIER_CODEC =
            Codec.INT.comapFlatMap(
                    BountyRecipe::validateTier,
                    BountyTier::getId
            );

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    public static @NotNull DataResult<BountyTier> validateTier(int rawTier) {
        BountyTier tier = BountyTier.fromValue(rawTier);
        if (tier == BountyTier.UNKNOWN) {
            return DataResult.error(() -> "invalid " + TIER_KEY + " '" + rawTier + "'");
        }
        return DataResult.success(tier);
    }

    public static @NotNull DataResult<BountyType> validateType(@Nullable BountyType type, @Nullable String raw) {
        if (raw == null) return DataResult.error(() -> TYPE_KEY + " is required");
        if (raw.isEmpty()) return DataResult.error(() -> TYPE_KEY + " must not be empty");
        if (type == null || type == BountyType.UNKNOWN) {
            return DataResult.error(() -> "unknown " + TYPE_KEY + " '" + raw + "'");
        }
        return DataResult.success(type);
    }

    public static @NotNull DataResult<BountyInfo> validateInfo(
            @Nullable BountyType type,
            @Nullable BountyTier tier
    ) {

        // ---- type ----
        String rawType = type != null ? type.getId() : null;
        DataResult<BountyType> typeRes = validateType(type, rawType);

        var typeErr = typeRes.error();
        if (typeErr.isPresent()) {
            String msg = typeErr.map(DataResult.Error::message).orElse("invalid " + TYPE_KEY);
            return DataResult.error(() -> msg);
        }

        BountyType validType = typeRes.result().orElse(null);
        if (validType == null) {
            return DataResult.error(() -> TYPE_KEY + " is required");
        }

        // ---- tier ----
        int rawTier = tier != null ? tier.getId() : -1;
        DataResult<BountyTier> tierRes = validateTier(rawTier);

        var tierErr = tierRes.error();
        if (tierErr.isPresent()) {
            String msg = tierErr.map(DataResult.Error::message).orElse("invalid " + TIER_KEY);
            return DataResult.error(() -> msg);
        }

        BountyTier validTier = tierRes.result().orElse(null);
        if (validTier == null) {
            return DataResult.error(() -> TIER_KEY + " is required");
        }

        return DataResult.success(new BountyInfo(validType, validTier));
    }

    // -------------------------------------------------------------------------
    // Stack read/write
    // -------------------------------------------------------------------------

    public static @NotNull BountyType getType(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return BountyType.UNKNOWN;
        if (!stack.has(BOUNTY_TYPE_COMP)) return BountyType.UNKNOWN;

        String raw = stack.get(BOUNTY_TYPE_COMP);
        if (raw == null) return BountyType.UNKNOWN;

        BountyType type = BountyType.fromString(raw);
        return type == null ? BountyType.UNKNOWN : type;
    }

    public static @NotNull BountyTier getTier(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return BountyTier.UNKNOWN;
        if (!stack.has(BOUNTY_TIER_COMP)) return BountyTier.UNKNOWN;

        Integer raw = stack.get(BOUNTY_TIER_COMP);
        if (raw == null) return BountyTier.UNKNOWN;

        return BountyTier.fromValue(raw);
    }

    public static void setType(@NotNull ItemStack stack, @Nullable BountyType type) {
        if (stack.isEmpty()) return;
        if (type == null || type == BountyType.UNKNOWN) return;
        stack.set(BOUNTY_TYPE_COMP, type.getId());
    }

    public static void setTier(@NotNull ItemStack stack, @Nullable BountyTier tier) {
        if (stack.isEmpty()) return;
        if (tier == null || tier == BountyTier.UNKNOWN) return;
        stack.set(BOUNTY_TIER_COMP, tier.getId());
    }

    public static DataResult<BountyInfo> readInfo(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return DataResult.error(() -> "stack is empty");
        if (!stack.has(BOUNTY_TYPE_COMP)) return DataResult.error(() -> "missing component: " + TYPE_KEY);
        if (!stack.has(BOUNTY_TIER_COMP)) return DataResult.error(() -> "missing component: " + TIER_KEY);

        BountyType type = getType(stack);
        if (type == BountyType.UNKNOWN) return DataResult.error(() -> "invalid " + TYPE_KEY + " on stack");

        BountyTier tier = getTier(stack);
        if (tier == BountyTier.UNKNOWN) return DataResult.error(() -> "invalid " + TIER_KEY + " on stack");

        return DataResult.success(new BountyInfo(type, tier));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidBountyStack(@NotNull ItemStack stack) {
        return readInfo(stack).result().isPresent();
    }

    public record BountyInfo(@NotNull BountyType type, @NotNull BountyTier tier) {}
}