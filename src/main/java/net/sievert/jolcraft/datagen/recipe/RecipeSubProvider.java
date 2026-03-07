package net.sievert.jolcraft.datagen.recipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RecipeSubProvider {

    String folder();

    /**
     * Single entry point for all subproviders.
     *
     * executor → JolCraft custom recipe emission
     * output   → vanilla recipe builders
     * items    → required for vanilla builders (ShapedRecipeBuilder, has(), etc)
     */
    void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    );

    /**
     * Default registration logic.
     */
    default void register(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        RecipeEmissionExecutor scoped = executor.scoped(folder());
        registerRecipes(scoped, output, items);
    }

    /**
     * Optional composite providers.
     */
    default List<? extends RecipeSubProvider> children() {
        return List.of();
    }
}