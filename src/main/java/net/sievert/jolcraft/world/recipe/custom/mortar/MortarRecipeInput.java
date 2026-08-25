package net.sievert.jolcraft.world.recipe.custom.mortar;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record MortarRecipeInput(
        @NotNull ItemStack input1,
        @NotNull ItemStack input2,
        @NotNull ItemStack input3,
        @NotNull ItemStack tool
) implements RecipeInput {

    public MortarRecipeInput {
        Objects.requireNonNull(
                input1,
                JolCraftDictionary.INPUT
        );

        Objects.requireNonNull(
                input2,
                JolCraftDictionary.INPUT
        );

        Objects.requireNonNull(
                input3,
                JolCraftDictionary.INPUT
        );

        Objects.requireNonNull(
                tool,
                JolCraftDictionary.TOOL
        );
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> input1;
            case 1 -> input2;
            case 2 -> input3;
            case 3 -> tool;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 4;
    }
}