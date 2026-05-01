package net.sievert.jolcraft.datagen.base.report;

import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class JolCraftDataTracking {

    private int totalCount = 0;
    private final Map<JolCraftDataDomain, Integer> domainCounts = new LinkedHashMap<>();
    private final Map<String, Integer> providerCounts = new LinkedHashMap<>();
    private final Map<String, Integer> pathCounts = new LinkedHashMap<>();

    private final Map<String, Integer> orderedCounters = new HashMap<>();

    public int nextOrder(@NotNull String key) {
        return orderedCounters.merge(key, 1, Integer::sum);
    }

    public void record(
            @NotNull JolCraftDataProvider<?> provider,
            @NotNull String path
    ) {
        Objects.requireNonNull(provider, JolCraftParameterIds.PROVIDER);
        Objects.requireNonNull(path, JolCraftParameterIds.PATH);

        totalCount++;
        providerCounts.merge(provider.id(), 1, Integer::sum);
        pathCounts.merge(path, 1, Integer::sum);

        JolCraftDataDomain domain = resolveDomain(provider);
        domainCounts.merge(domain, 1, Integer::sum);
    }

    public int totalCount() {
        return totalCount;
    }

    public @NotNull Map<JolCraftDataDomain, Integer> domainCounts() {
        return Map.copyOf(domainCounts);
    }

    public @NotNull Map<String, Integer> providerCounts() {
        return Map.copyOf(providerCounts);
    }

    public @NotNull Map<String, Integer> pathCounts() {
        return Map.copyOf(pathCounts);
    }

    private static @NotNull JolCraftDataDomain resolveDomain(@NotNull JolCraftDataProvider<?> provider) {
        for (JolCraftDataProvider<?> current : provider.chain()) {
            if (current instanceof JolCraftMainDataProvider<?> mainProvider) {
                return mainProvider.domain();
            }
        }

        throw new IllegalStateException("No main provider/domain found in chain for provider: " + provider.name());
    }

    public void logTrackedOutputCount(@NotNull JolCraftDataProvider<?> provider, @NotNull String unitName) {
        int count = providerCounts.getOrDefault(provider.id(), 0);

        JolCraftLogs.debug(
                JolCraftLogTags.DATAGEN,
                "{}: added {} {}",
                provider.name(),
                count,
                unitName
        );
    }

    public static void logExplicitCount(@NotNull JolCraftDataProvider<?> provider, int count, @NotNull String unitName) {
        JolCraftLogs.debug(
                JolCraftLogTags.DATAGEN,
                "{}: added {} {}",
                provider.name(),
                count,
                unitName
        );
    }
}