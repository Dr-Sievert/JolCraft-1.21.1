package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle;

import com.mojang.serialization.DataResult;
import net.minecraft.core.particles.ParticleOptions;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleSpec;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen-only builder for {@link ParticleOutput}.
 *
 * Policy:
 * - No throwing, no logging during validated build.
 * - Missing required fields return {@link DataResult#error}.
 * - Delegates final semantic validation to {@link ParticleOutput#validate()}.
 */
public final class ParticleOutputBuilder {

    private @Nullable ParticleSpec spec;
    private @Nullable IntRange count;
    private float speed = 0.0F;

    private ParticleOutputBuilder() {}

    public static @NotNull ParticleOutputBuilder builder() {
        return new ParticleOutputBuilder();
    }

    public @NotNull ParticleOutputBuilder spec(@Nullable ParticleSpec spec) {
        this.spec = spec;
        return this;
    }

    public @NotNull ParticleOutputBuilder particle(
            @Nullable ParticleProducer producer,
            @Nullable ParticleOptions particle
    ) {
        if (producer == null || particle == null) {
            this.spec = null;
            return this;
        }

        this.spec = new ParticleSpec(producer, particle);
        return this;
    }

    public @NotNull ParticleOutputBuilder count(@Nullable IntRange count) {
        this.count = count;
        return this;
    }

    public @NotNull ParticleOutputBuilder countFixed(int value) {
        this.count = IntRange.fixed(value);
        return this;
    }

    public @NotNull ParticleOutputBuilder speed(float speed) {
        this.speed = speed;
        return this;
    }

    public @NotNull DataResult<ParticleOutput> buildValidated() {
        ParticleSpec s = this.spec;
        IntRange c = this.count;
        float sp = this.speed;

        if (s == null) {
            return DataResult.error(() -> "Missing required field: 'spec'");
        }

        if (c == null) {
            return DataResult.error(() -> "Missing required field: 'count'");
        }

        if (!Float.isFinite(sp) || sp < 0.0F) {
            return DataResult.error(() -> "'speed' must be finite and >= 0");
        }

        return new ParticleOutput(s, c, sp).validate();
    }

    public @NotNull ParticleOutput build() {
        return buildValidated().getOrThrow(IllegalStateException::new);
    }
}