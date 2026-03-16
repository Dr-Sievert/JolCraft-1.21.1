package net.sievert.jolcraft.param.custom.item;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.project.ParamProjection;
import net.sievert.jolcraft.param.custom.item.component.DataComponentProjection;
import net.sievert.jolcraft.param.custom.item.component.ResolvedDataComponent;

import java.util.Objects;

/**
 * Projects a resolved item stack into a real runtime ItemStack.
 */
public final class ItemStackProjection implements
        ParamProjection<ResolvedItemStack, ItemStack> {

    private final DataComponentProjection<Object> componentProjection =
            new DataComponentProjection<>();

    @Override
    public ItemStack project(ResolvedItemStack param) {
        Objects.requireNonNull(param, JolCraftParameterIds.PARAMETER);

        ItemStack stack = new ItemStack(param.item(), param.count());

        for (ResolvedDataComponent<?> component : param.components()) {
            applyProjectedComponent(stack, component);
        }

        return stack;
    }

    @SuppressWarnings("unchecked")
    private void applyProjectedComponent(
            ItemStack stack,
            ResolvedDataComponent<?> component
    ) {
        ResolvedDataComponent<Object> typedResolved =
                (ResolvedDataComponent<Object>) component;

        DataComponentProjection.Projected<Object> projected =
                componentProjection.project(typedResolved);

        stack.set(projected.type(), projected.value());
    }
}