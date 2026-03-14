package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle;

import com.mojang.serialization.DataResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ParticleSpecBuilder {

    private @Nullable RecipeLookups lookups;
    private @Nullable ParticleProducer producer;
    private @Nullable ParticleOptions particle;

    private ParticleSpecBuilder() {}

    public static @NotNull ParticleSpecBuilder builder() {
        return new ParticleSpecBuilder();
    }

    public @NotNull ParticleSpecBuilder lookups(@Nullable RecipeLookups lookups) {
        this.lookups = lookups;
        return this;
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
        ParticleOptions p = this.particle;
        if (p == null) {
            return DataResult.error(() -> "Missing required field: 'particle'");
        }

        ParticleProducer resolvedProducer = this.producer;
        if (resolvedProducer == null) {
            RecipeLookups recipeLookups = this.lookups;
            if (recipeLookups == null) {
                return DataResult.error(() -> "Missing required field: 'lookups'");
            }

            ResourceLocation id = resolveParticleId(recipeLookups.particles(), p.getType());
            if (id == null) {
                return DataResult.error(() -> "Could not resolve particle id");
            }

            resolvedProducer = new ParticleProducer(id);
        }

        return new ParticleSpec(resolvedProducer, p).validate();
    }

    public @Nullable ParticleSpec buildOrNull() {
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