package net.sievert.jolcraft.world.recipe.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for JolCraft recipe types that are executed by custom
 * machines, interactions, traders, or other external systems.
 *
 * Vanilla crafting assembly and recipe-book presentation are adapter-only.
 * Concrete recipes remain responsible for matching, serialization, type,
 * and serializer definitions.
 */
public interface CustomRecipe<T extends RecipeInput> extends Recipe<T> {

    @Override
    default @NotNull ItemStack assemble(
            @NotNull T input,
            HolderLookup.@NotNull Provider registries
    ) {
        return ItemStack.EMPTY;
    }

    @Override
    default @NotNull ItemStack getResultItem(
            HolderLookup.@NotNull Provider registries
    ) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean canCraftInDimensions(
            int width,
            int height
    ) {
        return false;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default boolean showNotification() {
        return false;
    }
}
