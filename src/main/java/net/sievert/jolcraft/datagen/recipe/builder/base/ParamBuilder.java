package net.sievert.jolcraft.datagen.recipe.builder.base;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.datagen.base.builder.JolCraftValidatedBuilder;
import org.jetbrains.annotations.NotNull;

public interface ParamBuilder<T> extends JolCraftValidatedBuilder<T> {

    T build();

    @Override
    default @NotNull DataResult<T> buildValidated() {
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