package net.sievert.jolcraft.param.base.resolve;

/**
 * Shared context for resolve-layer interpreters.
 *
 * Provides access to generic resolution services used to turn
 * abstract param identities and selectors into concrete values.
 */
public interface ResolutionContext {

    /**
     * Returns the exact identity resolver.
     *
     * @return identity resolver
     */
    IdentityResolver identities();

    /**
     * Returns the selector resolver.
     *
     * @return selector resolver
     */
    SelectorResolver selectors();
}