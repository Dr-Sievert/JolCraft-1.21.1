package net.sievert.jolcraft.event.game.world.entity.attribute;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public final class JolCraftEntityAttributeEvents {

    private JolCraftEntityAttributeEvents() {}

    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        JolCraftEntityAttributeEventsHelper.clearTrackedAttributes(entity);
    }

    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        JolCraftEntityAttributeEventsHelper.tickAttributes(entity);
    }

    public static void onLivingHeal(LivingHealEvent event) {
        JolCraftEntityAttributeEventsHelper.onLivingHeal(event);
    }

    public static void applyAttackBuild(LivingIncomingDamageEvent event) {
        JolCraftEntityAttributeEventsHelper.applyAttackBuild(event);
    }

    public static void applyDefenseShaping(LivingIncomingDamageEvent event) {
        JolCraftEntityAttributeEventsHelper.applyDefenseShaping(event);
    }

    public static void applyFinalDefenses(LivingDamageEvent.Pre event) {
        JolCraftEntityAttributeEventsHelper.applyFinalDefenses(event);
    }

    public static void applyPostHitMarkers(LivingDamageEvent.Post event) {
        JolCraftEntityAttributeEventsHelper.applyPostHitMarkers(event);
    }

    public static void applySecondaryDamage(LivingDamageEvent.Post event) {
        JolCraftEntityAttributeEventsHelper.applySecondaryDamage(event);
    }

    public static void applyPostHitSideEffects(LivingDamageEvent.Post event) {
        JolCraftEntityAttributeEventsHelper.applyPostHitSideEffects(event);
    }
}
