package net.sievert.jolcraft.datagen.client.language.util;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;

/**
 * Translation key construction helpers.
 * No concrete names or shared constants live here.
 */
@OnlyIn(Dist.CLIENT)
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
    // Specific categories
    // ---------------------------------------------------------------------

    public static String tooltip(String category, String path) { return category(JolCraftLanguageCategory.TOOLTIP, category + "." + path); }
}