package net.sievert.jolcraft.world.recipe.param.base;

import com.mojang.serialization.DataResult;

public interface Param {

    /**
     * Uniform validation entry point.
     * Must never throw.
     */
    DataResult<?> validate();
}