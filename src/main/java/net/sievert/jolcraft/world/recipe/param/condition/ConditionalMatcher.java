package net.sievert.jolcraft.world.recipe.param.condition;

import net.sievert.jolcraft.param.runtime.WorldContext;
import org.jetbrains.annotations.NotNull;

/**
 * Condition-gated matcher contract.
 *
 * Strict server-only runtime:
 * - Context is always required and never null.
 */
public interface ConditionalMatcher<T> extends ConditionGate {

    boolean matches(@NotNull WorldContext ctx, @NotNull T value);
}