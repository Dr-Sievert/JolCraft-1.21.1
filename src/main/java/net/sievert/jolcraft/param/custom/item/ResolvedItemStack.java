package net.sievert.jolcraft.param.custom.item;

import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.custom.item.component.ResolvedDataComponent;

import java.util.List;
import java.util.Objects;

/**
 * Resolved item stack param.
 */
public record ResolvedItemStack(
        Item item,
        int count,
        List<ResolvedDataComponent<?>> components
) {

    public ResolvedItemStack {
        Objects.requireNonNull(item, JolCraftParameterIds.ITEM);
        Objects.requireNonNull(components, JolCraftParameterIds.COMPONENTS);
        components = List.copyOf(components);
    }
}