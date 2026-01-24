package net.sievert.jolcraft.network.client.data;

import java.util.Collections;
import java.util.Set;

public class ClientTomeUnlocksData {
    private static Set<String> UNLOCKS = Collections.emptySet();

    public static void setUnlocks(Set<String> unlocks) {
        UNLOCKS = Set.copyOf(unlocks);
    }

    public static Set<String> getAllUnlocks() {
        return UNLOCKS;
    }

    public static void clear() {
        UNLOCKS = Collections.emptySet();
    }
}
