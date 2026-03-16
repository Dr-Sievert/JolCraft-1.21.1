package net.sievert.jolcraft.param.custom.item;

import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.identity.TagParam;
import net.sievert.jolcraft.param.base.resolve.ResolutionContext;

import java.util.List;
import java.util.Objects;

/**
 * Resolves item tag params into concrete runtime items.
 */
public final class ItemTagResolver {

    public List<Item> resolve(
            TagParam<Item> param,
            ResolutionContext ctx
    ) {
        Objects.requireNonNull(param, JolCraftParameterIds.PARAMETER);
        Objects.requireNonNull(ctx, JolCraftParameterIds.CONTEXT);

        return ctx.selectors().resolveAll(param);
    }
}