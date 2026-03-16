package net.sievert.jolcraft.param.base.project;

/**
 * Contract for a projection-layer interpreter that converts an
 * already-resolved source value into a final runtime object.
 *
 * Projection does not perform abstract identity or selector resolution.
 *
 * @param <P> resolved source type
 * @param <O> projected output type
 */
public interface ParamProjection<P, O> {

    /**
     * Projects the given resolved source value into a final runtime object.
     *
     * @param param resolved source value
     * @return projected value
     */
    O project(P param);
}