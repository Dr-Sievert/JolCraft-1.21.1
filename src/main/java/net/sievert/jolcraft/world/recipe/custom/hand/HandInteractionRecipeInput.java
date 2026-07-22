package net.sievert.jolcraft.world.recipe.custom.hand;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record HandInteractionRecipeInput(
        @NotNull ItemStack ingredientA,
        @NotNull ItemStack ingredientB
) implements RecipeInput {

    public static final String INGREDIENT_A_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.INGREDIENT,
                    "a"
            );

    public static final String INGREDIENT_B_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.INGREDIENT,
                    "b"
            );

    public HandInteractionRecipeInput {
        Objects.requireNonNull(
                ingredientA,
                INGREDIENT_A_KEY
        );

        Objects.requireNonNull(
                ingredientB,
                INGREDIENT_B_KEY
        );
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
}