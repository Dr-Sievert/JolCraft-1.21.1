package net.sievert.jolcraft.data.language;

import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;
import net.sievert.jolcraft.util.JolCraftStrings;

/**
 * Translation key construction helpers.
 * No concrete names or shared constants live here.
 */
public abstract class AbstractLanguageKeys {

    protected static final String MOD_ID = JolCraft.MOD_ID;

    // ---------------------------------------------------------------------
    // Generic
    // ---------------------------------------------------------------------

    /** Join non-empty parts with "." */
    public static String key(String... parts) {
        return JolCraftStrings.dotted(parts);
    }

    /** Append one additional part to an already-built key */
    public static String keyWith(String base, String suffix) {
        return JolCraftStrings.dotted(base, suffix);
    }

    /** "<category>.<modid>.<path>" */
    public static String category(String category, String path) {
        return JolCraftStrings.dotted(category, MOD_ID, path);
    }

    /** "<modid>.<path>" */
    public static String mod(String path) {
        return JolCraftStrings.dotted(MOD_ID, path);
    }

    // ---------------------------------------------------------------------
    // Specific categories
    // ---------------------------------------------------------------------

    /** "tooltip.<modid>.<category>.<id>" */
    public static String tooltip(String category, String id) {
        return category(JolCraftDataKeys.TOOLTIP,
                JolCraftStrings.dotted(category, id));
    }

    /** "tooltip.<modid>.structure.<id>" */
    public static String tooltipStructure(String id) {
        return tooltip(JolCraftDataKeys.STRUCTURE, id);
    }

    /** "block.<modid>.<id>" */
    public static String block(String id) {
        return category(JolCraftDataKeys.BLOCK, id);
    }

    /** "item.<modid>.<id>" */
    public static String item(String id) {
        return category(JolCraftDataKeys.ITEM, id);
    }

    /** "effect.<modid>.<id>" */
    public static String effect(String id) {
        return category(JolCraftDataKeys.EFFECT, id);
    }

    /** "entity.<modid>.<id>" */
    public static String entity(String id) {
        return category(JolCraftDataKeys.ENTITY, id);
    }

    /** "itemGroup.<modid>.<id>" */
    public static String itemGroup(String id) {
        return category(JolCraftDataKeys.ITEM_GROUP, id);
    }

    /** "attribute.<modid>.<id>" */
    public static String attribute(String id) {
        return category(JolCraftDataKeys.ATTRIBUTE, id);
    }
}