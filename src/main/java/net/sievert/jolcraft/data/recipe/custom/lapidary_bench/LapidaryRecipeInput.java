package net.sievert.jolcraft.data.recipe.custom.lapidary_bench;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.recipe.custom.base.ContextInput;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record LapidaryRecipeInput(
        @NotNull WorldContext ctx,
        @NotNull ItemStack input,
        @NotNull ItemStack tool
) implements RecipeInput, ContextInput {

    public LapidaryRecipeInput {
        Objects.requireNonNull(ctx, JolCraftDictionary.CONTEXT);
        Objects.requireNonNull(input, JolCraftDictionary.INPUT);
        Objects.requireNonNull(tool, JolCraftDictionary.TOOL);
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