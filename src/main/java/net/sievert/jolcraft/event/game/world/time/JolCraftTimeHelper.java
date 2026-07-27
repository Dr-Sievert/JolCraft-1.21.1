package net.sievert.jolcraft.event.game.world.time;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class JolCraftTimeHelper {

    public static final long TICKS_PER_DAY = 24000L;
    public static final long TICKS_PER_SECOND = 20L;

    private JolCraftTimeHelper() {}

    public static long day(ServerLevel level) {
        return level.getDayTime() / TICKS_PER_DAY;
    }

    public static long day(ServerPlayer player) {
        return day(player.serverLevel());
    }
}