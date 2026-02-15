package net.sievert.jolcraft.data.recipe.custom.hand;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record HandInteractionRecipeInput(
        ItemStack mainHand,
        ItemStack offHand
) implements RecipeInput {

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> mainHand;
            case 1 -> offHand;
            default -> throw new IllegalArgumentException("HandInteractionRecipeInput has no slot " + index);
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return mainHand.isEmpty() && offHand.isEmpty();
    }
}
