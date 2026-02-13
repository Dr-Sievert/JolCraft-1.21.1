package net.sievert.jolcraft.data.id.worldgen;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftConfiguredFeatureIds extends JolCraftIds {

    private JolCraftConfiguredFeatureIds() {}

    // ---------------------------------------------------------------------
    // Vegetation
    // ---------------------------------------------------------------------

    public static final String DUSKCAP_PATCH =
            join(JolCraftBlockIds.DUSKCAP, JolCraftDictionary.PATCH);

    public static final String DEEPSLATE_BULBS_PATCH =
            join(JolCraftDictionary.DEEPSLATE, JolCraftStrings.plural(JolCraftDictionary.BULB), JolCraftDictionary.PATCH);

    // ---------------------------------------------------------------------
    // Ores
    // ---------------------------------------------------------------------

    public static final String SMALL_MITHRIL_ORE =
            join(JolCraftDictionary.SMALL, JolCraftDictionary.MITHRIL, JolCraftDictionary.ORE);

    public static final String MEDIUM_MITHRIL_ORE =
            join(JolCraftDictionary.MEDIUM, JolCraftDictionary.MITHRIL, JolCraftDictionary.ORE);

    public static final String LARGE_MITHRIL_ORE =
            join(JolCraftDictionary.LARGE, JolCraftDictionary.MITHRIL, JolCraftDictionary.ORE);

    public static final String SPECIAL_MITHRIL_ORE =
            join(JolCraftDictionary.SPECIAL, JolCraftDictionary.MITHRIL, JolCraftDictionary.ORE);

    // ---------------------------------------------------------------------
    // Geodes
    // ---------------------------------------------------------------------

    public static final String BASALT_GEODE =
            join(JolCraftDictionary.BASALT, JolCraftDictionary.GEODE);
}