package net.sievert.jolcraft.param.custom.item;

import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.resolve.ResolutionContext;
import net.sievert.jolcraft.param.custom.item.component.DataComponentParam;
import net.sievert.jolcraft.param.custom.item.component.DataComponentResolver;
import net.sievert.jolcraft.param.custom.item.component.ResolvedDataComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Resolves an item stack param into concrete runtime stack parts.
 */
public final class ItemStackResolver {

    private final ItemResolver itemResolver = new ItemResolver();
    private final DataComponentResolver componentResolver = new DataComponentResolver();

    /**
     * Resolves the given stack param.
     *
     * @param param stack param
     * @param ctx resolution context
     * @return resolved item stack
     */
    public ResolvedItemStack resolve(
            ItemParam.Stack param,
            ResolutionContext ctx
    ) {
        Objects.requireNonNull(param, JolCraftParameterIds.PARAMETER);
        Objects.requireNonNull(ctx, JolCraftParameterIds.CONTEXT);

        Item item = itemResolver.resolve(param.item(), ctx);

        List<ResolvedDataComponent<?>> components = new ArrayList<>();

        for (DataComponentParam<?> component : param.state().components()) {
            components.add(resolveComponent(component, ctx));
        }

        return new ResolvedItemStack(
                item,
                param.amount(),
                components
        );
    }

    @SuppressWarnings("unchecked")
    private <V> ResolvedDataComponent<V> resolveComponent(
            DataComponentParam<?> component,
            ResolutionContext ctx
    ) {
        DataComponentParam<V> typed = (DataComponentParam<V>) component;
        return componentResolver.resolve(typed, ctx);
    }
}