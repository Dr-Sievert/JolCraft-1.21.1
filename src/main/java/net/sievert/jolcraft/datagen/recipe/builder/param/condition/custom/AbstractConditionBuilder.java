package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import net.sievert.jolcraft.world.recipe.param.condition.Condition;

/**
 * Shared base for condition builders that support {@link Condition#invert()}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Owns only the "invert" flag + fluent setter.
 * - Subclasses read {@link #invert()} when assembling their Condition.
 *
 * - Uses self-typing generics to preserve fluent return type.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractConditionBuilder<B extends AbstractConditionBuilder<B>> {

    private boolean invert;

    protected AbstractConditionBuilder() {}

    protected final boolean invert() {
        return invert;
    }

    public final B invert(boolean invert) {
        this.invert = invert;
        return (B) this;
    }
}