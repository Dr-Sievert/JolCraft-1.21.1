package net.sievert.jolcraft.param.custom.item.component;

import net.minecraft.core.component.DataComponentType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.identity.IdentityParam;
import net.sievert.jolcraft.param.base.resolve.IdentityResolver;
import net.sievert.jolcraft.param.base.resolve.ResolutionContext;

import java.util.Objects;

/**
 * Resolves a data component param into a concrete runtime component entry.
 */
public final class DataComponentResolver {

    /**
     * Resolves the given component param.
     *
     * @param param component param
     * @param ctx resolution context
     * @param <V> component value type
     * @return resolved component entry
     */
    public <V> ResolvedDataComponent<V> resolve(
            DataComponentParam<V> param,
            ResolutionContext ctx
    ) {
        Objects.requireNonNull(param, JolCraftParameterIds.PARAMETER);
        Objects.requireNonNull(ctx, JolCraftParameterIds.CONTEXT);

        IdentityParam<DataComponentType<V>> identity = param.identity();
        IdentityResolver identities = ctx.identities();
        DataComponentType<V> type = identities.resolve(identity);

        return new ResolvedDataComponent<>(type, param.value());
    }
}