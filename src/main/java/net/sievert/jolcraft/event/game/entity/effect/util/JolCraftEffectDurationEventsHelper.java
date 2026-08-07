package net.sievert.jolcraft.event.game.entity.effect.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.mixin.MobEffectInstanceAccessor;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.AbstractCrowdControlEffect;

public final class JolCraftEffectDurationEventsHelper {

    private JolCraftEffectDurationEventsHelper() {}

    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = event.getEffectInstance();

        if (effect.isInfiniteDuration()) {
            return;
        }

        applyFocus(entity, effect);
        applyTenacity(entity, effect);
    }

    private static void applyFocus(
            LivingEntity entity,
            MobEffectInstance effect
    ) {
        if (!effect.getEffect().value().isBeneficial()) {
            return;
        }

        double focus = entity.getAttributeValue(JolCraftAttributes.FOCUS);

        if (focus <= 0.0D) {
            return;
        }

        modifyDuration(
                entity,
                effect,
                JolCraftAttributes.FOCUS,
                focus
        );
    }

    private static void applyTenacity(
            LivingEntity entity,
            MobEffectInstance effect
    ) {
        if (!(effect.getEffect().value() instanceof AbstractCrowdControlEffect)) {
            return;
        }

        double tenacity = entity.getAttributeValue(JolCraftAttributes.TENACITY);

        if (tenacity <= 0.0D) {
            return;
        }

        modifyDuration(
                entity,
                effect,
                JolCraftAttributes.TENACITY,
                -tenacity
        );
    }

    private static void modifyDuration(
            LivingEntity entity,
            MobEffectInstance effect,
            Holder<Attribute> attribute,
            double multiplier
    ) {
        int originalDuration = effect.getDuration();
        int modifiedDuration = Math.max(
                0,
                (int) Math.round(
                        originalDuration * (1.0D + multiplier)
                )
        );

        ((MobEffectInstanceAccessor) effect)
                .jolcraft$setDuration(modifiedDuration);

        logDurationModification(
                entity,
                effect,
                attribute,
                multiplier,
                originalDuration,
                modifiedDuration
        );
    }

    private static void logDurationModification(
            LivingEntity entity,
            MobEffectInstance effect,
            Holder<Attribute> attribute,
            double multiplier,
            int originalDuration,
            int modifiedDuration
    ) {
        double changedSeconds =
                (modifiedDuration - originalDuration) / 20.0D;

        JolCraftLogs.debug(
                JolCraftLogTags.ENTITY,
                "Attribute {} modified {} duration by {}s ({}%) on entity {} at {} in {}",
                attribute.unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(null),
                effect.getEffect().unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(null),
                String.format("%+.1f", changedSeconds),
                JolCraftLogs.pct1(multiplier),
                entity.getName().getString(),
                JolCraftLogs.roundedPos(entity.position()),
                entity.level().dimension().location()
        );
    }
}