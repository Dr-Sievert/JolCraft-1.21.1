package net.sievert.jolcraft.server;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.sievert.jolcraft.gui.custom.strongbox.LockMenu;

public class ServerTickHandler {

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, ServerTickHandler::onServerTick);
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player.containerMenu instanceof LockMenu lockMenu) {
                lockMenu.tick();
            }
        }
    }
}
