package net.sievert.jolcraft.data.recipe.custom.fermenting_cauldron;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.recipe.custom.base.ContextInput;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@MethodsReturnNonnullByDefault
public record FermentingCauldronRecipeInput(
        @NotNull WorldContext ctx,
        @NotNull ItemStack ingredient,
        @NotNull ItemStack lastIngredient
) implements RecipeInput, ContextInput {

    public FermentingCauldronRecipeInput {
        Objects.requireNonNull(ctx, JolCraftDictionary.CONTEXT);
        Objects.requireNonNull(ingredient, JolCraftDictionary.INGREDIENT);
        Objects.requireNonNull(lastIngredient, JolCraftStrings.underscored(JolCraftDictionary.LAST, JolCraftDictionary.INGREDIENT));
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> ingredient;
            case 1 -> lastIngredient;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }
}