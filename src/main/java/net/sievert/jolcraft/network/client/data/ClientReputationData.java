package net.sievert.jolcraft.network.client.data;

import java.util.List;

public final class ClientReputationData {
    private static int tier;
    private static List<?> endorsements = List.of();
    private static int revision;

    public static int getTier() {
        return tier;
    }

    public static List<?> getAllEndorsements() {
        return endorsements;
    }

    public static int revision() {
        return revision;
    }

    public static void setTier(int value) {
        if (tier == value) return;
        tier = value;
        revision++;
    }

    public static void setEndorsements(List<?> value) {
        endorsements = (value == null) ? List.of() : List.copyOf(value);
        revision++;
    }
}
