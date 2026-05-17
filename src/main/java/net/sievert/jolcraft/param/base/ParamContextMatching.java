package net.sievert.jolcraft.param.base;

import net.sievert.jolcraft.param.runtime.WorldContext;

public interface ParamContextMatching<T> extends ParamMatching<T> {

    boolean matches(WorldContext context, T value);

    @Override
    default boolean matches(T value) {
        throw new UnsupportedOperationException("WorldContext is required for this matcher");
    }
}