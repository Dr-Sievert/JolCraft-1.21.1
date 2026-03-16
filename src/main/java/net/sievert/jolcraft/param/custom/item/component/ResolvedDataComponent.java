package net.sievert.jolcraft.param.custom.item.component;

import net.minecraft.core.component.DataComponentType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;

import java.util.Objects;

/**
 * Resolved data component entry.
 *
 * @param <V> component value type
 */
public record ResolvedDataComponent<V>(
        DataComponentType<V> type,
        V value
) {

    public ResolvedDataComponent {
        Objects.requireNonNull(type, JolCraftParameterIds.TYPE);
    }
}