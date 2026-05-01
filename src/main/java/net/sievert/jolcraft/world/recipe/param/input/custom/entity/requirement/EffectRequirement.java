package net.sievert.jolcraft.world.recipe.param.input.custom.entity.requirement;

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
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import org.jetbrains.annotations.NotNull;

public record EffectRequirement(
        Holder<MobEffect> effect,
        int minAmplifier
) implements SelfValidating<EffectRequirement>, RegistryIntrospectable {

    private static final Codec<Holder<MobEffect>> EFFECT_CODEC =
            RegistryFixedCodec.create(Registries.MOB_EFFECT);

    private record Raw(Holder<MobEffect> effect, int minAmplifier) {}

    private static final Codec<Raw> RAW_CODEC =
            Codec.either(
                    EFFECT_CODEC,
                    RecordCodecBuilder.<Raw>create(instance -> instance.group(
                            EFFECT_CODEC.fieldOf(JolCraftParameterIds.EFFECT).forGetter(Raw::effect),
                            Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN_AMPLIFIER, 0).forGetter(Raw::minAmplifier)
                    ).apply(instance, Raw::new))
            ).xmap(
                    either -> either.map(
                            effect -> new Raw(effect, 0),
                            raw -> raw
                    ),
                    raw -> raw.minAmplifier() == 0
                            ? Either.left(raw.effect())
                            : Either.right(raw)
            );

    public static final Codec<EffectRequirement> CODEC =
            ParamCodecContract.create(
                    RAW_CODEC,
                    raw -> DataResult.success(new EffectRequirement(raw.effect(), raw.minAmplifier())),
                    req -> new Raw(req.effect(), req.minAmplifier())
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> EFFECT_STREAM =
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT);

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> {
                        EFFECT_STREAM.encode(buf, req.effect());
                        buf.writeVarInt(req.minAmplifier());
                    },
                    buf -> new EffectRequirement(
                            EFFECT_STREAM.decode(buf),
                            buf.readVarInt()
                    )
            );

    public EffectRequirement {
        if (effect == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.EFFECT + "'");
        }
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        return RegistryIntrospection.single(Registries.MOB_EFFECT, effect);
    }

    @Override
    public @NotNull DataResult<EffectRequirement> validate() {
        if (minAmplifier < 0) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.MIN_AMPLIFIER + "' must be >= 0");
        }
        return SelfValidating.ok(this);
    }

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        if (minAmplifier < 0) return false;

        MobEffectInstance inst = living.getEffect(effect);
        if (inst == null) return false;

        return inst.getAmplifier() >= minAmplifier;
    }
}