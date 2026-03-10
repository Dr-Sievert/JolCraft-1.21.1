package net.sievert.jolcraft.data.recipe.param.output.custom.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record ParticleProducer(
        @NotNull ResourceLocation particleId
) implements SelfValidating<ParticleProducer>, RegistryIntrospectionSource {

    private static final Codec<ParticleProducer> RAW_CODEC =
            ResourceLocation.CODEC.xmap(ParticleProducer::new, ParticleProducer::particleId);

    public static final Codec<ParticleProducer> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleProducer> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> ResourceLocation.STREAM_CODEC.encode(buf, value.particleId()),
                    buf -> new ParticleProducer(ResourceLocation.STREAM_CODEC.decode(buf))
            );

    public static @NotNull DataResult<ParticleProducer> of(@NotNull ResourceLocation particleId) {
        return DataResult.success(new ParticleProducer(particleId));
    }

    public static @NotNull DataResult<ParticleProducer> of(@NotNull Holder<ParticleType<?>> type) {
        return DataResult.success(new ParticleProducer(extractParticleId(type)));
    }

    public @NotNull Optional<Holder.Reference<ParticleType<?>>> resolve(@NotNull RegistryAccess access) {
        return access.lookupOrThrow(Registries.PARTICLE_TYPE)
                .get(ResourceKey.create(Registries.PARTICLE_TYPE, particleId));
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return List.of(
                RegistryIntrospection.empty(Registries.PARTICLE_TYPE)
        );
    }

    @Override
    public @NotNull DataResult<ParticleProducer> validate() {
        return SelfValidating.ok(this);
    }

    private static @NotNull ResourceLocation extractParticleId(@NotNull Holder<ParticleType<?>> type) {
        return type.unwrapKey()
                .map(ResourceKey::location)
                .orElseThrow(() -> new IllegalArgumentException("particle holder has no registry key"));
    }
}