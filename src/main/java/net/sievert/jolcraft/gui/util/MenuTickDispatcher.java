package net.sievert.jolcraft.gui.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MenuTickDispatcher {
    private static final Set<ServerPlayer> TICKING = ConcurrentHashMap.newKeySet();

    public static void register(ServerPlayer player) {
        TICKING.add(player);
    }

    public static void unregister(ServerPlayer player) {
        TICKING.remove(player);
    }

    public static void tickAll(MinecraftServer server) {
        for (ServerPlayer player : TICKING) {
            if (player.containerMenu instanceof TickableMenu tickable) {
                tickable.tick(player);
            }
        }
    }
}
