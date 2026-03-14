package net.sievert.jolcraft.datagen.recipe.builder.base;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;

/**
 * Param-specific builder contract.
 *
 * Build targets may be:
 * - concrete self-validating params
 * - polymorphic param interfaces such as OutputParam
 *
 * Contract:
 * - build() assembles the value
 * - buildValidated() validates when the built value implements SelfValidating
 */
public interface ParamBuilder<T> extends ValidatedBuilder<T> {

    /**
     * Assemble without validation.
     */
    T build();

    @Override
    default DataResult<T> buildValidated() {
        T built = build();

        if (built == null) {
            return DataResult.error(() -> "builder produced null");
        }

        if (built instanceof SelfValidating<?> validating) {
            DataResult<?> validated = validating.validate();

            if (validated.error().isPresent()) {
                return DataResult.error(() ->
                        validated.error().map(DataResult.Error::message).orElse("invalid")
                );
            }
        }

        return DataResult.success(built);
    }
}