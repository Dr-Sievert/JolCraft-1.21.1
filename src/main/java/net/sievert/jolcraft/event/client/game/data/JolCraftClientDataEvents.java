package net.sievert.jolcraft.event.client.game.data;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.loot.custom.reward.client.RewardLootTableClientCache;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class JolCraftClientDataEvents {

    private JolCraftClientDataEvents() {}

    @SubscribeEvent
    public static void onLoggingOut(
            ClientPlayerNetworkEvent.LoggingOut event
    ) {
        RewardLootTableClientCache.clear();
    }
}
