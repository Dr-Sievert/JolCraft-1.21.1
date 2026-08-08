package net.sievert.jolcraft.event.game.entity.effect.util;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.util.JolCraftRuntime;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

import javax.annotation.Nullable;
import java.util.List;

public final class JolCraftStackingEffectEventsHelper {

    private static final int UNLIMITED = -1;

    private static final List<StackingRule> RULES = List.of(
            rule(JolCraftEffects.ATAXIA_CURSE, 4),
            rule(JolCraftEffects.CORROSION, 4),
            rule(JolCraftEffects.DELIRIUM_CURSE, UNLIMITED),
            rule(JolCraftEffects.FAMINE_CURSE, UNLIMITED),
            rule(JolCraftEffects.FRAILTY_CURSE, 4),
            rule(JolCraftEffects.HEX, UNLIMITED),
            rule(JolCraftEffects.VITALITY_CURSE, 4),
            rule(JolCraftEffects.CURSED_WOUND, UNLIMITED)
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

            int newAmplifier =
                    oldEffect.getAmplifier()
                            + addedEffect.getAmplifier()
                            + 1;

            if (rule.maxAmplifier() >= 0) {
                newAmplifier = Math.min(
                        newAmplifier,
                        rule.maxAmplifier()
                );
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
