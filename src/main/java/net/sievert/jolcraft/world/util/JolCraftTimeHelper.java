package net.sievert.jolcraft.world.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

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

    public static boolean isDay(ServerLevel level) {
        return !level.dimensionType().hasFixedTime() && level.isDay();
    }

    public static boolean isDay(ServerPlayer player) {
        return isDay(player.serverLevel());
    }

    public static boolean isDay(Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return false;
        return isDay(serverLevel);
    }
}