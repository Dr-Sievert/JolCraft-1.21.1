package net.sievert.jolcraft.event.game.entity.effect;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.JolCraftRuntime;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.Nullable;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftStackingEffectEvents {

    private JolCraftStackingEffectEvents() {}

    private static final JolCraftRuntime.Guard ATAXIA_GUARD = new JolCraftRuntime.Guard();
    private static final JolCraftRuntime.Guard CORROSION_GUARD = new JolCraftRuntime.Guard();
    private static final JolCraftRuntime.Guard DELIRIUM_GUARD = new JolCraftRuntime.Guard();
    private static final JolCraftRuntime.Guard FAMINE_GUARD = new JolCraftRuntime.Guard();
    private static final JolCraftRuntime.Guard FRAILTY_GUARD = new JolCraftRuntime.Guard();
    private static final JolCraftRuntime.Guard HEX_GUARD = new JolCraftRuntime.Guard();
    private static final JolCraftRuntime.Guard VITALITY_GUARD = new JolCraftRuntime.Guard();
    private static final JolCraftRuntime.Guard WOUND_GUARD = new JolCraftRuntime.Guard();

    @SubscribeEvent
    public static void onAddedStackingEffect(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance addedEffect = event.getEffectInstance();
        MobEffectInstance oldEffect = event.getOldEffectInstance();

        if (addedEffect.is(JolCraftEffects.ATAXIA_CURSE)) {
            applyStackingEffect(entity, addedEffect, oldEffect, ATAXIA_GUARD, 4);
            return;
        }

        if (addedEffect.is(JolCraftEffects.CORROSION)) {
            applyStackingEffect(entity, addedEffect, oldEffect, CORROSION_GUARD, 4);
            return;
        }

        if (addedEffect.is(JolCraftEffects.DELIRIUM_CURSE)) {
            applyStackingEffect(entity, addedEffect, oldEffect, DELIRIUM_GUARD, -1);
            return;
        }

        if (addedEffect.is(JolCraftEffects.FAMINE_CURSE)) {
            applyStackingEffect(entity, addedEffect, oldEffect, FAMINE_GUARD, -1);
            return;
        }

        if (addedEffect.is(JolCraftEffects.FRAILTY_CURSE)) {
            applyStackingEffect(entity, addedEffect, oldEffect, FRAILTY_GUARD, 4);
            return;
        }

        if (addedEffect.is(JolCraftEffects.HEX)) {
            applyStackingEffect(entity, addedEffect, oldEffect, HEX_GUARD, -1);
            return;
        }

        if (addedEffect.is(JolCraftEffects.VITALITY_CURSE)) {
            applyStackingEffect(entity, addedEffect, oldEffect, VITALITY_GUARD, 4);
        }

        if (addedEffect.is(JolCraftEffects.CURSED_WOUND)) {
            applyStackingEffect(entity, addedEffect, oldEffect, WOUND_GUARD, -1);
        }
    }

    private static void applyStackingEffect(
            LivingEntity entity,
            MobEffectInstance addedEffect,
            @Nullable MobEffectInstance oldEffect,
            JolCraftRuntime.Guard guard,
            int maxAmplifier
    ) {
        if (!guard.enter(entity)) return;

        try {
            if (oldEffect == null) return;

            int oldAmp = oldEffect.getAmplifier();
            int addedAmp = addedEffect.getAmplifier();

            int newAmp = oldAmp + addedAmp + 1;
            if (maxAmplifier >= 0) {
                newAmp = Math.min(newAmp, maxAmplifier);
            }

            int newDuration = Math.max(oldEffect.getDuration(), addedEffect.getDuration());

            entity.addEffect(new MobEffectInstance(
                    addedEffect.getEffect(),
                    newDuration,
                    newAmp,
                    addedEffect.isAmbient(),
                    addedEffect.isVisible(),
                    addedEffect.showIcon()
            ));

            if (addedEffect.is(JolCraftEffects.ATAXIA_CURSE)
                    || addedEffect.is(JolCraftEffects.DELIRIUM_CURSE)
                    || addedEffect.is(JolCraftEffects.FAMINE_CURSE)
                    || addedEffect.is(JolCraftEffects.FRAILTY_CURSE)
                    || addedEffect.is(JolCraftEffects.CURSED_WOUND)
                    || addedEffect.is(JolCraftEffects.HEX)
                    || addedEffect.is(JolCraftEffects.VITALITY_CURSE)) {
                PlaySound.curse(entity);
            }

            if (addedEffect.is(JolCraftEffects.CORROSION)) {
                JolCraftSoundHelper.entity(entity, SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7F, 0.55F);
            }

        } finally {
            guard.exit(entity);
        }
    }
}