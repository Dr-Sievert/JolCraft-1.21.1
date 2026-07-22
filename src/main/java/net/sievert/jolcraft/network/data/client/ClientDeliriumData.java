package net.sievert.jolcraft.network.data.client;

import net.minecraft.client.Minecraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;

public final class ClientDeliriumData {

    private static long endGameTime = 0L;

    private ClientDeliriumData() {}

    public static void start(int durationTicks) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;

        long now = mc.level.getGameTime();
        long end = now + Math.max(0, durationTicks);

        long prevEnd = endGameTime;
        endGameTime = Math.max(endGameTime, end);

        if (endGameTime != prevEnd) {
            JolCraftLogs.debug(
                    JolCraftLogTags.PLAYER,
                    "Client delirium updated: now={} end={} (+{}t)",
                    now,
                    endGameTime,
                    durationTicks
            );
        }
    }

    public static void clear() {
        endGameTime = 0L;
    }

    public static boolean isInactive() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            clear();
            return true;
        }

        if (mc.level.getGameTime() >= endGameTime) {
            clear();
            return true;
        }

        return false;
    }
}