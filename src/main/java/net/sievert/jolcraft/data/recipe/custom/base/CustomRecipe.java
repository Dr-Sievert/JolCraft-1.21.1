package net.sievert.jolcraft.data.recipe.custom.base;

import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for JolCraft custom (non-vanilla) recipe types.
 *
 * Defaults away vanilla recipe-book/placement noise.
 * Concrete recipes must still implement:
 * - matches(...)
 * - assemble(...) (or implement CustomOutputRecipe instead)
 * - getSerializer()
 * - getType()
 */
public interface CustomRecipe<T extends RecipeInput> extends Recipe<T> {

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default boolean showNotification() {
        return false;
    }

    @Override
    default @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    default @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}