package net.sievert.jolcraft.data.recipe.param.output.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EffectOutput(
        Holder<MobEffect> id,
        int duration,
        int amplifier
) implements OutputParam, SelfValidating<EffectOutput>, RegistryIntrospectable {

    // ---------------------------------------------------------------------
    // Sentinel
    // ---------------------------------------------------------------------

    public static final EffectOutput EMPTY = new EffectOutput(null, 0, 0);

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

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<Holder<MobEffect>> EFFECT_CODEC =
            RegistryFixedCodec.create(Registries.MOB_EFFECT);

    private static final Codec<EffectOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    EFFECT_CODEC.fieldOf(JolCraftParameterIds.ID)
                            .forGetter(EffectOutput::id),

                    Codec.INT.fieldOf(JolCraftParameterIds.DURATION)
                            .forGetter(EffectOutput::duration),

                    Codec.INT.optionalFieldOf(JolCraftParameterIds.AMPLIFIER, 0)
                            .forGetter(EffectOutput::amplifier)
            ).apply(instance, EffectOutput::new));

    public static final Codec<EffectOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)
                                .encode(buf, value.id);

                        buf.writeVarInt(value.duration);
                        buf.writeVarInt(value.amplifier);
                    },
                    (buf) -> {
                        Holder<MobEffect> id =
                                ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)
                                        .decode(buf);

                        int duration = buf.readVarInt();
                        int amplifier = buf.readVarInt();

                        return new EffectOutput(id, duration, amplifier);
                    }
            );

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

        if (this == EMPTY) return List.of();
        if (id == null) return List.of();
        if (duration < 1) return List.of();
        if (amplifier < 0) return List.of();

        return List.of(
                new Output.Effects(
                        List.of(new Output.EffectSpec(id, duration, amplifier))
                )
        );
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EffectOutput> validate() {

        if (this == EMPTY)
            return SelfValidating.ok(this);

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

        return SelfValidating.ok(this);
    }
}