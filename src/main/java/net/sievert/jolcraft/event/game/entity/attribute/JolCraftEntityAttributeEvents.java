package net.sievert.jolcraft.event.game.entity.attribute;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
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

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        JolCraftEntityAttributeEventsHelper.applyIncomingDamageModifiers(event);
    }

    public static void onFinalDamage(LivingDamageEvent.Pre event) {
        JolCraftEntityAttributeEventsHelper.applyFinalDamageReductions(event);
    }
}