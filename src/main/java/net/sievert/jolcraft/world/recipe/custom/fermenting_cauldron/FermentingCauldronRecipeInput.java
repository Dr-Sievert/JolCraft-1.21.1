package net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@MethodsReturnNonnullByDefault
public record FermentingCauldronRecipeInput(
        @NotNull ItemStack ingredient,
        @NotNull ItemStack lastIngredient
) implements RecipeInput {

    private static final String LAST_INGREDIENT_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LAST,
                    JolCraftDictionary.INGREDIENT
            );

    public FermentingCauldronRecipeInput {
        Objects.requireNonNull(
                ingredient,
                JolCraftDictionary.INGREDIENT
        );

        Objects.requireNonNull(
                lastIngredient,
                LAST_INGREDIENT_KEY
        );
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