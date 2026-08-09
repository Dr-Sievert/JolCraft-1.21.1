package net.sievert.jolcraft.event.game.entity;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.game.entity.attribute.JolCraftEntityAttributeEvents;
import net.sievert.jolcraft.event.game.entity.damage.JolCraftDamageEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.JolCraftEffectEvents;
import net.sievert.jolcraft.event.game.entity.npc.JolCraftDwarfEvents;
import net.sievert.jolcraft.event.game.entity.npc.villager.JolCraftVillagerEvents;
import net.sievert.jolcraft.event.game.recipe.JolCraftBountyEvents;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftEntityEvents {

    private JolCraftEntityEvents() {}

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        JolCraftEntityAttributeEvents.onEntityLeaveLevel(event);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        JolCraftEntityAttributeEvents.onEntityTick(event);
        JolCraftEffectEvents.onEntityTick(event);
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        JolCraftDamageEventsHelper.onIncomingDamage(event);
    }

    @SubscribeEvent
    public static void onFinalDamage(LivingDamageEvent.Pre event) {
        JolCraftDamageEventsHelper.onFinalDamage(event);
    }

    @SubscribeEvent
    public static void onFinalDamagePost(LivingDamageEvent.Post event) {
        JolCraftDamageEventsHelper.onPostDamage(event);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        JolCraftBountyEvents.onLivingDeath(event);
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        JolCraftEffectEvents.onEffectAdded(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        JolCraftEffectEvents.onEffectRemoved(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        JolCraftEffectEvents.onEffectExpired(event);
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        JolCraftEffectEvents.onEffectApplicable(event);
    }

    @SubscribeEvent
    public static void onArmorHurt(ArmorHurtEvent event) {
        JolCraftEffectEvents.onArmorHurt(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHeal(LivingHealEvent event) {
        JolCraftEffectEvents.onLivingHeal(event);
        if (event.isCanceled()) return;

        JolCraftEntityAttributeEvents.onLivingHeal(event);
    }

    @SubscribeEvent
    public static void onInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        JolCraftDwarfEvents.onInvulnerabilityCheck(event);
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        JolCraftDwarfEvents.onDwarfHostileMobSpawn(event);
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(
            PlayerInteractEvent.EntityInteractSpecific event
    ) {
        JolCraftVillagerEvents.onVillagerCrateInteract(event);
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        JolCraftVillagerEvents.addVillagerTrades(event);
    }
}
