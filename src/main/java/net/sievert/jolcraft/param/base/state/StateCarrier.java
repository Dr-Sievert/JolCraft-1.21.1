package net.sievert.jolcraft.param.base.state;

import java.util.List;

/**
 * Contract for objects that carry state entries.
 */
public interface StateCarrier {

    List<? extends StateEntry<?, ?>> states();

}