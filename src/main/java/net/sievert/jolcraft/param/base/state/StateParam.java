package net.sievert.jolcraft.param.base.state;

import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;

import java.util.Objects;

/**
 * Atomic state parameter.
 *
 * Represents configuration or attached data.
 *
 * @param <S> state value type
 */
public class StateParam<S> {

    protected final S state;

    public StateParam(S state) {
        this.state = Objects.requireNonNull(state, JolCraftParameterIds.STATE);
    }

    /**
     * Returns the stored state value.
     *
     * @return state value
     */
    public S value() {
        return state;
    }
}