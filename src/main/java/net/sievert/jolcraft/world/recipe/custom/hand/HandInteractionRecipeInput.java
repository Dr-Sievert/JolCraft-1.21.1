package net.sievert.jolcraft.world.recipe.custom.hand;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.custom.base.ContextInput;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record HandInteractionRecipeInput(
        @NotNull WorldContext ctx,
        @NotNull ItemStack ingredientA,
        @NotNull ItemStack ingredientB
) implements RecipeInput, ContextInput, ItemTransformSourceResolver {

    public HandInteractionRecipeInput {
        Objects.requireNonNull(ctx, JolCraftDictionary.CONTEXT);
        Objects.requireNonNull(ingredientA, JolCraftStrings.underscored(JolCraftDictionary.INGREDIENT, "a"));
        Objects.requireNonNull(ingredientB, JolCraftStrings.underscored(JolCraftDictionary.INGREDIENT, "b"));
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> ingredientA;
            case 1 -> ingredientB;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public @NotNull ItemStack resolveItemTransformSource(@NotNull String source) {
        if (HandInteractionRecipe.SOURCE_INGREDIENT_A.equals(source)) {
            return ingredientA;
        }

        if (HandInteractionRecipe.SOURCE_INGREDIENT_B.equals(source)) {
            return ingredientB;
        }

        return ItemStack.EMPTY;
    }
}