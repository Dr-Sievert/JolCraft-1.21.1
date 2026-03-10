package net.sievert.jolcraft.data.recipe.param.output.custom.particle;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record ParticleOutput(
        @NotNull ParticleSpec spec,
        @NotNull IntRange count,
        float speed
) implements OutputParam, SelfValidating<ParticleOutput>, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftDictionary.PARTICLE,
                    JolCraftDictionary.OUTPUT
            ));

    public static final byte DISC = 6;

    private record CanonicalRaw(
            @NotNull ResourceLocation id,
            @NotNull ParticleOptions particle,
            @NotNull IntRange count,
            float speed
    ) {}

    private record VerboseRaw(
            @NotNull ParticleSpec particle,
            @NotNull IntRange count,
            float speed
    ) {}

    private static final Codec<CanonicalRaw> CANONICAL_RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC
                            .fieldOf(JolCraftParameterIds.ID)
                            .forGetter(CanonicalRaw::id),

                    net.minecraft.core.particles.ParticleTypes.CODEC
                            .fieldOf(JolCraftParameterIds.PARTICLE)
                            .forGetter(CanonicalRaw::particle),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(CanonicalRaw::count),

                    Codec.FLOAT
                            .optionalFieldOf(JolCraftParameterIds.SPEED, 0.0F)
                            .forGetter(CanonicalRaw::speed)
            ).apply(instance, CanonicalRaw::new));

    private static final Codec<VerboseRaw> VERBOSE_RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ParticleSpec.CODEC
                            .fieldOf(JolCraftParameterIds.PARTICLE)
                            .forGetter(VerboseRaw::particle),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(VerboseRaw::count),

                    Codec.FLOAT
                            .optionalFieldOf(JolCraftParameterIds.SPEED, 0.0F)
                            .forGetter(VerboseRaw::speed)
            ).apply(instance, VerboseRaw::new));

    public static final Codec<ParticleOutput> CODEC =
            ParamCodecContract.create(
                    Codec.either(CANONICAL_RAW_CODEC, VERBOSE_RAW_CODEC),
                    ParticleOutput::fromRaw,
                    ParticleOutput::toRaw
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ParticleSpec.STREAM_CODEC.encode(buf, value.spec());
                        IntRange.STREAM_CODEC.encode(buf, value.count());
                        buf.writeFloat(value.speed());
                    },
                    buf -> new ParticleOutput(
                            ParticleSpec.STREAM_CODEC.decode(buf),
                            IntRange.STREAM_CODEC.decode(buf),
                            buf.readFloat()
                    )
            );

    public static final ParamTypeDef<OutputParam> TYPE_DEF = new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static @NotNull DataResult<ParticleOutput> fromRaw(
            @NotNull Either<CanonicalRaw, VerboseRaw> raw
    ) {
        if (raw.left().isPresent()) {
            CanonicalRaw canonical = raw.left().orElseThrow();
            return ParticleSpec.of(canonical.id(), canonical.particle())
                    .map(spec -> new ParticleOutput(spec, canonical.count(), canonical.speed()));
        }

        VerboseRaw verbose = raw.right().orElseThrow();
        return DataResult.success(new ParticleOutput(
                verbose.particle(),
                verbose.count(),
                verbose.speed()
        ));
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull ParticleOutput output) {
        return Either.left(new CanonicalRaw(
                output.spec().producer().particleId(),
                output.spec().particle(),
                output.count(),
                output.speed()
        ));
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return spec.introspections();
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        RandomSource random = ctx.random();
        int rolled = count.roll(random);
        if (rolled <= 0) {
            return List.of();
        }

        return List.of(
                new Output.Particles(List.of(
                        new Output.Particle(
                                spec.particle(),
                                rolled,
                                speed
                        )
                ))
        );
    }

    @Override
    public @NotNull DataResult<ParticleOutput> validate() {
        DataResult<ParticleSpec> specValidation = spec.validate();
        if (specValidation.error().isPresent()) {
            String msg = specValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid particle spec: " + msg);
        }

        DataResult<IntRange> countValidation = IntRange.validateRange(count);
        if (countValidation.error().isPresent()) {
            String msg = countValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid '" + JolCraftParameterIds.COUNT + "': " + msg);
        }

        if (!Float.isFinite(speed) || speed < 0.0F) {
            return DataResult.error(() ->
                    "invalid '" + JolCraftParameterIds.SPEED + "': must be finite and >= 0");
        }

        return SelfValidating.ok(this);
    }
}