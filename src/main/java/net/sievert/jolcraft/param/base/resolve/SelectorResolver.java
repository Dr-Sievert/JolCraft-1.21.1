package net.sievert.jolcraft.param.base.resolve;

import net.sievert.jolcraft.param.base.identity.IdentitySelector;
import net.sievert.jolcraft.param.base.identity.TagParam;

import java.util.List;
import java.util.Optional;

/**
 * Resolves selectors into concrete runtime values.
 *
 * Supports both exact identity and tag-based selection.
 *
 * Selector resolution is not the same as projection or runtime
 * materialization of a final object such as an ItemStack.
 */
public interface SelectorResolver {

    /**
     * Resolves the selector to all matching concrete values.
     *
     * @param selector selector to resolve
     * @param <T> resolved value type
     * @return all matching concrete values
     */
    <T> List<T> resolveAll(IdentitySelector<T> selector);

    /**
     * Resolves the selector to a single concrete value when possible.
     *
     * Returns an empty result when the selector does not resolve
     * to exactly one usable concrete value.
     *
     * @param selector selector to resolve
     * @param <T> resolved value type
     * @return single resolved value when available
     */
    <T> Optional<T> resolveOne(IdentitySelector<T> selector);

    /**
     * Resolves the given tag to all matching concrete values.
     *
     * @param tag tag to resolve
     * @param <T> resolved value type
     * @return all matching concrete values for the tag
     */
    <T> List<T> resolveAll(TagParam<T> tag);
}