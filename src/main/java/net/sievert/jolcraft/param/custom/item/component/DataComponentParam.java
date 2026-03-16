package net.sievert.jolcraft.param.custom.item.component;

import net.minecraft.core.component.DataComponentType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.identity.IdentityParam;
import net.sievert.jolcraft.param.base.state.StateCarrier;
import net.sievert.jolcraft.param.base.state.StateEntry;
import net.sievert.jolcraft.param.base.state.StateParam;

import java.util.List;
import java.util.Objects;

/**
 * Param describing a single data component entry.
 *
 * @param <V> component value type
 */
public record DataComponentParam<V>(
        IdentityParam<DataComponentType<V>> identity,
        StateParam<V> state
) implements
        StateEntry<IdentityParam<DataComponentType<V>>, V>,
        StateCarrier {

    public DataComponentParam {
        Objects.requireNonNull(identity, JolCraftParameterIds.IDENTITY);
        Objects.requireNonNull(state, JolCraftParameterIds.STATE);
    }

    public V value() {
        return state.value();
    }

    @Override
    public List<? extends StateEntry<?, ?>> states() {
        return List.of(this);
    }
}