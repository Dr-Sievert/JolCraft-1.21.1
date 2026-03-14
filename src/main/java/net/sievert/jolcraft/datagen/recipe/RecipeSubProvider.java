package net.sievert.jolcraft.datagen.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Datagen recipe sub-provider contract.
 *
 * Design goals:
 * - Single entry point for all JolCraft recipe emission.
 * - One registry lookup surface ({@link RecipeLookups}) for all registry-backed params.
 * - Supports hierarchical subproviders through {@link #children()}.
 *
 * Parameters:
 * executor → JolCraft custom recipe emission
 * output   → vanilla recipe builders
 * lookups  → unified registry lookup context (items, biomes, blocks, etc.)
 */
public interface RecipeSubProvider {

    /**
     * Folder name under this recipe type.
     */
    @NotNull
    String folder();

    /**
     * Main recipe registration entry point.
     */
    void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull RecipeLookups lookups
    );

    /**
     * Scoped registration wrapper.
     */
    default void register(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull RecipeLookups lookups
    ) {
        RecipeEmissionExecutor scoped = executor.scoped(folder());
        registerRecipes(scoped, output, lookups);

        for (RecipeSubProvider child : children()) {
            child.register(scoped, output, lookups);
        }
    }

    /**
     * Optional composite providers.
     */
    default @NotNull List<? extends RecipeSubProvider> children() {
        return List.of();
    }
}