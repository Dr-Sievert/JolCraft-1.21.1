package net.sievert.jolcraft.world.recipe.param.output.custom.particle;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public record ParticleSpec(
        @NotNull ParticleProducer producer,
        @NotNull ParticleOptions particle
) implements SelfValidating<ParticleSpec>, RegistryIntrospectionSource {

    public static final Codec<ParticleSpec> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<ParticleSpec, T>> decode(DynamicOps<T> ops, T input) {
            return extractParticleId(ops, input).flatMap(id -> {
                T normalizedInput = normalizeForParticleCodec(ops, input, id);

                return ParticleTypes.CODEC.decode(ops, normalizedInput).flatMap(pair ->
                        ParticleProducer.of(id)
                                .map(producer -> new ParticleSpec(producer, pair.getFirst()))
                                .flatMap(ParticleSpec::validate)
                                .map(spec -> Pair.of(spec, pair.getSecond()))
                );
            });
        }

        @Override
        public <T> DataResult<T> encode(ParticleSpec input, DynamicOps<T> ops, T prefix) {
            if (input.particle() instanceof SimpleParticleType) {
                return ResourceLocation.CODEC.encode(input.producer().particleId(), ops, prefix);
            }

            return ParticleTypes.CODEC.encode(input.particle(), ops, prefix);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleSpec> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ParticleProducer.STREAM_CODEC.encode(buf, value.producer());
                        ParticleTypes.STREAM_CODEC.encode(buf, value.particle());
                    },
                    buf -> new ParticleSpec(
                            ParticleProducer.STREAM_CODEC.decode(buf),
                            ParticleTypes.STREAM_CODEC.decode(buf)
                    )
            );

    public static @NotNull DataResult<ParticleSpec> of(
            @NotNull ResourceLocation particleId,
            @NotNull ParticleOptions particle
    ) {
        return ParticleProducer.of(particleId)
                .map(producer -> new ParticleSpec(producer, particle));
    }

    private static <T> @NotNull DataResult<ResourceLocation> extractParticleId(
            @NotNull DynamicOps<T> ops,
            T input
    ) {
        DataResult<ResourceLocation> direct = ResourceLocation.CODEC.parse(ops, input);
        if (direct.result().isPresent()) {
            return direct;
        }

        return ops.getMap(input).flatMap(map -> {
            T typeValue = map.get(ops.createString(JolCraftParameterIds.TYPE));
            if (typeValue == null) {
                return DataResult.error(() -> "Missing required field: '" + JolCraftParameterIds.TYPE + "'");
            }
            return ResourceLocation.CODEC.parse(ops, typeValue);
        });
    }

    private static <T> @NotNull T normalizeForParticleCodec(
            @NotNull DynamicOps<T> ops,
            T input,
            @NotNull ResourceLocation particleId
    ) {
        if (ops.getMap(input).result().isPresent()) {
            return input;
        }

        T encodedId = ResourceLocation.CODEC.encodeStart(ops, particleId)
                .result()
                .orElseGet(() -> ops.createString(particleId.toString()));

        return ops.createMap(Stream.of(
                Pair.of(ops.createString(JolCraftParameterIds.TYPE), encodedId)
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