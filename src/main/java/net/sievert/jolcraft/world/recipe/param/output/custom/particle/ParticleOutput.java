package net.sievert.jolcraft.world.recipe.param.output.custom.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.world.recipe.param.quantity.DoubleRange;
import net.sievert.jolcraft.world.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record ParticleOutput(
        @NotNull ParticleSpec spec,
        @NotNull IntRange count,
        @NotNull DoubleRange speed,
        @NotNull DoubleRange offsetX,
        @NotNull DoubleRange offsetY,
        @NotNull DoubleRange offsetZ,
        @NotNull DoubleRange spreadX,
        @NotNull DoubleRange spreadY,
        @NotNull DoubleRange spreadZ
) implements OutputParam, SelfValidating<ParticleOutput>, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftDictionary.PARTICLE,
                    JolCraftDictionary.OUTPUT
            ));

    public static final byte DISC = 6;

    private record Raw(
            @NotNull ParticleSpec particle,
            @NotNull IntRange count,
            @NotNull DoubleRange speed,
            @NotNull DoubleRange offsetX,
            @NotNull DoubleRange offsetY,
            @NotNull DoubleRange offsetZ,
            @NotNull DoubleRange spreadX,
            @NotNull DoubleRange spreadY,
            @NotNull DoubleRange spreadZ
    ) {}

    private static final Codec<Raw> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ParticleSpec.CODEC
                            .fieldOf(JolCraftParameterIds.PARTICLE)
                            .forGetter(Raw::particle),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(Raw::count),

                    DoubleRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.SPEED, DoubleRange.ZERO)
                            .forGetter(Raw::speed),

                    DoubleRange.CODEC
                            .optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "x"), DoubleRange.ZERO)
                            .forGetter(Raw::offsetX),

                    DoubleRange.CODEC
                            .optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "y"), DoubleRange.ZERO)
                            .forGetter(Raw::offsetY),

                    DoubleRange.CODEC
                            .optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "z"), DoubleRange.ZERO)
                            .forGetter(Raw::offsetZ),

                    DoubleRange.CODEC
                            .optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.SPREAD, "x"), DoubleRange.ZERO)
                            .forGetter(Raw::spreadX),

                    DoubleRange.CODEC
                            .optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.SPREAD, "y"), DoubleRange.ZERO)
                            .forGetter(Raw::spreadY),

                    DoubleRange.CODEC
                            .optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.SPREAD, "z"), DoubleRange.ZERO)
                            .forGetter(Raw::spreadZ)
            ).apply(instance, Raw::new));

    public static final Codec<ParticleOutput> CODEC =
            ParamCodecs.validated(
                    RAW_CODEC.xmap(
                            (Raw raw) -> new ParticleOutput(
                                    raw.particle(),
                                    raw.count(),
                                    raw.speed(),
                                    raw.offsetX(),
                                    raw.offsetY(),
                                    raw.offsetZ(),
                                    raw.spreadX(),
                                    raw.spreadY(),
                                    raw.spreadZ()
                            ),
                            (ParticleOutput out) -> new Raw(
                                    out.spec(),
                                    out.count(),
                                    out.speed(),
                                    out.offsetX(),
                                    out.offsetY(),
                                    out.offsetZ(),
                                    out.spreadX(),
                                    out.spreadY(),
                                    out.spreadZ()
                            )
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ParticleSpec.STREAM_CODEC.encode(buf, value.spec());
                        IntRange.STREAM_CODEC.encode(buf, value.count());
                        DoubleRange.STREAM_CODEC.encode(buf, value.speed());
                        DoubleRange.STREAM_CODEC.encode(buf, value.offsetX());
                        DoubleRange.STREAM_CODEC.encode(buf, value.offsetY());
                        DoubleRange.STREAM_CODEC.encode(buf, value.offsetZ());
                        DoubleRange.STREAM_CODEC.encode(buf, value.spreadX());
                        DoubleRange.STREAM_CODEC.encode(buf, value.spreadY());
                        DoubleRange.STREAM_CODEC.encode(buf, value.spreadZ());
                    },
                    buf -> new ParticleOutput(
                            ParticleSpec.STREAM_CODEC.decode(buf),
                            IntRange.STREAM_CODEC.decode(buf),
                            DoubleRange.STREAM_CODEC.decode(buf),
                            DoubleRange.STREAM_CODEC.decode(buf),
                            DoubleRange.STREAM_CODEC.decode(buf),
                            DoubleRange.STREAM_CODEC.decode(buf),
                            DoubleRange.STREAM_CODEC.decode(buf),
                            DoubleRange.STREAM_CODEC.decode(buf),
                            DoubleRange.STREAM_CODEC.decode(buf)
                    )
            );

    public static final ParamTypeDef<OutputParam> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

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
        int rolledCount = count.isFixed() ? count.min() : count.roll(ctx.random());
        if (rolledCount <= 0) {
            return List.of();
        }

        double rolledSpeed = speed.isFixed() ? speed.min() : speed.roll(ctx.random());
        double rolledOffsetX = offsetX.isFixed() ? offsetX.min() : offsetX.roll(ctx.random());
        double rolledOffsetY = offsetY.isFixed() ? offsetY.min() : offsetY.roll(ctx.random());
        double rolledOffsetZ = offsetZ.isFixed() ? offsetZ.min() : offsetZ.roll(ctx.random());
        double rolledSpreadX = spreadX.isFixed() ? spreadX.min() : spreadX.roll(ctx.random());
        double rolledSpreadY = spreadY.isFixed() ? spreadY.min() : spreadY.roll(ctx.random());
        double rolledSpreadZ = spreadZ.isFixed() ? spreadZ.min() : spreadZ.roll(ctx.random());

        return List.of(
                new Output.Particles(List.of(
                        new Output.Particle(
                                spec.particle(),
                                rolledCount,
                                rolledOffsetX,
                                rolledOffsetY,
                                rolledOffsetZ,
                                rolledSpreadX,
                                rolledSpreadY,
                                rolledSpreadZ,
                                rolledSpeed
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

        DataResult<DoubleRange> speedValidation = DoubleRange.validateRange(speed);
        if (speedValidation.error().isPresent()) {
            String msg = speedValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid '" + JolCraftParameterIds.SPEED + "': " + msg);
        }

        DataResult<DoubleRange> offsetXValidation = DoubleRange.validateRange(offsetX);
        if (offsetXValidation.error().isPresent()) {
            String msg = offsetXValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid 'offset_x': " + msg);
        }

        DataResult<DoubleRange> offsetYValidation = DoubleRange.validateRange(offsetY);
        if (offsetYValidation.error().isPresent()) {
            String msg = offsetYValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid 'offset_y': " + msg);
        }

        DataResult<DoubleRange> offsetZValidation = DoubleRange.validateRange(offsetZ);
        if (offsetZValidation.error().isPresent()) {
            String msg = offsetZValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid 'offset_z': " + msg);
        }

        DataResult<DoubleRange> spreadXValidation = DoubleRange.validateRange(spreadX);
        if (spreadXValidation.error().isPresent()) {
            String msg = spreadXValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid 'spread_x': " + msg);
        }

        DataResult<DoubleRange> spreadYValidation = DoubleRange.validateRange(spreadY);
        if (spreadYValidation.error().isPresent()) {
            String msg = spreadYValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid 'spread_y': " + msg);
        }

        DataResult<DoubleRange> spreadZValidation = DoubleRange.validateRange(spreadZ);
        if (spreadZValidation.error().isPresent()) {
            String msg = spreadZValidation.error().map(DataResult.Error::message).orElse("");
            return DataResult.error(() -> "invalid 'spread_z': " + msg);
        }

        if (speed.min() < 0.0D || speed.max() < 0.0D) {
            return DataResult.error(() ->
                    "invalid '" + JolCraftParameterIds.SPEED + "': must be >= 0");
        }

        if (spreadX.min() < 0.0D || spreadX.max() < 0.0D) {
            return DataResult.error(() -> "invalid 'spread_x': must be >= 0");
        }

        if (spreadY.min() < 0.0D || spreadY.max() < 0.0D) {
            return DataResult.error(() -> "invalid 'spread_y': must be >= 0");
        }

        if (spreadZ.min() < 0.0D || spreadZ.max() < 0.0D) {
            return DataResult.error(() -> "invalid 'spread_z': must be >= 0");
        }

        return SelfValidating.ok(this);
    }
}