package net.sievert.jolcraft.data.recipe.param.output.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
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
import net.sievert.jolcraft.data.recipe.param.output.base.Output.EffectTarget;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EffectOutput(
        Holder<MobEffect> id,
        int duration,
        int amplifier,
        EffectTarget target
) implements OutputParam, SelfValidating<EffectOutput>, RegistryIntrospectable {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<Holder<MobEffect>> EFFECT_CODEC =
            RegistryFixedCodec.create(Registries.MOB_EFFECT);

    private static final Codec<EffectTarget> TARGET_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> switch (s.toLowerCase()) {
                        case JolCraftParameterIds.PLAYER -> DataResult.success(EffectTarget.PLAYER);
                        case JolCraftParameterIds.ENTITY -> DataResult.success(EffectTarget.ENTITY);
                        default -> DataResult.error(() -> "Unknown effect target: " + s);
                    },
                    t -> switch (t) {
                        case PLAYER -> JolCraftParameterIds.PLAYER;
                        case ENTITY -> JolCraftParameterIds.ENTITY;
                    }
            );

    private static final Codec<EffectOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    EFFECT_CODEC.fieldOf(JolCraftParameterIds.ID)
                            .forGetter(EffectOutput::id),

                    Codec.INT.fieldOf(JolCraftParameterIds.DURATION)
                            .forGetter(EffectOutput::duration),

                    Codec.INT.optionalFieldOf(JolCraftParameterIds.AMPLIFIER, 0)
                            .forGetter(EffectOutput::amplifier),

                    TARGET_CODEC.fieldOf(JolCraftParameterIds.TARGET)
                            .forGetter(EffectOutput::target)
            ).apply(instance, EffectOutput::new));

    public static final Codec<EffectOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final StreamCodec<ByteBuf, EffectTarget> TARGET_STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(
                    EffectTarget::byId,
                    EffectTarget::getId
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)
                                .encode(buf, value.id);

                        buf.writeVarInt(value.duration);
                        buf.writeVarInt(value.amplifier);
                        TARGET_STREAM_CODEC.encode(buf, value.target);
                    },
                    (buf) -> {
                        Holder<MobEffect> id =
                                ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)
                                        .decode(buf);

                        int duration = buf.readVarInt();
                        int amplifier = buf.readVarInt();
                        EffectTarget target = TARGET_STREAM_CODEC.decode(buf);

                        return new EffectOutput(id, duration, amplifier, target);
                    }
            );

    // ---------------------------------------------------------------------
    // TYPE ID
    // ---------------------------------------------------------------------

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(
                    JolCraftStrings.underscored(
                            JolCraftDictionary.EFFECT,
                            JolCraftDictionary.OUTPUT
                    )
            );

    public static final byte DISC = 4;

    public static final ParamTypeDef<OutputParam> TYPE_DEF = new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull RegistryIntrospection introspection() {
        Holder<MobEffect> h = id;
        return (h != null)
                ? RegistryIntrospection.single(Registries.MOB_EFFECT, h)
                : RegistryIntrospection.mixed(Registries.MOB_EFFECT, 0, false);
    }

    // ---------------------------------------------------------------------
    // OUTPUT PARAM
    // ---------------------------------------------------------------------

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        if (id == null) return List.of();
        if (duration < 1) return List.of();
        if (amplifier < 0) return List.of();
        if (target == null) return List.of();

        return List.of(
                new Output.Effects(
                        List.of(new Output.EffectSpec(id, duration, amplifier, target))
                )
        );
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EffectOutput> validate() {

        if (id == null)
            return SelfValidating.invalid(
                    "Missing required field: '" + JolCraftParameterIds.ID + "'"
            );

        if (duration < 1)
            return SelfValidating.invalid(
                    "'" + JolCraftParameterIds.DURATION + "' must be >= 1"
            );

        if (amplifier < 0)
            return SelfValidating.invalid(
                    "'" + JolCraftParameterIds.AMPLIFIER + "' must be >= 0"
            );

        if (target == null)
            return SelfValidating.invalid(
                    "Missing required field: '" + JolCraftParameterIds.TARGET + "'"
            );

        return SelfValidating.ok(this);
    }
}