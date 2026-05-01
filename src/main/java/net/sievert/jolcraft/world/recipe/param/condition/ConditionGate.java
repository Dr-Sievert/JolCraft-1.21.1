package net.sievert.jolcraft.world.recipe.param.condition;

import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

/**
 * Top-level condition gate contract.
 *
 * Strict server-only runtime:
 * - Context is always required and never null.
 */
public interface ConditionGate {

    @NotNull Conditions conditions();

    /**
     * Evaluates the top-level gate.
     *
     * - EMPTY => true
     * - Otherwise delegates to {@link Conditions#test(WorldContext)}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean gatePasses(@NotNull WorldContext ctx) {
        return conditions().test(ctx);
    }
}