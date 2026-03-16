package net.sievert.jolcraft.param.base.adapter;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.identity.IdentityParam;

import java.util.Objects;

/**
 * Utility for normalizing registry-backed objects into canonical
 * identity params.
 */
public final class IdentityAdapter {

    private IdentityAdapter() {}

    public static <T> IdentityParam<T> of(ResourceKey<T> key) {
        return new IdentityParam<>(Objects.requireNonNull(key, JolCraftParameterIds.KEY));
    }

    public static <T> IdentityParam<T> of(Holder<T> holder) {
        Objects.requireNonNull(holder, JolCraftParameterIds.HOLDER);
        return of(holder.unwrapKey().orElseThrow(() -> new IllegalStateException("Expected bound holder with registry key")));
    }
}