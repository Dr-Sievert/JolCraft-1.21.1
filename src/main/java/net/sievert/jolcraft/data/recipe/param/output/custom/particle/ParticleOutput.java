package net.sievert.jolcraft.data.recipe.param.output.custom.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Particle output param (universal).
 *
 * Uses vanilla ParticleOptions dispatch (inside {@link ParticleSpec}), so JSON and network
 * support all particle option types.
 *
 * Introspection:
 * - Reports PARTICLE_TYPE registry usage based on {@link ParticleSpec#producer()}.
 * - No tags possible (ParticleOptions encodes a concrete type).
 *
 * Runtime position/spread are caller-owned, not stored in the param.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record ParticleOutput(
        @NotNull ParticleSpec spec,
        @NotNull IntRange count,
        float speed
) implements OutputParam, SelfValidating<ParticleOutput>, RegistryIntrospectable {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftDictionary.PARTICLE,
                    JolCraftDictionary.OUTPUT
            ));

    public static final byte DISC = 6;

    private static final Codec<ParticleOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ParticleSpec.CODEC
                            .fieldOf(JolCraftParameterIds.PARTICLE)
                            .forGetter(ParticleOutput::spec),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(ParticleOutput::count),

                    Codec.FLOAT
                            .optionalFieldOf(JolCraftParameterIds.SPEED, 0.0F)
                            .forGetter(ParticleOutput::speed)
            ).apply(instance, ParticleOutput::new));

    public static final Codec<ParticleOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ParticleSpec.STREAM_CODEC.encode(buf, value.spec);
                        IntRange.STREAM_CODEC.encode(buf, value.count);
                        buf.writeFloat(value.speed);
                    },
                    buf -> new ParticleOutput(
                            ParticleSpec.STREAM_CODEC.decode(buf),
                            IntRange.STREAM_CODEC.decode(buf),
                            buf.readFloat()
                    )
            );

    public static final ParamTypeDef<OutputParam> TYPE_DEF = new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        if (validate().error().isPresent()) {
            return List.of();
        }

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
    public @NotNull RegistryIntrospection introspection() {
        return spec.introspection();
    }

    @Override
    public @NotNull DataResult<ParticleOutput> validate() {
        var sv = spec.validate();
        if (sv.error().isPresent()) {
            String msg = sv.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid particle spec: " + msg);
        }

        var countValid = IntRange.validateRange(count);
        if (countValid.error().isPresent()) {
            String msg = countValid.error().get().message();
            return DataResult.error(() -> "invalid '" + JolCraftParameterIds.COUNT + "': " + msg);
        }

        if (!Float.isFinite(speed) || speed < 0.0F) {
            return DataResult.error(() ->
                    "invalid '" + JolCraftParameterIds.SPEED + "': must be finite and >= 0");
        }

        return SelfValidating.ok(this);
    }
}