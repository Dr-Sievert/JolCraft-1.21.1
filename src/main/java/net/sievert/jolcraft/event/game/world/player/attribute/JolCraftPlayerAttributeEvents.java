package net.sievert.jolcraft.event.game.world.player.attribute;

import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.UUID;

public final class JolCraftPlayerAttributeEvents {

    private JolCraftPlayerAttributeEvents() {}

    public static void clearPlayerTracking(UUID uuid) {
        JolCraftPlayerAttributeEventsHelper.clearPlayerTracking(uuid);
    }

    public static void onXpChange(PlayerXpEvent.XpChange event) {
        JolCraftPlayerAttributeEventsHelper.applyXpIncrease(event);
    }

    public static void onCriticalHit(CriticalHitEvent event) {
        JolCraftPlayerAttributeEventsHelper.applyLuminanceCritical(event);
    }

    public static void applyAttackBuild(LivingIncomingDamageEvent event) {
        JolCraftPlayerAttributeEventsHelper.applyLuminanceCriticalDamage(event);
    }

    public static void onRightContainerBlock(PlayerInteractEvent.RightClickBlock event) {
        JolCraftPlayerAttributeEventsHelper.trackChestLoot(event);
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        JolCraftPlayerAttributeEventsHelper.applyChestLootIncrease(event);
    }

    public static void onContainerClose(PlayerContainerEvent.Close event) {
        JolCraftPlayerAttributeEventsHelper.clearChestLootTracking(event);
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        JolCraftPlayerAttributeEventsHelper.applyCropLootIncrease(event);
    }

    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        JolCraftPlayerAttributeEventsHelper.applyItemUseSpeedStart(event);
    }

    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        JolCraftPlayerAttributeEventsHelper.applyItemUseSpeedTick(event);
    }

    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        JolCraftPlayerAttributeEventsHelper.stopItemUseSpeed(event);
    }

    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        JolCraftPlayerAttributeEventsHelper.finishItemUseSpeed(event);
    }
}
