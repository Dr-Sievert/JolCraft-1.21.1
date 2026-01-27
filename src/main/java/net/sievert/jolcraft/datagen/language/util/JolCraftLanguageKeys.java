package net.sievert.jolcraft.datagen.language.util;

import net.sievert.jolcraft.JolCraft;

/**
 * Translation key construction helpers.
 * No concrete names or shared constants live here.
 */
public final class JolCraftLanguageKeys {

    public static final String MODID = JolCraft.MOD_ID;

    private JolCraftLanguageKeys() {}

    /** "<category>.<modid>.<path>" */
    public static String category(String category, String path) {
        return category + "." + MODID + "." + path;
    }

    /** "<category>.<modid>.<path>" (enum-safe) */
    public static String category(JolCraftLanguageCategory category, String path) {
        return category(category.key(), path);
    }

    /** "<modid>.<path>" */
    public static String mod(String path) {
        return MODID + "." + path;
    }

    // ---------------------------------------------------------------------
    // Standard categories
    // ---------------------------------------------------------------------

    public static String tooltip(String category, String path) { return category(JolCraftLanguageCategory.TOOLTIP, category + "." + path); }
    public static String container(String path) { return category(JolCraftLanguageCategory.CONTAINER, path); }
    public static String jei(String path)       { return category(JolCraftLanguageCategory.JEI, path); }
    public static String stat(String path)      { return category(JolCraftLanguageCategory.STAT, path); }

    /** itemGroup.<modid>.<path> */
    public static String itemGroup(String path) {
        return "itemGroup." + MODID + "." + path;
    }

    /** subtitles.<modid>.<path> */
    public static String subtitles(String path) {
        return "subtitles." + MODID + "." + path;
    }

    /** jolcraft.reputation_tier.<n> */
    public static String reputationTier(int tier) {
        return mod("reputation_tier." + tier);
    }

    /** trim_material.jolcraft.<id> */
    public static String trimMaterial(String id) {
        return "trim_material." + MODID + "." + id;
    }

    /** trim_pattern.jolcraft.<id> */
    public static String trimPattern(String id) {
        return "trim_pattern." + MODID + "." + id;
    }
}