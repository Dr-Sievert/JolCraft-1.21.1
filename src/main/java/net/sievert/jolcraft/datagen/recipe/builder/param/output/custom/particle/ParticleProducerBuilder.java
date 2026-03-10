package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    private @Nullable ResourceLocation particleId;

    private ParticleProducerBuilder() {}

    public static @NotNull ParticleProducerBuilder builder() {
        return new ParticleProducerBuilder();
    }

    // ---------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------

    public @NotNull ParticleProducerBuilder id(@NotNull ResourceLocation particleId) {
        this.particleId = particleId;
        return this;
    }

    public @NotNull DataResult<ParticleProducerBuilder> type(@NotNull Holder<ParticleType<?>> type) {
        ResourceLocation id = extractId(type);
        if (id == null) {
            return DataResult.error(() -> "Particle holder has no registry key");
        }

        this.particleId = id;
        return DataResult.success(this);
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    public @NotNull DataResult<ParticleProducer> build() {
        ResourceLocation id = this.particleId;

        if (id == null) {
            return DataResult.error(() -> "Missing required field: 'id'");
        }

        ParticleProducer producer = new ParticleProducer(id);
        return producer.validate();
    }

    private static @Nullable ResourceLocation extractId(@NotNull Holder<ParticleType<?>> type) {
        return type.unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);
    }
}