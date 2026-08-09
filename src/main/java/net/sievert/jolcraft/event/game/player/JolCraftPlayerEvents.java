package net.sievert.jolcraft.event.game.player;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.game.item.JolCraftCompassEvents;
import net.sievert.jolcraft.event.game.player.attribute.JolCraftPlayerAttributeEvents;
import net.sievert.jolcraft.event.game.player.util.JolCraftCoinPickupEventsHelper;
import net.sievert.jolcraft.event.game.player.util.JolCraftPlantingEventsHelper;
import net.sievert.jolcraft.event.game.recipe.JolCraftHandInteractionEvents;
import net.sievert.jolcraft.event.game.recipe.brewing.BrewingBlockConversionEventsHelper;
import net.sievert.jolcraft.network.handler.JolCraftServerPayloadHandlers;
import net.sievert.jolcraft.network.util.SyncHelper;
import net.sievert.jolcraft.world.data.custom.PendingStatData;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.curse.DeliriumCurseEffect;
import net.sievert.jolcraft.world.gui.menu.DwarfMerchantMenu;
import net.sievert.jolcraft.world.entity.player.advancement.JolCraftCriteriaTriggers;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftPlayerEvents {

    private JolCraftPlayerEvents() {}

    @SubscribeEvent
    public static void onAdvancementEarned(
            AdvancementEvent.AdvancementEarnEvent event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        JolCraftCriteriaTriggers.HAS_ADVANCEMENT.trigger(
                player,
                event.getAdvancement().id()
        );
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PendingStatData.awardPending(player);
        SyncHelper.syncAll(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            cleanupPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            cleanupPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        JolCraftCompassEvents.onCompassTick(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCriticalHit(CriticalHitEvent event) {
        JolCraftPlayerAttributeEvents.onCriticalHit(event);
    }

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        JolCraftPlayerAttributeEvents.onXpChange(event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        JolCraftPlayerAttributeEvents.onRightContainerBlock(event);
        JolCraftPlantingEventsHelper.tryHandle(event);

        if (!event.isCanceled()) {
            BrewingBlockConversionEventsHelper.tryHandle(event);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        JolCraftHandInteractionEvents.onHandInteractionRecipe(event);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        JolCraftPlayerAttributeEvents.onContainerOpen(event);
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        JolCraftPlayerAttributeEvents.onContainerClose(event);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        JolCraftPlayerAttributeEvents.onBlockBreak(event);
    }

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        JolCraftPlayerAttributeEvents.onUseItemStart(event);
    }

    @SubscribeEvent
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        JolCraftPlayerAttributeEvents.onUseTick(event);
    }

    @SubscribeEvent
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        JolCraftPlayerAttributeEvents.onUseStop(event);
    }

    @SubscribeEvent
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        JolCraftPlayerAttributeEvents.onUseFinish(event);
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        JolCraftCoinPickupEventsHelper.onItemPickup(event);
    }

    private static void cleanupPlayer(ServerPlayer player) {
        if (player.containerMenu instanceof DwarfMerchantMenu menu) {
            menu.getTrader().setTradingPlayer(null);
        }

        DeliriumCurseEffect.cleanupRuntime(player);
        JolCraftServerPayloadHandlers.cleanupPlayer(player);
        JolCraftPlayerAttributeEvents.clearPlayerTracking(player.getUUID());
        JolCraftCompassEvents.cleanupPlayer(player);
    }
}
