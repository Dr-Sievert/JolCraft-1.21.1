package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleSpec;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ParticleOutputBuilder {

    private @Nullable ParticleOptions particle;
    private IntRange count = IntRange.ONE;
    private @Nullable WorldAnchor anchor;
    private ParticleOutput.Vec3f spread = ParticleOutput.Vec3f.zero();
    private float speed = 0.0F;

    private ParticleOutputBuilder() {}

    public static @NotNull ParticleOutputBuilder builder() {
        return new ParticleOutputBuilder();
    }

    public @NotNull ParticleOutputBuilder particle(@Nullable ParticleOptions particle) {
        this.particle = particle;
        return this;
    }

    public @NotNull ParticleOutputBuilder count(@Nullable IntRange count) {
        this.count = count != null ? count : IntRange.ONE;
        return this;
    }

    public @NotNull ParticleOutputBuilder countFixed(int value) {
        this.count = IntRange.fixed(value);
        return this;
    }

    public @NotNull ParticleOutputBuilder anchor(@Nullable WorldAnchor anchor) {
        this.anchor = anchor;
        return this;
    }

    public @NotNull ParticleOutputBuilder spread(@Nullable ParticleOutput.Vec3f spread) {
        this.spread = spread != null ? spread : ParticleOutput.Vec3f.zero();
        return this;
    }

    public @NotNull ParticleOutputBuilder spread(float x, float y, float z) {
        this.spread = new ParticleOutput.Vec3f(x, y, z);
        return this;
    }

    public @NotNull ParticleOutputBuilder speed(float speed) {
        this.speed = speed;
        return this;
    }

    public @NotNull DataResult<ParticleOutput> build() {
        if (particle == null) {
            return DataResult.error(() -> "Missing required field: 'particle'");
        }
        if (count == null) {
            return DataResult.error(() -> "Missing required field: 'count'");
        }
        if (spread == null) {
            return DataResult.error(() -> "Missing required field: 'spread'");
        }
        if (!Float.isFinite(speed) || speed < 0.0F) {
            return DataResult.error(() -> "'speed' must be finite and >= 0");
        }

        ParticleType<?> type = particle.getType();
        Holder<ParticleType<?>> typeHolder = BuiltInRegistries.PARTICLE_TYPE.wrapAsHolder(type);

        ParticleSpec spec = new ParticleSpec(
                ParticleProducer.of(typeHolder),
                particle
        );

        ParticleOutput out = new ParticleOutput(spec, count, anchor, spread, speed);
        return out.validate();
    }
}