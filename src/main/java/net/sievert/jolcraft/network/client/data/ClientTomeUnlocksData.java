package net.sievert.jolcraft.network.client.data;

import java.util.List;

public final class ClientTomeUnlocksData {
    private static List<String> unlocks = List.of();
    private static int revision;

    public static java.util.List<String> getAllUnlocks() {
        return unlocks;
    }

    public static int revision() {
        return revision;
    }

    public static void setUnlocks(List<String> value) {
        unlocks = (value == null) ? List.of() : List.copyOf(value);
        revision++;
    }
}
