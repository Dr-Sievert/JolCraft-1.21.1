package net.sievert.jolcraft.data.recipe.param.output.custom.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import org.jetbrains.annotations.NotNull;

/**
 * Structural particle type handle.
 *
 * - Strict, non-null particle type holder.
 * - Introspectable: reports PARTICLE_TYPE registry usage.
 * - Runtime creation of ParticleOptions is owned by ParticleSpec (payload may vary per type).
 */
public record ParticleProducer(
        @NotNull Holder<ParticleType<?>> type
) implements SelfValidating<ParticleProducer>, RegistryIntrospectable {

    private static final Codec<Holder<ParticleType<?>>> PARTICLE_TYPE_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.PARTICLE_TYPE);

    private static final Codec<ParticleProducer> RAW_CODEC =
            PARTICLE_TYPE_HOLDER_CODEC.xmap(ParticleProducer::new, ParticleProducer::type);

    public static final Codec<ParticleProducer> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<ParticleType<?>>> PARTICLE_TYPE_HOLDER_STREAM =
            ByteBufCodecs.holderRegistry(Registries.PARTICLE_TYPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleProducer> STREAM_CODEC =
            PARTICLE_TYPE_HOLDER_STREAM.map(ParticleProducer::new, ParticleProducer::type);


    public static @NotNull DataResult<ParticleProducer> of(@NotNull Holder<ParticleType<?>> type) {
        return DataResult.success(new ParticleProducer(type));
    }

    @Override
    public @NotNull DataResult<ParticleProducer> validate() {
        return DataResult.success(this);
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        return RegistryIntrospection.single(Registries.PARTICLE_TYPE, type);
    }
}