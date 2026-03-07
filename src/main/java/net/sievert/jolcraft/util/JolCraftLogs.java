package net.sievert.jolcraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.JolCraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JolCraftLogs {

    private JolCraftLogs() {}

    private static final String LOGGER_NAME = JolCraft.MOD_ID.toUpperCase();
    private static final Logger LOGGER = LoggerFactory.getLogger(LOGGER_NAME);

    /* ---------------------------------------------------------------------
     * Debug
     * ------------------------------------------------------------------ */

    public static void debug(JolCraftLogTags tag, String message, Object... args) {
        if (!LOGGER.isDebugEnabled()) return;
        log(Level.DEBUG, tag, message, args);
    }

    /* ---------------------------------------------------------------------
     * Info
     * ------------------------------------------------------------------ */

    public static void info(JolCraftLogTags tag, String message, Object... args) {
        log(Level.INFO, tag, message, args);
    }

    /* ---------------------------------------------------------------------
     * Warn
     * ------------------------------------------------------------------ */

    public static void warn(JolCraftLogTags tag, String message, Object... args) {
        log(Level.WARN, tag, message, args);
    }

    /* ---------------------------------------------------------------------
     * Error
     * ------------------------------------------------------------------ */

    public static void error(JolCraftLogTags tag, String message, Object... args) {
        log(Level.ERROR, tag, message, args);
    }

    /* ---------------------------------------------------------------------
     * Internal
     * ------------------------------------------------------------------ */

    private enum Level { DEBUG, INFO, WARN, ERROR }

    private static void log(Level level, JolCraftLogTags tag, String message, Object... args) {
        String prefixed = prefix(tag.getId(), message);

        switch (level) {
            case DEBUG -> LOGGER.debug(prefixed, args);
            case INFO  -> LOGGER.info(prefixed, args);
            case WARN  -> LOGGER.warn(prefixed, args);
            case ERROR -> LOGGER.error(prefixed, args);
        }
    }

    private static String prefix(String tag, String message) {
        return "[" + tag + "] " + message;
    }

    /* ---------------------------------------------------------------------
     * Formatting helpers
     * ------------------------------------------------------------------ */

    public static double pct1(double value) {
        return Math.round(value * 1000.0D) / 10.0D;
    }

    private static String formatXYZ(long x, long y, long z) {
        return "X=" + x + ", Y=" + y + ", Z=" + z;
    }

    public static String roundedPos(Vec3 pos) {
        return formatXYZ(
                Math.round(pos.x),
                Math.round(pos.y),
                Math.round(pos.z)
        );
    }

    public static String roundedPos(BlockPos pos) {
        return formatXYZ(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );
    }

    public static String roundedPos(BlockEntity be) {
        return roundedPos(be.getBlockPos());
    }

    public static String roundedPos(Player player) {
        return roundedPos(player.position());
    }

    public static String roundedPos(Entity entity) {
        return roundedPos(entity.position());
    }
}