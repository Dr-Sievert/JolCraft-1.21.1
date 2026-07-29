package net.sievert.jolcraft.integration.jei.util.recipe;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record JeiItemOutcome(
        @NotNull ItemStack stack,
        int minCount,
        int maxCount,
        int weight,
        int totalWeight,
        int rolls
) {
    public JeiItemOutcome {
        if (minCount < 0) {
            throw new IllegalArgumentException(
                    "minCount must be at least 0"
            );
        }

        if (maxCount < minCount) {
            throw new IllegalArgumentException(
                    "maxCount must be at least minCount"
            );
        }

        if (weight <= 0) {
            throw new IllegalArgumentException(
                    "weight must be positive"
            );
        }

        if (totalWeight < weight) {
            throw new IllegalArgumentException(
                    "totalWeight must be at least weight"
            );
        }

        if (rolls <= 0) {
            throw new IllegalArgumentException(
                    "rolls must be positive"
            );
        }
    }

    /**
     * Chance that this entry is selected during one pool roll.
     */
    public double chancePerRoll() {
        return (double) weight / totalWeight;
    }

    public boolean hasCountRange() {
        return minCount != maxCount;
    }
}