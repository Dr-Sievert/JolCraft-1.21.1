package net.sievert.jolcraft.event.game.entity.damage;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.sievert.jolcraft.event.game.entity.attribute.JolCraftEntityAttributeEvents;
import net.sievert.jolcraft.event.game.entity.effect.JolCraftEffectEvents;
import net.sievert.jolcraft.event.game.player.attribute.JolCraftPlayerAttributeEvents;

public final class JolCraftDamageEventsHelper {

    private JolCraftDamageEventsHelper() {}

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        applyAttackBuild(event);
        if (event.isCanceled()) return;

        applyDefenseShaping(event);
        if (event.isCanceled()) return;

        applyPreMitigationTargetModifiers(event);
        if (event.isCanceled()) return;

        applyEarlyFlatDefenses(event);
    }

    public static void onFinalDamage(LivingDamageEvent.Pre event) {
        applyDamageAffinities(event);
        applyFinalDefenses(event);
    }

    public static void onPostDamage(LivingDamageEvent.Post event) {
        applyPostHitMarkers(event);
        applySecondaryDamage(event);
        applyPostHitSideEffects(event);
    }

    private static void applyAttackBuild(LivingIncomingDamageEvent event) {
        JolCraftPlayerAttributeEvents.applyAttackBuild(event);
        JolCraftEntityAttributeEvents.applyAttackBuild(event);
    }

    private static void applyPreMitigationTargetModifiers(LivingIncomingDamageEvent event) {
        JolCraftEffectEvents.applyPreMitigationTargetModifiers(event);
    }

    private static void applyEarlyFlatDefenses(LivingIncomingDamageEvent event) {
        JolCraftEffectEvents.applyEarlyFlatDefenses(event);
    }

    private static void applyDefenseShaping(LivingIncomingDamageEvent event) {
        JolCraftEntityAttributeEvents.applyDefenseShaping(event);
    }

    private static void applyDamageAffinities(LivingDamageEvent.Pre event) {
        JolCraftDamageAffinityEventsHelper.apply(event);
    }

    private static void applyFinalDefenses(LivingDamageEvent.Pre event) {
        JolCraftEntityAttributeEvents.applyFinalDefenses(event);
        JolCraftEffectEvents.applyFinalDefenses(event);
    }

    private static void applyPostHitMarkers(LivingDamageEvent.Post event) {
        JolCraftEntityAttributeEvents.applyPostHitMarkers(event);
        JolCraftEffectEvents.applyPostHitMarkers(event);
    }

    private static void applySecondaryDamage(LivingDamageEvent.Post event) {
        JolCraftEntityAttributeEvents.applySecondaryDamage(event);
        JolCraftEffectEvents.applySecondaryDamage(event);
    }

    private static void applyPostHitSideEffects(LivingDamageEvent.Post event) {
        JolCraftEntityAttributeEvents.applyPostHitSideEffects(event);
        JolCraftEffectEvents.applyPostHitSideEffects(event);
    }
}
