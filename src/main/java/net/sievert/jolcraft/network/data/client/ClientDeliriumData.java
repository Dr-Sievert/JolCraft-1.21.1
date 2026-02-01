package net.sievert.jolcraft.network.data.client;

import net.minecraft.client.Minecraft;

public final class ClientDeliriumData {

    private static long endGameTime = 0L;

    private ClientDeliriumData() {}

    public static void start(int durationTicks) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;
        long now = mc.level.getGameTime();
        long end = now + Math.max(0, durationTicks);
        endGameTime = Math.max(endGameTime, end);
    }

    public static boolean isActive() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return false;

        return mc.level.getGameTime() < endGameTime;
    }
}