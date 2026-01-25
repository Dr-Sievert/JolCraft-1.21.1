package net.sievert.jolcraft.network.data;

public final class ClientAncientLanguageData {
    private static boolean knows;
    private static int revision;

    public static boolean knowsLanguage() {
        return knows;
    }

    public static int revision() {
        return revision;
    }

    public static void setKnows(boolean value) {
        if (knows == value) return;
        knows = value;
        revision++;
    }
}