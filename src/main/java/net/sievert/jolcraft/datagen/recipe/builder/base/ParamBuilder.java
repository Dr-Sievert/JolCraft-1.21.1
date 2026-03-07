package net.sievert.jolcraft.datagen.recipe.builder.base;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;

/**
 * Param-specific builder contract.
 *
 * Param semantics:
 * - build() assembles the param object (no domain checks)
 * - buildValidated() may delegate to Validatable#validate()
 */
public interface ParamBuilder<T extends SelfValidating<T>> extends ValidatedBuilder<T> {

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
        return built.validate();
    }
}