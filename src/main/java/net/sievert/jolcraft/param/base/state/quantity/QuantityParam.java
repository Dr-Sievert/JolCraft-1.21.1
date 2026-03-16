package net.sievert.jolcraft.param.base.state.quantity;

import net.sievert.jolcraft.param.base.state.StateParam;

/**
 * Atomic quantity parameter.
 *
 * Represents a numeric magnitude used by the JolCraft param system.
 * Quantity is a specialized form of state whose value is numeric.
 *
 * @param <N> numeric value type
 */
public class QuantityParam<N extends Number> extends StateParam<N> {

    public QuantityParam(N value) {
        super(value);
    }

    public N value() {
        return state;
    }
}