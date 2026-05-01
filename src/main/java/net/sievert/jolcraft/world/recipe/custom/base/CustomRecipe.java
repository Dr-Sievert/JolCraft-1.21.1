package net.sievert.jolcraft.world.recipe.custom.base;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Base interface for JolCraft custom (non-vanilla) recipe types.
 *
 * Defaults away vanilla recipe-book/placement noise where supported.
 * Concrete recipes must still implement:
 * - matches(...)
 * - assemble(...) (or implement a custom output contract)
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
}