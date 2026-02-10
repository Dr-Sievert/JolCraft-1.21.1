package net.sievert.jolcraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
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

    public static void debug(String message, Object... args) {
        if (!LOGGER.isDebugEnabled()) return;
        LOGGER.debug(message, args);
    }

    public static void debug(String tag, String message, Object... args) {
        if (!LOGGER.isDebugEnabled()) return;
        LOGGER.debug(prefix(tag, message), args);
    }

    /* ---------------------------------------------------------------------
     * Info
     * ------------------------------------------------------------------ */

    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    public static void info(String tag, String message, Object... args) {
        LOGGER.info(prefix(tag, message), args);
    }

    /* ---------------------------------------------------------------------
     * Warn
     * ------------------------------------------------------------------ */

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void warn(String tag, String message, Object... args) {
        LOGGER.warn(prefix(tag, message), args);
    }

    /* ---------------------------------------------------------------------
     * Error
     * ------------------------------------------------------------------ */

    public static void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public static void error(String tag, String message, Object... args) {
        LOGGER.error(prefix(tag, message), args);
    }

    /* ---------------------------------------------------------------------
     * Internal
     * ------------------------------------------------------------------ */

    private static String prefix(String tag, String message) {
        return "[" + tag + "] " + message;
    }

    /* ---------------------------------------------------------------------
     * Formatting helpers
     * ------------------------------------------------------------------ */

    /**
     * Formats a fractional value (e.g. 0.15) as a percentage with 1 decimal (e.g. 15.0).
     * Intended for logging only.
     */
    public static double pct1(double value) {
        return Math.round(value * 1000.0D) / 10.0D;
    }


    /** Returns "(x, y, z)" rounded to nearest integer. */
    public static String roundedPos(Vec3 pos) {
        return "("
                + Math.round(pos.x) + ", "
                + Math.round(pos.y) + ", "
                + Math.round(pos.z) + ")";
    }

    /** Returns "(x, y, z)" from a BlockPos. */
    public static String roundedPos(BlockPos pos) {
        return "("
                + pos.getX() + ", "
                + pos.getY() + ", "
                + pos.getZ() + ")";
    }


    /** Convenience overloads */

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