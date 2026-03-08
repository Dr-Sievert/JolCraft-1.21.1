package net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement;

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
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import org.jetbrains.annotations.NotNull;

public record EffectRequirement(
        Holder<MobEffect> effect,
        int minAmplifier
) implements SelfValidating<EffectRequirement>, RegistryIntrospectable {

    private static final Codec<Holder<MobEffect>> EFFECT_CODEC =
            RegistryFixedCodec.create(Registries.MOB_EFFECT);

    private static final Codec<EffectRequirement> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    EFFECT_CODEC.fieldOf(JolCraftParameterIds.EFFECT).forGetter(EffectRequirement::effect),
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN_AMPLIFIER, 0).forGetter(EffectRequirement::minAmplifier)
            ).apply(instance, EffectRequirement::new));

    public static final Codec<EffectRequirement> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> EFFECT_STREAM =
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT);

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> {
                        Holder<MobEffect> eff = req.effect;
                        boolean hasEffect = eff != null;
                        buf.writeBoolean(hasEffect);
                        if (hasEffect) {
                            EFFECT_STREAM.encode(buf, eff);
                        }

                        buf.writeVarInt(Math.max(0, req.minAmplifier));
                    },
                    buf -> {
                        Holder<MobEffect> effect = null;
                        boolean hasEffect = buf.readBoolean();
                        if (hasEffect) {
                            effect = EFFECT_STREAM.decode(buf);
                        }

                        int minAmplifier = buf.readVarInt();
                        if (minAmplifier < 0) minAmplifier = 0;

                        return new EffectRequirement(effect, minAmplifier);
                    }
            );

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull RegistryIntrospection introspection() {
        return effect == null
                ? RegistryIntrospection.mixed(Registries.MOB_EFFECT, 0, false)
                : RegistryIntrospection.single(Registries.MOB_EFFECT, effect);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EffectRequirement> validate() {
        if (effect == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.EFFECT + "'");
        }
        if (minAmplifier < 0) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.MIN_AMPLIFIER + "' must be >= 0");
        }
        return SelfValidating.ok(this);
    }

    // ---------------------------------------------------------------------
    // MATCHING
    // ---------------------------------------------------------------------

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        if (effect == null || minAmplifier < 0) {
            return false;
        }

        MobEffectInstance inst = living.getEffect(effect);
        if (inst == null) {
            return false;
        }

        return inst.getAmplifier() >= minAmplifier;
    }
}