package net.sievert.jolcraft.world.recipe.custom.bounty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Shared helpers for bounty-related recipes (task + reward).
 * Keeps validation/codecs and common stack decoding in one place.
 *
 * Canonical bounty identity is:
 * - type -> {@link DwarfProfession} stored as professionName() (e.g. "merchant")
 * - tier -> {@link DwarfMerchantData.Level} serialized in JSON as lowercase name (e.g. "master")
 * - tier -> stored on item stacks as raw int [1..5]
 *
 * Rules:
 * - persisted recipe data is strict and fail-closed
 * - raw stack accessors return null on missing/invalid data
 * - {@link #readInfo(ItemStack)} is the canonical validated read path
 */
public final class BountyRecipe {

    private BountyRecipe() {}

    public static final String TYPE_KEY =
            JolCraftStrings.underscored(JolCraftDictionary.BOUNTY, JolCraftDictionary.TYPE);

    public static final String TIER_KEY =
            JolCraftStrings.underscored(JolCraftDictionary.BOUNTY, JolCraftDictionary.TIER);

    // -------------------------------------------------------------------------
    // Canonical component handles
    // -------------------------------------------------------------------------

    private static final DataComponentType<String> BOUNTY_TYPE_COMP =
            JolCraftDataComponents.BOUNTY_TYPE.get();

    private static final DataComponentType<Integer> BOUNTY_TIER_COMP =
            JolCraftDataComponents.BOUNTY_TIER.get();

    // -------------------------------------------------------------------------
    // Codecs
    // -------------------------------------------------------------------------

    /**
     * Strict codec for bounty type.
     * Stored as professionName(), e.g. "merchant".
     * Rejects null/blank/unknown/NONE.
     */
    public static final Codec<DwarfProfession> BOUNTY_TYPE_CODEC =
            Codec.STRING.comapFlatMap(
                    raw -> validateType(parseType(raw), raw),
                    DwarfProfession::professionName
            );

    /**
     * Strict codec for bounty tier.
     * Stored in recipe JSON as lowercase enum name, e.g. "novice", "master".
     *
     * Note:
     * - stack storage still uses raw merchant level int via components
     * - this codec is only for recipe/datapack serialization
     */
    public static final Codec<DwarfMerchantData.Level> BOUNTY_TIER_CODEC =
            Codec.STRING.comapFlatMap(
                    BountyRecipe::validateTier,
                    level -> level.name().toLowerCase(Locale.ROOT)
            );

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    public static @NotNull DataResult<DwarfMerchantData.Level> validateTier(int rawTier) {
        DwarfMerchantData.Level tier = parseTier(rawTier);
        if (tier == null) {
            return DataResult.error(() -> "invalid " + TIER_KEY + " '" + rawTier + "'");
        }
        return DataResult.success(tier);
    }

    public static @NotNull DataResult<DwarfMerchantData.Level> validateTier(@Nullable String rawTier) {
        if (rawTier == null) {
            return DataResult.error(() -> TIER_KEY + " is required");
        }

        if (rawTier.isBlank()) {
            return DataResult.error(() -> TIER_KEY + " must not be empty");
        }

        DwarfMerchantData.Level tier = parseTier(rawTier);
        if (tier == null) {
            return DataResult.error(() -> "invalid " + TIER_KEY + " '" + rawTier + "'");
        }

        return DataResult.success(tier);
    }

    public static @NotNull DataResult<DwarfProfession> validateType(
            @Nullable DwarfProfession type,
            @Nullable String raw
    ) {
        if (raw == null) {
            return DataResult.error(() -> TYPE_KEY + " is required");
        }
        if (raw.isBlank()) {
            return DataResult.error(() -> TYPE_KEY + " must not be empty");
        }
        if (type == null || type == DwarfProfession.NONE) {
            return DataResult.error(() -> "unknown " + TYPE_KEY + " '" + raw + "'");
        }
        return DataResult.success(type);
    }

    public static @NotNull DataResult<BountyInfo> validateInfo(
            @Nullable DwarfProfession type,
            @Nullable DwarfMerchantData.Level tier
    ) {
        if (type == null || type == DwarfProfession.NONE) {
            return DataResult.error(() -> TYPE_KEY + " is required");
        }

        if (tier == null) {
            return DataResult.error(() -> TIER_KEY + " is required");
        }

        return DataResult.success(new BountyInfo(type, tier));
    }

    // -------------------------------------------------------------------------
    // Parsing helpers
    // -------------------------------------------------------------------------

    /**
     * Parse bounty type from stored profession name.
     * Returns null for null/blank/invalid/NONE.
     */
    public static @Nullable DwarfProfession parseType(@Nullable String raw) {
        if (raw == null || raw.isBlank()) return null;

        DwarfProfession type = DwarfProfession.fromProfessionName(raw);
        if (type == DwarfProfession.NONE) return null;

        return type;
    }

    /**
     * Parse bounty tier from raw stored level.
     * Returns null for values outside [1..5].
     *
     * Intentionally does not use Level.fromId(raw) directly because that method
     * falls back to NOVICE and is not strict enough for bounty validation.
     */
    public static @Nullable DwarfMerchantData.Level parseTier(int raw) {
        if (raw < DwarfMerchantData.MIN_MERCHANT_LEVEL
                || raw > DwarfMerchantData.MAX_MERCHANT_LEVEL) {
            return null;
        }

        return DwarfMerchantData.Level.fromId(raw);
    }

    /**
     * Parse bounty tier from lowercase/uppercase serialized name.
     * Returns null for null/blank/unknown values.
     */
    public static @Nullable DwarfMerchantData.Level parseTier(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return DwarfMerchantData.Level.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Stack read/write
    // -------------------------------------------------------------------------

    public static @Nullable DwarfProfession getType(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (!stack.has(BOUNTY_TYPE_COMP)) return null;

        String raw = stack.get(BOUNTY_TYPE_COMP);
        return parseType(raw);
    }

    public static @Nullable DwarfMerchantData.Level getTier(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (!stack.has(BOUNTY_TIER_COMP)) return null;

        Integer raw = stack.get(BOUNTY_TIER_COMP);
        if (raw == null) return null;

        return parseTier(raw);
    }

    public static void setType(@NotNull ItemStack stack, @Nullable DwarfProfession type) {
        if (stack.isEmpty()) return;
        if (type == null || type == DwarfProfession.NONE) return;

        stack.set(BOUNTY_TYPE_COMP, type.professionName());
    }

    public static void setTier(@NotNull ItemStack stack, @Nullable DwarfMerchantData.Level tier) {
        if (stack.isEmpty()) return;
        if (tier == null) return;

        stack.set(BOUNTY_TIER_COMP, tier.getId());
    }

    public static @NotNull DataResult<BountyInfo> readInfo(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return DataResult.error(() -> "stack is empty");
        }
        if (!stack.has(BOUNTY_TYPE_COMP)) {
            return DataResult.error(() -> "missing component: " + TYPE_KEY);
        }
        if (!stack.has(BOUNTY_TIER_COMP)) {
            return DataResult.error(() -> "missing component: " + TIER_KEY);
        }

        DwarfProfession type = getType(stack);
        if (type == null) {
            return DataResult.error(() -> "invalid " + TYPE_KEY + " on stack");
        }

        DwarfMerchantData.Level tier = getTier(stack);
        if (tier == null) {
            return DataResult.error(() -> "invalid " + TIER_KEY + " on stack");
        }

        return DataResult.success(new BountyInfo(type, tier));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidBountyStack(@NotNull ItemStack stack) {
        return readInfo(stack).result().isPresent();
    }

    public record BountyInfo(
            @NotNull DwarfProfession type,
            @NotNull DwarfMerchantData.Level tier
    ) {}
}