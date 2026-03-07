package net.sievert.jolcraft.data.recipe.param;

import com.mojang.serialization.DataResult;

public interface Param {

    /**
     * Uniform validation entry point.
     * Must never throw.
     */
    DataResult<?> validate();
}