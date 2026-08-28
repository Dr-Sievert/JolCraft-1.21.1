package net.sievert.jolcraft.integration.jei.custom.brewing.corruption;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record JeiCorruptionRecipe(
        List<ItemStack> potionInputs,
        ItemStack ingredient,
        List<ItemStack> potionOutputs
) {

    public JeiCorruptionRecipe {
        if (potionInputs.isEmpty()) {
            throw new IllegalArgumentException(
                    "Corruption JEI recipe requires potion inputs"
            );
        }

        if (potionInputs.size() != potionOutputs.size()) {
            throw new IllegalArgumentException(
                    "Corruption JEI input and output counts must match"
            );
        }

        potionInputs =
                potionInputs.stream()
                        .map(ItemStack::copy)
                        .toList();

        ingredient =
                ingredient.copy();

        potionOutputs =
                potionOutputs.stream()
                        .map(ItemStack::copy)
                        .toList();
    }
}
