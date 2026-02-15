package net.sievert.jolcraft.data.recipe.custom.bounty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;

import javax.annotation.Nullable;

/**
 * Shared helpers for bounty-related recipes (task + reward).
 * Keeps validation/codecs and common stack decoding in one place.
 */
public final class BountyRecipe {

    private BountyRecipe() {}

    /** Strict codec for bounty type: rejects UNKNOWN. */
    public static final Codec<BountyType> BOUNTY_TYPE_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> {
                        String id = s.trim().toLowerCase();
                        BountyType t = BountyType.fromString(id);
                        if (t == null || t == BountyType.UNKNOWN) {
                            return DataResult.error(() -> "Unknown bounty_type '" + s + "'");
                        }
                        return DataResult.success(t);
                    },
                    BountyType::getId
            );

    public static DataResult<Integer> validateTier(int tier) {
        if (tier < 1 || tier > 5 || BountyTier.fromValue(tier) == BountyTier.UNKNOWN) {
            return DataResult.error(() -> "tier must be 1..5 and map to a known tier (got " + tier + ")");
        }
        return DataResult.success(tier);
    }

    public static DataResult<BountyType> validateType(@Nullable BountyType type) {
        if (type == null || type == BountyType.UNKNOWN) {
            return DataResult.error(() -> "bounty_type must be a valid type (not unknown)");
        }
        return DataResult.success(type);
    }

    public static BountyType readType(ItemStack stack) {
        String raw = stack.get(JolCraftDataComponents.BOUNTY_TYPE.get());
        if (raw == null || raw.isEmpty()) return BountyType.UNKNOWN;

        BountyType t = BountyType.fromString(raw);
        return (t == null) ? BountyType.UNKNOWN : t;
    }

    public static BountyTier readTier(ItemStack stack) {
        Integer raw = stack.get(JolCraftDataComponents.BOUNTY_TIER.get());
        if (raw == null) return BountyTier.UNKNOWN;
        return BountyTier.fromValue(raw);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidBountyStack(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!stack.has(JolCraftDataComponents.BOUNTY_TYPE.get())) return false;
        if (!stack.has(JolCraftDataComponents.BOUNTY_TIER.get())) return false;

        BountyType type = BountyRecipe.readType(stack);
        BountyTier tier = BountyRecipe.readTier(stack);

        return type != BountyType.UNKNOWN && tier != BountyTier.UNKNOWN;
    }
}