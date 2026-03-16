package net.sievert.jolcraft.param.base.identity;

import net.minecraft.resources.ResourceKey;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;

import java.util.Objects;

/**
 * Atomic identity parameter.
 *
 * Represents the exact identity of a registry-backed object.
 *
 * @param <T> registry entry type
 */
public record IdentityParam<T>(ResourceKey<T> key) {

    public IdentityParam {
        Objects.requireNonNull(key, JolCraftParameterIds.KEY);
    }
}