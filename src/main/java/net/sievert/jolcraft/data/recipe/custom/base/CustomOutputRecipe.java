package net.sievert.jolcraft.data.recipe.custom.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

/**
 * Base for JolCraft recipes whose real runtime output is NOT a single ItemStack.
 *
 * Vanilla assemble(...) is adapter-only and always returns EMPTY.
 */
public interface CustomOutputRecipe<T extends RecipeInput, O> extends CustomRecipe<T> {

    /**
     * Real runtime output resolution. Must be pure (no side effects) and fail-closed.
     */
    @NotNull O roll(@NotNull T input, @NotNull WorldContext ctx);

    @Override
    default @NotNull ItemStack assemble(@NotNull T input, HolderLookup.@NotNull Provider registries) {
        return ItemStack.EMPTY;
    }
}