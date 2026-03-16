package net.sievert.jolcraft.param.custom.item;

import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.resolve.ResolutionContext;

import java.util.List;
import java.util.Objects;

/**
 * Resolves item params into a single concrete runtime item.
 */
public final class ItemResolver {

    /**
     * Resolves the given item param to exactly one concrete item.
     *
     * @param param item param
     * @param ctx resolution context
     * @return resolved concrete item
     */
    public Item resolve(
            ItemParam.Identity param,
            ResolutionContext ctx
    ) {
        Objects.requireNonNull(param, JolCraftParameterIds.PARAMETER);
        Objects.requireNonNull(ctx, JolCraftParameterIds.CONTEXT);

        List<Item> matches = ctx.selectors().resolveAll(param.item());

        if (matches.isEmpty()) {
            throw new IllegalArgumentException(
                    "Could not resolve item selector to any item: " + param.item()
            );
        }

        if (matches.size() > 1) {
            throw new IllegalArgumentException(
                    "Could not resolve item selector to a single item; matched "
                            + matches.size()
                            + " items: "
                            + param.item()
            );
        }

        return matches.getFirst();
    }
}