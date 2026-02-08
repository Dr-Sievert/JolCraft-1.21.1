package net.sievert.jolcraft.data.language;

import net.sievert.jolcraft.JolCraft;

/**
 * Translation key construction helpers.
 * No concrete names or shared constants live here.
 * Extend this on key holder classes to get the helpers without duplication.
 */
public abstract class AbstractLanguageKeys {

    protected static final String MODID = JolCraft.MOD_ID;

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
    // Specific categories
    // ---------------------------------------------------------------------

    /** "tooltip.<modid>.<category>.<path>" */
    public static String tooltip(String category, String path) {
        return category(JolCraftLanguageCategory.TOOLTIP, category + "." + path);
    }

    /** "tooltip.<modid>.structure.<id>" */
    public static String tooltipStructure(String structureId) {
        return tooltip("structure", structureId);
    }


}