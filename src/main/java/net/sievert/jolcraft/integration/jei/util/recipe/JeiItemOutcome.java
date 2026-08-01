package net.sievert.jolcraft.integration.jei.util.recipe;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record JeiItemOutcome(
        @NotNull ItemStack stack,
        int minCount,
        int maxCount,
        int weight,
        int totalWeight,
        int minRolls,
        int maxRolls
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

        if (minRolls <= 0) {
            throw new IllegalArgumentException(
                    "minRolls must be positive"
            );
        }

        if (maxRolls < minRolls) {
            throw new IllegalArgumentException(
                    "maxRolls must be at least minRolls"
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

    public boolean hasRollRange() {
        return minRolls != maxRolls;
    }

    public boolean hasMultipleRolls() {
        return maxRolls > 1;
    }
}