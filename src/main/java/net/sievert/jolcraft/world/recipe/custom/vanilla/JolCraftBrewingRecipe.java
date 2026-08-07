package net.sievert.jolcraft.world.recipe.custom.vanilla;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import org.jetbrains.annotations.NotNull;

public record JolCraftBrewingRecipe(
        Ingredient input,
        Ingredient ingredient,
        ItemStack output
) implements IBrewingRecipe {

    @Override
    public boolean isInput(@NotNull ItemStack stack) {
        return input.test(stack);
    }

    @Override
    public boolean isIngredient(@NotNull ItemStack stack) {
        return ingredient.test(stack);
    }

    @Override
    public @NotNull ItemStack getOutput(@NotNull ItemStack inputStack, @NotNull ItemStack ingredientStack) {
        if (!isInput(inputStack) || !isIngredient(ingredientStack)) {
            return ItemStack.EMPTY;
        }

        return output.copy();
    }
}