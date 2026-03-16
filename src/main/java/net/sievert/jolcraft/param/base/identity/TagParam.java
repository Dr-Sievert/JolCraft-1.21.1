package net.sievert.jolcraft.param.base.identity;

import net.minecraft.tags.TagKey;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;

import java.util.Objects;

/**
 * Atomic tag parameter.
 *
 * Represents a registry tag identity.
 *
 * @param <T> registry entry type
 */
public record TagParam<T>(TagKey<T> tag) {

    public TagParam {
        Objects.requireNonNull(tag, JolCraftParameterIds.TAG);
    }
}