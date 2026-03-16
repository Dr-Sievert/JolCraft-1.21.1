package net.sievert.jolcraft.param.base.identity;

/**
 * Basic implementation of IdentitySpecifier.
 *
 * Selects a registry entry either by exact identity or tag.
 *
 * @param <T> registry entry type
 */
public record IdentitySelector<T>(
        IdentityParam<T> identity,
        TagParam<T> tag
) implements IdentitySpecifier<T> {

    public IdentitySelector {
        if ((identity == null) == (tag == null)) {
            throw new IllegalArgumentException("Exactly one of identity or tag must be present");
        }
    }
}