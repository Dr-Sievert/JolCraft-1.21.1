package net.sievert.jolcraft.param.base.identity;

/**
 * Contract for objects that specify a target by exact identity or tag.
 *
 * @param <T> registry entry type
 */
public interface IdentitySpecifier<T> {

    IdentityParam<T> identity();

    TagParam<T> tag();

}