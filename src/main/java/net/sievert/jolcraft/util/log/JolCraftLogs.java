package net.sievert.jolcraft.util.log;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Locale;

public final class JolCraftLogs {

    private JolCraftLogs() {}

    public static final Logger LOGGER = LogUtils.getLogger();

    /* ---------------------------------------------------------------------
     * Debug
     * ------------------------------------------------------------------ */

    public static void debug(String message, Object... args) {
        if (!LOGGER.isDebugEnabled()) return;
        LOGGER.debug(message, args);
    }

    public static void debug(String tag, String message, Object... args) {
        if (!LOGGER.isDebugEnabled()) return;
        LOGGER.debug("[{}] {}", tag, format(message, args));
    }

    /* ---------------------------------------------------------------------
     * Info
     * ------------------------------------------------------------------ */

    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    public static void info(String tag, String message, Object... args) {
        LOGGER.info("[{}] {}", tag, format(message, args));
    }

    /* ---------------------------------------------------------------------
     * Warn
     * ------------------------------------------------------------------ */

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void warn(String tag, String message, Object... args) {
        LOGGER.warn("[{}] {}", tag, format(message, args));
    }

    /* ---------------------------------------------------------------------
     * Error
     * ------------------------------------------------------------------ */

    public static void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public static void error(String tag, String message, Object... args) {
        LOGGER.error("[{}] {}", tag, format(message, args));
    }

    public static void error(String tag, String message, Throwable t, Object... args) {
        LOGGER.error("[{}] {}", tag, format(message, args), t);
    }

    /* ---------------------------------------------------------------------
     * Internal
     * ------------------------------------------------------------------ */

    private static String format(String message, Object... args) {
        if (args == null || args.length == 0) return message;
        return String.format(Locale.ROOT, message, args);
    }
}