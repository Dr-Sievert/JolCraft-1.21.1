package net.sievert.jolcraft.event.game.entity.effect.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public final class JolCraftEffectApplicationEventsHelper {

    private static final List<EffectProtection> PROTECTIONS = List.of(
            attributeProtection(
                    JolCraftAttributes.MAGIC_RESISTANCE,
                    JolCraftEffects.CURSED_WOUND,
                    JolCraftEffects.ATAXIA_CURSE,
                    JolCraftEffects.DELIRIUM_CURSE,
                    JolCraftEffects.FAMINE_CURSE,
                    JolCraftEffects.FRAILTY_CURSE,
                    JolCraftEffects.VITALITY_CURSE
            ),
            attributeProtection(
                    JolCraftAttributes.POISON_RESISTANCE,
                    MobEffects.POISON
            ),
            effectProtection(
                    MobEffects.FIRE_RESISTANCE,
                    JolCraftEffects.SUNFIRE,
                    JolCraftEffects.FROST_RESISTANCE
            ),
            attributeProtection(
                    JolCraftAttributes.FROST_RESISTANCE,
                    MobEffects.FIRE_RESISTANCE
            ),
            attributeProtection(
                    JolCraftAttributes.WITHER_RESISTANCE,
                    MobEffects.WITHER
            ),
            cleansingEffectProtection(
                    JolCraftEffects.ANCHOR,
                    MobEffects.LEVITATION,
                    MobEffects.SLOW_FALLING,
                    MobEffects.DOLPHINS_GRACE,
                    MobEffects.MOVEMENT_SPEED,
                    MobEffects.JUMP
            )
    );

    private JolCraftEffectApplicationEventsHelper() {}

    public static void onEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance addedEffect = event.getEffectInstance();

        for (EffectProtection protection : PROTECTIONS) {
            if (!protection.cleanseExisting()
                    || protection.protectionEffect() == null
                    || !addedEffect.is(protection.protectionEffect())) {
                continue;
            }

            for (Holder<MobEffect> blockedEffect : protection.blockedEffects()) {
                event.getEntity().removeEffect(blockedEffect);
            }

            return;
        }
    }

    public static void onEffectApplicable(
            MobEffectEvent.Applicable event
    ) {
        LivingEntity entity = event.getEntity();
        Holder<MobEffect> effect =
                event.getEffectInstance().getEffect();

        for (EffectProtection protection : PROTECTIONS) {
            if (!protection.blockedEffects().contains(effect)) {
                continue;
            }

            double strength =
                    protectionStrength(entity, protection);

            if (strength <= 0.0D) {
                continue;
            }

            if (strength < 1.0D
                    && entity.getRandom().nextDouble() >= strength) {
                continue;
            }

            event.setResult(
                    MobEffectEvent.Applicable.Result.DO_NOT_APPLY
            );

            JolCraftLogs.debug(
                    JolCraftLogTags.ENTITY,
                    "Entity {} resisted application of effect {} through {} with {}% protection chance",
                    entity.getName().getString(),
                    effect.unwrapKey()
                            .map(ResourceKey::location)
                            .orElse(null),
                    protectionId(protection),
                    JolCraftLogs.pct1(strength)
            );

            return;
        }
    }

    private static double protectionStrength(
            LivingEntity entity,
            EffectProtection protection
    ) {
        if (protection.protectionEffect() != null) {
            return entity.hasEffect(protection.protectionEffect())
                    ? 1.0D
                    : 0.0D;
        }

        return Mth.clamp(
                entity.getAttributeValue(Objects.requireNonNull(protection.protectionAttribute())),
                0.0D,
                1.0D
        );
    }

    private static Object protectionId(
            EffectProtection protection
    ) {
        if (protection.protectionAttribute() != null) {
            return protection.protectionAttribute()
                    .unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);
        }

        return Objects.requireNonNull(protection.protectionEffect())
                .unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);
    }

    @SafeVarargs
    private static EffectProtection attributeProtection(
            Holder<Attribute> protectionAttribute,
            Holder<MobEffect>... blockedEffects
    ) {
        return new EffectProtection(
                protectionAttribute,
                null,
                false,
                List.of(blockedEffects)
        );
    }

    @SafeVarargs
    private static EffectProtection effectProtection(
            Holder<MobEffect> protectionEffect,
            Holder<MobEffect>... blockedEffects
    ) {
        return new EffectProtection(
                null,
                protectionEffect,
                false,
                List.of(blockedEffects)
        );
    }

    @SafeVarargs
    private static EffectProtection cleansingEffectProtection(
            Holder<MobEffect> protectionEffect,
            Holder<MobEffect>... blockedEffects
    ) {
        return new EffectProtection(
                null,
                protectionEffect,
                true,
                List.of(blockedEffects)
        );
    }

    private record EffectProtection(
            @Nullable Holder<Attribute> protectionAttribute,
            @Nullable Holder<MobEffect> protectionEffect,
            boolean cleanseExisting,
            List<Holder<MobEffect>> blockedEffects
    ) {}
}