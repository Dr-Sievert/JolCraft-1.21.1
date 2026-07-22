package net.sievert.jolcraft.world.recipe.custom.lapidary_bench;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record LapidaryRecipeInput(
        @NotNull ItemStack input,
        @NotNull ItemStack tool
) implements RecipeInput {

    public LapidaryRecipeInput {
        Objects.requireNonNull(
                input,
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
            case 0 -> input;
            case 1 -> tool;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
