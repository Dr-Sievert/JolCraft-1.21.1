package net.sievert.jolcraft.param.custom.entity.input.requirement;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;

public record EffectRequirement(
        Holder<MobEffect> effect,
        int minAmplifier
) implements ParamData<EffectRequirement> {

    private static final Codec<Holder<MobEffect>> EFFECT_CODEC =
            RegistryFixedCodec.create(Registries.MOB_EFFECT);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> EFFECT_STREAM =
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT);

    private record Raw(Holder<MobEffect> effect, int minAmplifier) {}

    private static final Codec<Raw> RAW_CODEC =
            ParamCodecs.either(
                    EFFECT_CODEC,
                    RecordCodecBuilder.<Raw>create(inst -> inst.group(
                            EFFECT_CODEC.fieldOf(JolCraftParameterIds.EFFECT)
                                    .forGetter(Raw::effect),
                            Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN_AMPLIFIER, 0)
                                    .forGetter(Raw::minAmplifier)
                    ).apply(inst, Raw::new)),
                    either -> ParamValidations.ok(either.map(effect -> new Raw(effect, 0), raw -> raw)),
                    raw -> ParamValidations.ok(raw.minAmplifier() == 0
                            ? Either.left(raw.effect())
                            : Either.right(raw))
            );

    public static final Codec<EffectRequirement> CODEC =
            ParamCodecs.validated(
                    RAW_CODEC.xmap(
                            raw -> new EffectRequirement(raw.effect(), raw.minAmplifier()),
                            req -> new Raw(req.effect(), req.minAmplifier())
                    ),
                    EffectRequirement::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectRequirement> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    EFFECT_STREAM,
                    EffectRequirement::effect,
                    ByteBufCodecs.VAR_INT,
                    EffectRequirement::minAmplifier,
                    EffectRequirement::new
            ), EffectRequirement::validate);

    public EffectRequirement {
        if (effect == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.EFFECT + "'");
        }
    }

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        if (minAmplifier < 0) return false;

        MobEffectInstance inst = living.getEffect(effect);
        return inst != null && inst.getAmplifier() >= minAmplifier;
    }

    @Override
    public DataResult<EffectRequirement> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.notNull(this, effect, JolCraftParameterIds.EFFECT),
                () -> ParamValidations.nonNegative(this, minAmplifier, JolCraftParameterIds.MIN_AMPLIFIER)
        );
    }

    @Override
    public Codec<EffectRequirement> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EffectRequirement> streamCodec() {
        return STREAM_CODEC;
    }
}