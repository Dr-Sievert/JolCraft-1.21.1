package net.sievert.jolcraft.world.recipe.param.introspection;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Atomic single-registry introspection contract.
 *
 * Used by params that contribute exactly one structural registry signal
 * (e.g. single holder, single tag, or mixed(...) for one registry).
 *
 * Design rules:
 * - Implementors provide exactly ONE {@link RegistryIntrospection}.
 * - This interface automatically adapts atomic introspection to the
 *   plural {@link RegistryIntrospectionSource} contract.
 * - No allocation-heavy logic here; just structural reporting.
 *
 * Why this extends {@link RegistryIntrospectionSource}:
 * - Many higher-level aggregators operate only on plural sources.
 * - Atomic types must seamlessly participate in merging without
 *   special-casing or instanceof checks.
 */
public interface RegistryIntrospectable extends RegistryIntrospectionSource {

    /**
     * Returns the single structural registry introspection
     * produced by this atomic param.
     *
     * Never returns null.
     */
    @NotNull RegistryIntrospection introspection();

    /**
     * Adapts the atomic form into the plural contract.
     *
     * Default implementation wraps {@link #introspection()}
     * into a single-element immutable list.
     */
    @Override
    default @NotNull List<RegistryIntrospection> introspections() {
        return asList();
    }

    /**
     * Convenience helper used internally by the default implementation.
     */
    default @NotNull List<RegistryIntrospection> asList() {
        return List.of(introspection());
    }
}