package net.sievert.jolcraft.param.base.state;

/**
 * Contract for a single carried state entry.
 *
 * A state entry binds a state identity to a state value.
 *
 * @param <I> state identity type
 * @param <S> state value type
 */
public interface StateEntry<I, S> {

    I identity();

    StateParam<S> state();

}