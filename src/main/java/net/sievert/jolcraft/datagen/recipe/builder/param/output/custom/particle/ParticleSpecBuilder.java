package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle;

import com.mojang.serialization.DataResult;
import net.minecraft.core.particles.ParticleOptions;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen-only builder for {@link ParticleSpec}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Missing required fields return {@link DataResult#error}.
 * - Delegates final semantic validation to {@link ParticleSpec#validate()}.
 */
public final class ParticleSpecBuilder {

    private @Nullable ParticleProducer producer;
    private @Nullable ParticleOptions particle;

    private ParticleSpecBuilder() {}

    public static @NotNull ParticleSpecBuilder builder() {
        return new ParticleSpecBuilder();
    }

    public @NotNull ParticleSpecBuilder producer(@Nullable ParticleProducer producer) {
        this.producer = producer;
        return this;
    }

    public @NotNull ParticleSpecBuilder particle(@Nullable ParticleOptions particle) {
        this.particle = particle;
        return this;
    }

    public @NotNull DataResult<ParticleSpec> build() {
        ParticleProducer p = this.producer;
        ParticleOptions o = this.particle;

        if (p == null) {
            return DataResult.error(() -> "Missing required field: 'producer'");
        }

        if (o == null) {
            return DataResult.error(() -> "Missing required field: 'particle'");
        }

        ParticleSpec spec = new ParticleSpec(p, o);
        return spec.validate();
    }
}