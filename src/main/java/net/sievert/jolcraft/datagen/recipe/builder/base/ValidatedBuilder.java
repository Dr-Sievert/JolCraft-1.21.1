package net.sievert.jolcraft.datagen.recipe.builder.base;

import com.mojang.serialization.DataResult;

/**
 * Shared datagen-only validated builder contract.
 *
 * Contract:
 * - Never throws (returns DataResult)
 * - Never saves
 * - Never logs
 * - Does not prescribe *how* validation happens
 */
public interface ValidatedBuilder<T> {

    /**
     * Builds and returns canonical validation result.
     */
    DataResult<T> buildValidated();
}