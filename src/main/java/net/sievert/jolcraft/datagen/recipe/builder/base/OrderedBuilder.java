package net.sievert.jolcraft.datagen.recipe.builder.base;

import org.jetbrains.annotations.NotNull;

/**
 * Opt-in contract for RecipeBuilders that support
 * executor-assigned sequential ordering (1..N).
 *
 * Order value:
 * - 0  = not yet assigned (fallback)
 * - >0 = assigned (manual or executor)
 */
public interface OrderedBuilder extends RecipeBuilder {

    /**
     * Stable bucket key used for sequencing.
     * Builders sharing this key share the same 1..N counter.
     */
    @NotNull String orderKey();

    /**
     * Current order value.
     * 0 means "not assigned".
     */
    int order();

    /**
     * Executor assigns computed order here.
     */
    void setOrder(int order);
}