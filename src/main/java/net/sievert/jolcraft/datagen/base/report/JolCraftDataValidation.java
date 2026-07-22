package net.sievert.jolcraft.datagen.base.report;

import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public final class JolCraftDataValidation {

    private JolCraftDataValidation() {}

    public static void validate(@NotNull JolCraftDataTracking tracking) {
        Objects.requireNonNull(tracking, JolCraftDictionary.TRACK);

        validateNoDuplicatePaths(tracking);
    }

    private static void validateNoDuplicatePaths(@NotNull JolCraftDataTracking tracking) {
        for (Map.Entry<String, Integer> entry : tracking.pathCounts().entrySet()) {
            int count = entry.getValue();
            if (count > 1) {
                throw new IllegalStateException(
                        "Duplicate generated path: " + entry.getKey() + " (count: " + count + ")"
                );
            }
        }
    }
}