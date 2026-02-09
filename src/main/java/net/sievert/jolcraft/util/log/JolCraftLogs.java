package net.sievert.jolcraft.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JolCraftLogs {

    private JolCraftLogs() {}

    private static final String LOGGER_NAME = "JOLCRAFT";

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

    public static void error(String tag, String message, Throwable t, Object... args) {
        LOGGER.error(prefix(tag, message), args, t);
    }

    /* ---------------------------------------------------------------------
     * Internal
     * ------------------------------------------------------------------ */

    private static String prefix(String tag, String message) {
        return "[" + tag + "] " + message;
    }
}