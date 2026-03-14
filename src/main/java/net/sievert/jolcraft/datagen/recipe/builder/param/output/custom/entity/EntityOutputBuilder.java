package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntityOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Datagen builder for {@link EntityOutput}.
 *
 * S+:
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

    /**
     * Inline spec builder convenience.
     */
    public @NotNull EntityOutputBuilder result(@NotNull Consumer<EntitySpecBuilder> spec) {
        EntitySpecBuilder builder = EntitySpecBuilder.builder();
        spec.accept(builder);
        this.result = builder.buildOrNull();
        return this;
    }

    public @NotNull EntityOutputBuilder clearResult() {
        this.result = null;
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
        return out.validate();
    }

    public @Nullable EntityOutput buildOrNull() {
        return build().result().orElse(null);
    }
}