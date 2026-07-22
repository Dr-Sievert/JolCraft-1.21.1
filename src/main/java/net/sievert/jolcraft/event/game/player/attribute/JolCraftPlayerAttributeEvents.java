package net.sievert.jolcraft.event.game.player.attribute;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftPlayerAttributeEvents {

    private JolCraftPlayerAttributeEvents() {}

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        JolCraftPlayerAttributeHelper.applyXpIncrease(event);
    }

    @SubscribeEvent
    public static void onPlayerRadiantTick(PlayerTickEvent.Post event) {
        JolCraftPlayerAttributeHelper.tickRadiantEntity(event);
    }

    @SubscribeEvent
    public static void onLevelTickRadiantAura(LevelTickEvent.Post event) {
        JolCraftPlayerAttributeHelper.tickRadiantAura(event);
    }

    @SubscribeEvent
    public static void onRightContainerBlock(PlayerInteractEvent.RightClickBlock event) {
        JolCraftPlayerAttributeHelper.trackChestLoot(event);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        JolCraftPlayerAttributeHelper.applyChestLootIncrease(event);
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        JolCraftPlayerAttributeHelper.clearChestLootTracking(event);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        JolCraftPlayerAttributeHelper.applyCropLootIncrease(event);
    }

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        JolCraftPlayerAttributeHelper.applyItemUseSpeedStart(event);
    }

    @SubscribeEvent
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        JolCraftPlayerAttributeHelper.applyItemUseSpeedTick(event);
    }

    @SubscribeEvent
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        JolCraftPlayerAttributeHelper.stopItemUseSpeed(event);
    }

    @SubscribeEvent
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        JolCraftPlayerAttributeHelper.finishItemUseSpeed(event);
    }
}