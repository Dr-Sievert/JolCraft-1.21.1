package net.sievert.jolcraft.param.base.resolve;

import net.sievert.jolcraft.param.base.identity.IdentityParam;

/**
 * Resolves exact param identities into concrete runtime values.
 */
public interface IdentityResolver {

    /**
     * Resolves the given exact identity.
     *
     * @param identity exact param identity
     * @param <T> resolved value type
     * @return resolved concrete value
     */
    <T> T resolve(IdentityParam<T> identity);
}