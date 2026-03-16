package net.sievert.jolcraft.param.custom.item.component;

import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.project.ParamProjection;

import java.util.Objects;

/**
 * Projects a resolved data component entry into its final projected form.
 *
 * @param <V> component value type
 */
public final class DataComponentProjection<V> implements
        ParamProjection<ResolvedDataComponent<V>, DataComponentProjection.Projected<V>> {

    @Override
    public Projected<V> project(ResolvedDataComponent<V> param) {
        Objects.requireNonNull(param, JolCraftParameterIds.PARAMETER);
        return new Projected<>(param.type(), param.value());
    }

    /**
     * Concrete projected component entry.
     *
     * @param <V> component value type
     */
    public record Projected<V>(
            net.minecraft.core.component.DataComponentType<V> type,
            V value
    ) {

        public Projected {
            Objects.requireNonNull(type, JolCraftParameterIds.TYPE);
        }
    }
}