package net.sievert.jolcraft.event.game.world.entity.effect.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.util.JolCraftRuntime;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

import javax.annotation.Nullable;
import java.util.List;

public final class JolCraftStackingEffectEventsHelper {

    private static final int UNLIMITED = -1;

    private static final List<StackingRule> RULES = List.of(

            rule(JolCraftEffects.ATAXIA_CURSE, 4),
            rule(JolCraftEffects.DELIRIUM_CURSE, UNLIMITED),
            rule(JolCraftEffects.FAMINE_CURSE, UNLIMITED),
            rule(JolCraftEffects.FRAILTY_CURSE, 4),
            rule(JolCraftEffects.VITALITY_CURSE, 4),
            rule(JolCraftEffects.CURSED_WOUND, UNLIMITED),
            rule(JolCraftEffects.HEX, UNLIMITED),

            rule(JolCraftEffects.EXPLOSION_VULNERABILITY, UNLIMITED),
            rule(JolCraftEffects.FIRE_VULNERABILITY, UNLIMITED),
            rule(JolCraftEffects.FROST_VULNERABILITY, UNLIMITED),
            rule(JolCraftEffects.MAGIC_VULNERABILITY, UNLIMITED),
            rule(JolCraftEffects.POISON_VULNERABILITY, UNLIMITED),
            rule(JolCraftEffects.SLOW_VULNERABILITY, UNLIMITED),
            rule(JolCraftEffects.WITHER_VULNERABILITY, UNLIMITED),

            rule(JolCraftEffects.CORROSION, 4)
    );

    private JolCraftStackingEffectEventsHelper() {}

    public static void onEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance addedEffect = event.getEffectInstance();

        for (StackingRule rule : RULES) {
            if (!addedEffect.is(rule.effect())) continue;

            applyStackingEffect(
                    event.getEntity(),
                    addedEffect,
                    event.getOldEffectInstance(),
                    rule
            );

            return;
        }
    }

    private static void applyStackingEffect(
            LivingEntity entity,
            MobEffectInstance addedEffect,
            @Nullable MobEffectInstance oldEffect,
            StackingRule rule
    ) {
        if (!rule.guard().enter(entity)) return;

        try {
            if (oldEffect == null) return;

            int oldAmplifier = oldEffect.getAmplifier();
            int addedAmplifier = addedEffect.getAmplifier();

            int newAmplifier =
                    oldAmplifier
                            + addedAmplifier
                            + 1;

            boolean capped =
                    rule.maxAmplifier() >= 0
                            && newAmplifier > rule.maxAmplifier();

            if (capped) {
                newAmplifier = rule.maxAmplifier();
            }

            entity.addEffect(new MobEffectInstance(
                    addedEffect.getEffect(),
                    Math.max(
                            oldEffect.getDuration(),
                            addedEffect.getDuration()
                    ),
                    newAmplifier,
                    addedEffect.isAmbient(),
                    addedEffect.isVisible(),
                    addedEffect.showIcon()
            ));

            JolCraftLogs.debug(
                    JolCraftLogTags.ENTITY,
                    "Entity {} stacked effect {} from amplifier {} + {} to {}{}",
                    entity.getName().getString(),
                    addedEffect.getEffect().unwrapKey()
                            .map(ResourceKey::location)
                            .orElse(null),
                    oldAmplifier,
                    addedAmplifier,
                    newAmplifier,
                    capped ? " (capped)" : ""
            );
        } finally {
            rule.guard().exit(entity);
        }
    }

    private static StackingRule rule(
            Holder<MobEffect> effect,
            int maxAmplifier
    ) {
        return new StackingRule(
                effect,
                new JolCraftRuntime.Guard(),
                maxAmplifier
        );
    }

    private record StackingRule(
            Holder<MobEffect> effect,
            JolCraftRuntime.Guard guard,
            int maxAmplifier
    ) {}
}
