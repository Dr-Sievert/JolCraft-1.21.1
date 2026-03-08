package net.sievert.jolcraft.data.recipe.param.output.custom.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import org.jetbrains.annotations.NotNull;

/**
 * Universal particle payload + structural type handle.
 *
 * Rules:
 * - producer REQUIRED
 * - particle REQUIRED
 * - producer.type() MUST match particle.getType()
 */
public record ParticleSpec(
        @NotNull ParticleProducer producer,
        @NotNull ParticleOptions particle
) implements SelfValidating<ParticleSpec>, RegistryIntrospectable {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<ParticleSpec> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    ParticleProducer.CODEC
                            .fieldOf(JolCraftParameterIds.PRODUCER)
                            .forGetter(ParticleSpec::producer),

                    net.minecraft.core.particles.ParticleTypes.CODEC
                            .fieldOf(JolCraftParameterIds.PARTICLE)
                            .forGetter(ParticleSpec::particle)
            ).apply(inst, ParticleSpec::new));

    public static final Codec<ParticleSpec> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleSpec> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ParticleProducer.STREAM_CODEC.encode(buf, value.producer);
                        net.minecraft.core.particles.ParticleTypes.STREAM_CODEC.encode(buf, value.particle);
                    },
                    buf -> new ParticleSpec(
                            ParticleProducer.STREAM_CODEC.decode(buf),
                            net.minecraft.core.particles.ParticleTypes.STREAM_CODEC.decode(buf)
                    )
            );

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull RegistryIntrospection introspection() {
        return producer.introspection();
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<ParticleSpec> validate() {
        DataResult<ParticleProducer> pv = producer.validate();
        if (pv.error().isPresent()) {
            String msg = pv.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "producer invalid: " + msg);
        }

        Holder<ParticleType<?>> holder = producer.type();
        ParticleType<?> payloadType = particle.getType();

        if (holder.value() != payloadType) {
            return DataResult.error(() ->
                    "particle type mismatch between producer and particle payload");
        }

        return SelfValidating.ok(this);
    }
}