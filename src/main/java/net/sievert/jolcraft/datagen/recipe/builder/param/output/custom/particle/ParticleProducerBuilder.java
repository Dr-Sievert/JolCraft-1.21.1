package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleProducer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ParticleProducerBuilder {

    private @Nullable RecipeLookups lookups;
    private @Nullable ResourceLocation particleId;

    private ParticleProducerBuilder() {}

    public static @NotNull ParticleProducerBuilder builder() {
        return new ParticleProducerBuilder();
    }

    public @NotNull ParticleProducerBuilder lookups(@Nullable RecipeLookups lookups) {
        this.lookups = lookups;
        return this;
    }

    public @NotNull ParticleProducerBuilder particle(@Nullable ParticleOptions particle) {
        if (particle == null || lookups == null) {
            this.particleId = null;
            return this;
        }

        this.particleId = resolveParticleId(lookups.particles(), particle.getType());
        return this;
    }

    public @NotNull ParticleProducerBuilder type(@Nullable Holder<ParticleType<?>> type) {
        if (type == null) {
            this.particleId = null;
            return this;
        }

        this.particleId = type.unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

        return this;
    }

    public @NotNull DataResult<ParticleProducer> build() {
        if (particleId == null) {
            return DataResult.error(() -> "Missing required field: 'particle'");
        }

        return new ParticleProducer(particleId).validate();
    }

    public @Nullable ParticleProducer buildOrNull() {
        return build().result().orElse(null);
    }

    private static @Nullable ResourceLocation resolveParticleId(
            @NotNull HolderLookup.RegistryLookup<ParticleType<?>> registry,
            @NotNull ParticleType<?> type
    ) {
        return registry.listElements()
                .filter(ref -> ref.value() == type)
                .findFirst()
                .flatMap(ref -> ref.unwrapKey().map(ResourceKey::location))
                .orElse(null);
    }
}