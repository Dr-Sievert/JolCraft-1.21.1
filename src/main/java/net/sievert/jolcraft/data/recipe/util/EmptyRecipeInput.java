package net.sievert.jolcraft.data.recipe.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for context-only recipe inputs (no inventory slots).
 */
public class EmptyRecipeInput implements RecipeInput {

    protected EmptyRecipeInput() {}

    @Override
    public @NotNull ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }
}