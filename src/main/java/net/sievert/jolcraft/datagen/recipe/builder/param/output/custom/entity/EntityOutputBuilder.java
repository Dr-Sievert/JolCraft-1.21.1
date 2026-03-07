package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntityOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen builder for {@link EntityOutput}.
 *
 * - no throwing
 * - fail-closed
 * - minimal surface
 */
public final class EntityOutputBuilder {

    private @Nullable EntitySpec result;

    private EntityOutputBuilder() {}

    public static @NotNull EntityOutputBuilder builder() {
        return new EntityOutputBuilder();
    }

    // ---------------------------------------------------------------------
    // Result
    // ---------------------------------------------------------------------

    public @NotNull EntityOutputBuilder result(@Nullable EntitySpec result) {
        this.result = result;
        return this;
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    public @NotNull DataResult<EntityOutput> build() {
        if (result == null) {
            return DataResult.error(() -> "result is required");
        }

        EntityOutput out = new EntityOutput(result);

        DataResult<EntityOutput> v = out.validate();
        var err = v.error();
        return err.<DataResult<EntityOutput>>map(entityOutputError ->
                DataResult.error(entityOutputError::message)).orElseGet(() -> DataResult.success(out));

    }

    public @NotNull EntityOutput buildOrEmpty() {
        return build().result().orElse(EntityOutput.EMPTY);
    }
}