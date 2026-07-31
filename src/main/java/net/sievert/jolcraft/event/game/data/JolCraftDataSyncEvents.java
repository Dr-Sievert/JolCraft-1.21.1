package net.sievert.jolcraft.event.game.data;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundRewardLootTablesPacket;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.loot.custom.reward.RewardLootTableSync;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftDataSyncEvents {

    private JolCraftDataSyncEvents() {}

    @SubscribeEvent
    public static void onDatapackSync(
            OnDatapackSyncEvent event
    ) {
        MinecraftServer server = event.getPlayerList().getServer();

        var lootTables = RewardLootTableSync.collect(server);

        ClientboundRewardLootTablesPacket packet =
                new ClientboundRewardLootTablesPacket(
                        lootTables
                );

        event.getRelevantPlayers()
                .forEach(player ->
                        JolCraftNetworking.sendToClient(
                                player,
                                packet
                        )
                );

        JolCraftLogs.info(
                JolCraftLogTags.NETWORK,
                "Bounty Reward sync for loot tables: recipes = {}, players = {}",
                lootTables.size(),
                event.getRelevantPlayers().count()
        );
    }
}
