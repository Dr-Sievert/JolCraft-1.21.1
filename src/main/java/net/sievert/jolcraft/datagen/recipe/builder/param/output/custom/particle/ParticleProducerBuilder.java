package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleProducer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen-only builder for {@link ParticleProducer}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Nulls are rejected (fail-closed).
 * - Delegates final validation to param {@link ParticleProducer#validate()}.
 */
public final class ParticleProducerBuilder {

    private @Nullable Holder<ParticleType<?>> type;

    private ParticleProducerBuilder() {}

    public static @NotNull ParticleProducerBuilder builder() {
        return new ParticleProducerBuilder();
    }

    // ---------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------

    public @NotNull ParticleProducerBuilder type(@NotNull Holder<ParticleType<?>> type) {
        this.type = type;
        return this;
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    public @NotNull DataResult<ParticleProducer> build() {
        if (type == null) {
            return DataResult.error(() -> "Missing required field: 'type'");
        }

        ParticleProducer p = ParticleProducer.of(type);
        return p.validate();
    }
}