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
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

/**
 * Particle output param (universal).
 *
 * Uses vanilla ParticleOptions dispatch (inside {@link ParticleSpec}), so JSON and network support all particle option types.
 *
 * Introspection:
 * - Reports PARTICLE_TYPE registry usage based on {@link ParticleSpec#producer()}.
 * - No tags possible (ParticleOptions encodes a concrete type).
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record ParticleOutput(
        @NotNull ParticleSpec spec,
        @NotNull IntRange count,
        @Nullable WorldAnchor anchor,
        @NotNull Vec3f spread,
        float speed
) implements OutputParam, SelfValidating<ParticleOutput>, RegistryIntrospectable {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftDictionary.PARTICLE,
                    JolCraftDictionary.OUTPUT
            ));

    public record Vec3f(float x, float y, float z) implements SelfValidating<Vec3f> {

        private static final Codec<Vec3f> RAW_CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        Codec.FLOAT.fieldOf(JolCraftParameterIds.X).forGetter(Vec3f::x),
                        Codec.FLOAT.fieldOf(JolCraftParameterIds.Y).forGetter(Vec3f::y),
                        Codec.FLOAT.fieldOf(JolCraftParameterIds.Z).forGetter(Vec3f::z)
                ).apply(instance, Vec3f::new));

        public static final Codec<Vec3f> CODEC = ParamCodecs.validated(RAW_CODEC);

        public static final StreamCodec<RegistryFriendlyByteBuf, Vec3f> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {
                            buf.writeFloat(v.x);
                            buf.writeFloat(v.y);
                            buf.writeFloat(v.z);
                        },
                        buf -> new Vec3f(buf.readFloat(), buf.readFloat(), buf.readFloat())
                );

        public static @NotNull Vec3f zero() {
            return new Vec3f(0.0F, 0.0F, 0.0F);
        }

        @Override
        public @NotNull DataResult<Vec3f> validate() {
            if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
                return SelfValidating.invalid("Vec3f components must be finite");
            }
            return SelfValidating.ok(this);
        }
    }

    private static final Codec<ParticleOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(

                    ParticleSpec.CODEC.fieldOf(JolCraftParameterIds.PARTICLE)
                            .forGetter(ParticleOutput::spec),

                    IntRange.CODEC.optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.fixed(1))
                            .forGetter(ParticleOutput::count),

                    WorldAnchor.CODEC.optionalFieldOf(JolCraftParameterIds.POSITION)
                            .forGetter(v -> Optional.ofNullable(v.anchor)),

                    Vec3f.CODEC.optionalFieldOf(JolCraftParameterIds.SPREAD, Vec3f.zero())
                            .forGetter(ParticleOutput::spread),

                    Codec.FLOAT.optionalFieldOf(JolCraftParameterIds.SPEED, 0.0F)
                            .forGetter(ParticleOutput::speed)

            ).apply(instance, (spec, count, anchorOpt, spread, speed) ->
                    new ParticleOutput(spec, count, anchorOpt.orElse(null), spread, speed)
            ));

    public static final Codec<ParticleOutput> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ParticleSpec.STREAM_CODEC.encode(buf, value.spec);
                        IntRange.STREAM_CODEC.encode(buf, value.count);
                        WorldAnchor.encodeOptional(buf, value.anchor);
                        Vec3f.STREAM_CODEC.encode(buf, value.spread);
                        buf.writeFloat(value.speed);
                    },
                    buf -> {
                        ParticleSpec spec = ParticleSpec.STREAM_CODEC.decode(buf);
                        IntRange count = IntRange.STREAM_CODEC.decode(buf);
                        WorldAnchor anchor = WorldAnchor.decodeOptional(buf);
                        Vec3f spread = Vec3f.STREAM_CODEC.decode(buf);
                        float speed = buf.readFloat();

                        return new ParticleOutput(spec, count, anchor, spread, speed);
                    }
            );

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        if (!Float.isFinite(speed) || speed < 0.0F) {
            return List.of();
        }

        RandomSource random = ctx.random();

        if (IntRange.validateRange(count).error().isPresent()) {
            return List.of();
        }
        if (spread.validate().error().isPresent()) {
            return List.of();
        }

        int n = Math.max(0, count.roll(random));
        if (n == 0) {
            return List.of();
        }

        return List.of(
                new Output.Particles(List.of(
                        new Output.Particle(
                                spec.particle(),
                                n,
                                anchor,
                                spread.x,
                                spread.y,
                                spread.z,
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
            return DataResult.error(() -> "particle invalid: " + msg);
        }

        var countValid = IntRange.validateRange(count);
        var cErrOpt = countValid.error();
        if (cErrOpt.isPresent()) {
            String msg = cErrOpt.get().message();
            return DataResult.error(() -> "Invalid '" + JolCraftParameterIds.COUNT + "': " + msg);
        }

        var spreadValid = spread.validate();
        var sErrOpt = spreadValid.error();
        if (sErrOpt.isPresent()) {
            String msg = sErrOpt.get().message();
            return DataResult.error(() -> "Invalid '" + JolCraftParameterIds.SPREAD + "': " + msg);
        }

        if (!Float.isFinite(speed) || speed < 0.0F) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.SPEED + "' must be finite and >= 0");
        }

        return SelfValidating.ok(this);
    }
}