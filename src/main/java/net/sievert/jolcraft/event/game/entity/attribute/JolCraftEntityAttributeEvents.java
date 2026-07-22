package net.sievert.jolcraft.event.game.entity.attribute;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.sievert.jolcraft.JolCraft;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftEntityAttributeEvents {

    private JolCraftEntityAttributeEvents() {}

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        JolCraftEntityAttributeHelper.clearTrackedAttributes(entity);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        JolCraftEntityAttributeHelper.tickAttributes(entity);
    }

    @SubscribeEvent
    public static void onArmorPenetration(LivingIncomingDamageEvent event) {
        JolCraftEntityAttributeHelper.applyArmorPenetration(event);
    }

    @SubscribeEvent
    public static void handleDamageReductions(LivingDamageEvent.Pre event) {
        JolCraftEntityAttributeHelper.applyFinalDamageReductions(event);
    }
}