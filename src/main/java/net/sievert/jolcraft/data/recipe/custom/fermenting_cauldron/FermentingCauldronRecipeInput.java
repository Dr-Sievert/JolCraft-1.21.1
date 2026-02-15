package net.sievert.jolcraft.data.recipe.custom.fermenting_cauldron;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

@MethodsReturnNonnullByDefault
public record FermentingCauldronRecipeInput(
        ItemStack usedItem,
        ItemStack lastIngredient
) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> usedItem;
            case 1 -> lastIngredient;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }

    public boolean isVanillaFullWaterCauldron() {
        return lastIngredient.isEmpty();
    }
}
