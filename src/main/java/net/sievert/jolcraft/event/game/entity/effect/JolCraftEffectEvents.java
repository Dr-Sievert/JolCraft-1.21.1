package net.sievert.jolcraft.event.game.entity.effect;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.game.entity.effect.harmful.JolCraftCrowdControlEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.harmful.JolCraftCurseEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.util.JolCraftEffectDamageEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.util.JolCraftEffectDurationEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.util.JolCraftStackingEffectEventsHelper;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftEffectEvents {

    private JolCraftEffectEvents() {}

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        JolCraftStackingEffectEventsHelper.onEffectAdded(event);
        JolCraftEffectDurationEventsHelper.onEffectAdded(event);
        JolCraftCurseEventsHelper.onEffectAdded(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        JolCraftCurseEventsHelper.onEffectRemoved(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        JolCraftCurseEventsHelper.onEffectExpired(event);
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (isCreative(event.getEntity())) return;

        JolCraftEffectDamageEventsHelper.onEffectApplicable(event);
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (isCreative(event.getEntity())) return;

        JolCraftEffectDamageEventsHelper.onIncomingDamage(event);
        JolCraftCurseEventsHelper.onIncomingDamage(event);
    }

    @SubscribeEvent
    public static void onArmorHurt(ArmorHurtEvent event) {
        if (isCreative(event.getEntity())) return;

        JolCraftEffectDamageEventsHelper.onArmorHurt(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHeal(LivingHealEvent event) {
        if (isCreative(event.getEntity())) return;

        JolCraftCurseEventsHelper.onLivingHeal(event);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (isCreative(event.getEntity())) return;

        JolCraftCrowdControlEventsHelper.onEntityTick(event);
    }

    private static boolean isCreative(Object entity) {
        return entity instanceof Player player && player.isCreative();
    }
}