package net.sievert.jolcraft.data.recipe.param.output.custom.particle;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ParticleSpec(
        @NotNull ParticleProducer producer,
        @NotNull ParticleOptions particle
) implements SelfValidating<ParticleSpec>, RegistryIntrospectionSource {

    private record CanonicalRaw(
            @NotNull ResourceLocation id,
            @NotNull ParticleOptions particle
    ) {}

    private record VerboseRaw(
            @NotNull ParticleProducer producer,
            @NotNull ParticleOptions particle
    ) {}

    private static final Codec<CanonicalRaw> CANONICAL_RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    ResourceLocation.CODEC
                            .fieldOf(JolCraftParameterIds.ID)
                            .forGetter(CanonicalRaw::id),

                    net.minecraft.core.particles.ParticleTypes.CODEC
                            .fieldOf(JolCraftParameterIds.PARTICLE)
                            .forGetter(CanonicalRaw::particle)
            ).apply(inst, CanonicalRaw::new));

    private static final Codec<VerboseRaw> VERBOSE_RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    ParticleProducer.CODEC
                            .fieldOf(JolCraftParameterIds.PRODUCER)
                            .forGetter(VerboseRaw::producer),

                    net.minecraft.core.particles.ParticleTypes.CODEC
                            .fieldOf(JolCraftParameterIds.PARTICLE)
                            .forGetter(VerboseRaw::particle)
            ).apply(inst, VerboseRaw::new));

    public static final Codec<ParticleSpec> CODEC =
            ParamCodecContract.create(
                    Codec.either(CANONICAL_RAW_CODEC, VERBOSE_RAW_CODEC),
                    ParticleSpec::fromRaw,
                    ParticleSpec::toRaw
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleSpec> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ParticleProducer.STREAM_CODEC.encode(buf, value.producer());
                        net.minecraft.core.particles.ParticleTypes.STREAM_CODEC.encode(buf, value.particle());
                    },
                    buf -> new ParticleSpec(
                            ParticleProducer.STREAM_CODEC.decode(buf),
                            net.minecraft.core.particles.ParticleTypes.STREAM_CODEC.decode(buf)
                    )
            );

    public static @NotNull DataResult<ParticleSpec> of(
            @NotNull ResourceLocation particleId,
            @NotNull ParticleOptions particle
    ) {
        return ParticleProducer.of(particleId)
                .map(producer -> new ParticleSpec(producer, particle));
    }

    private static @NotNull DataResult<ParticleSpec> fromRaw(
            @NotNull Either<CanonicalRaw, VerboseRaw> raw
    ) {
        if (raw.left().isPresent()) {
            CanonicalRaw canonical = raw.left().orElseThrow();
            return of(canonical.id(), canonical.particle());
        }

        VerboseRaw verbose = raw.right().orElseThrow();
        return DataResult.success(new ParticleSpec(
                verbose.producer(),
                verbose.particle()
        ));
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull ParticleSpec spec) {
        return Either.left(new CanonicalRaw(
                spec.producer().particleId(),
                spec.particle()
        ));
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return producer.introspections();
    }

    @Override
    public @NotNull DataResult<ParticleSpec> validate() {
        DataResult<ParticleProducer> producerValidation = producer.validate();
        if (producerValidation.error().isPresent()) {
            String msg = producerValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "producer invalid: " + msg);
        }

        return SelfValidating.ok(this);
    }
}