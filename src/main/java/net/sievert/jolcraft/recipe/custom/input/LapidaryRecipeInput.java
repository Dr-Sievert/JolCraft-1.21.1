package net.sievert.jolcraft.recipe.custom.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record LapidaryRecipeInput(ItemStack input, ItemStack tool) implements RecipeInput {

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.input;
            case 1 -> this.tool;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + index);
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.input.isEmpty() && this.tool.isEmpty();
    }
}
