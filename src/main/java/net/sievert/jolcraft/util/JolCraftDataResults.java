package net.sievert.jolcraft.util;

import com.mojang.serialization.DataResult;

import java.util.Optional;

/**
 * Small helpers for working with Mojang DataResult in dev/datagen flows.
 *
 * Contract:
 * - Never returns null
 * - On error: logs + throws IllegalStateException
 * - If a partial value exists, it is included in the exception message
 */
public final class JolCraftDataResults {

    private JolCraftDataResults() {}

    /**
     * Require success. If error, logs and throws IllegalStateException.
     *
     * @param result  DataResult to unwrap
     * @param tag     Log tag (type-safe)
     * @param context Human-readable context (e.g. "BountyTaskRecipeBuilder.recipeName")
     */
    public static <T> T require(DataResult<T> result, JolCraftLogTags tag, String context) {
        if (result == null) {
            String msg = "DataResult is null (" + context + ")";
            JolCraftLogs.error(tag, msg);
            throw new IllegalStateException(msg);
        }

        Optional<DataResult.Error<T>> err = result.error();
        if (err.isPresent()) {
            String message = err.orElseThrow().message();

            String partial = result.result()
                    .map(Object::toString)
                    .orElse("<none>");

            String full = context + " failed: " + message + " | partial=" + partial;
            JolCraftLogs.error(tag, full);
            throw new IllegalStateException(full);
        }

        return result.result()
                .orElseThrow(() -> {
                    String msg = context + " failed: missing result with no error";
                    JolCraftLogs.error(tag, msg);
                    return new IllegalStateException(msg);
                });
    }

    /**
     * Convenience overload using DATA tag.
     */
    public static <T> T require(DataResult<T> result, String context) {
        return require(result, JolCraftLogTags.DATA, context);
    }
}