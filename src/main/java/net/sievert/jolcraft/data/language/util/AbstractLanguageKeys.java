package net.sievert.jolcraft.data.language.util;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
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

    /** "tooltip.<modid>.<category>.<name>" */
    public static String tooltip(String category, String id) { return category(JolCraftDictionary.TOOLTIP, JolCraftStrings.dotted(category, id)); }

    /** "tooltip.<modid>.structure.<name>" */
    public static String tooltipStructure(String id) {
        return tooltip(JolCraftDictionary.STRUCTURE, id);
    }

    /** "block.<modid>.<name>" */
    public static String block(String id) {
        return category(JolCraftDictionary.BLOCK, id);
    }

    /** "fluid_type.<modid>.<name>" */
    public static String fluidType(String id) {
        return category(
                NeoForgeRegistries.Keys.FLUID_TYPES.location().getPath(),
                id
        );
    }

    /** "item.<modid>.<name>" */
    public static String item(String id) {
        return category(JolCraftDictionary.ITEM, id);
    }

    /** "effect.<modid>.<name>" */
    public static String effect(String id) {
        return category(JolCraftDictionary.EFFECT, id);
    }

    /** "entity.<modid>.<name>" */
    public static String entity(String id) {
        return category(JolCraftDictionary.ENTITY, id);
    }

    /** "itemGroup.<modid>.<name>" */
    public static String itemGroup(String id) { return category(JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftDictionary.GROUP), id); }

    /** "attribute.<modid>.<name>" */
    public static String attribute(String id) {
        return category(JolCraftDictionary.ATTRIBUTE, id);
    }

    /** "subtitle.<modid>.<name>" */
    public static String subtitle(String id) {
        return category(JolCraftDictionary.SUBTITLE, id);
    }

    /** "config.jade.plugin_<modid>.<name>" */
    public static String jadeConfig(String id) {
        return key(
                JolCraftDictionary.CONFIG,
                JolCraftDictionary.JADE,
                JolCraftStrings.underscored(
                        JolCraftDictionary.PLUGIN,
                        MOD_ID
                ),
                id
        );
    }

    protected static String subtitleFromSoundId(String soundId) {
        return subtitle(soundId.replace('_', '.'));
    }

}