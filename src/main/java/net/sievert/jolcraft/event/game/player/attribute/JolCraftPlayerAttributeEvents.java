package net.sievert.jolcraft.event.game.player.attribute;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.sievert.jolcraft.JolCraft;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftPlayerAttributeEvents {

    private JolCraftPlayerAttributeEvents() {}

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        JolCraftPlayerAttributeEventsHelper.applyXpIncrease(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCriticalHit(CriticalHitEvent event) {
        JolCraftPlayerAttributeEventsHelper.applyLuminanceCritical(event);
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        JolCraftPlayerAttributeEventsHelper.applyLuminanceCriticalDamage(event);
    }

    @SubscribeEvent
    public static void onRightContainerBlock(PlayerInteractEvent.RightClickBlock event) {
        JolCraftPlayerAttributeEventsHelper.trackChestLoot(event);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        JolCraftPlayerAttributeEventsHelper.applyChestLootIncrease(event);
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        JolCraftPlayerAttributeEventsHelper.clearChestLootTracking(event);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        JolCraftPlayerAttributeEventsHelper.applyCropLootIncrease(event);
    }

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        JolCraftPlayerAttributeEventsHelper.applyItemUseSpeedStart(event);
    }

    @SubscribeEvent
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        JolCraftPlayerAttributeEventsHelper.applyItemUseSpeedTick(event);
    }

    @SubscribeEvent
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        JolCraftPlayerAttributeEventsHelper.stopItemUseSpeed(event);
    }

    @SubscribeEvent
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        JolCraftPlayerAttributeEventsHelper.finishItemUseSpeed(event);
    }
}