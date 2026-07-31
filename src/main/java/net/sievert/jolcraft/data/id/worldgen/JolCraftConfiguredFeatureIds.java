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

    public static final String HUGE_DUSKCAP = JolCraftFeatureIds.HUGE_DUSKCAP;

    public static final String HUGE_FESTERLING = JolCraftFeatureIds.HUGE_FESTERLING;

    public static final String DEEPSLATE_BULBS_PATCH =
            join(JolCraftDictionary.DEEPSLATE, JolCraftStrings.plural(JolCraftDictionary.BULB), JolCraftDictionary.PATCH);

    // ---------------------------------------------------------------------
    // Ores
    // ---------------------------------------------------------------------

    public static final String ORE_MITHRIL_SMALL =
            join(JolCraftDictionary.ORE, JolCraftDictionary.MITHRIL, JolCraftDictionary.SMALL);

    public static final String ORE_MITHRIL_MEDIUM =
            join(JolCraftDictionary.ORE, JolCraftDictionary.MITHRIL, JolCraftDictionary.MEDIUM);

    public static final String ORE_MITHRIL_LARGE =
            join(JolCraftDictionary.ORE, JolCraftDictionary.MITHRIL, JolCraftDictionary.LARGE);

    public static final String ORE_MITHRIL_SPECIAL =
            join(JolCraftDictionary.ORE, JolCraftDictionary.MITHRIL, JolCraftDictionary.SPECIAL);

    public static final String ORE_MITHRIL_BURIED =
            join(JolCraftDictionary.ORE, JolCraftDictionary.MITHRIL, JolCraftDictionary.BURIED);

    // ---------------------------------------------------------------------
    // Geodes
    // ---------------------------------------------------------------------

    public static final String BASALT_GEODE = JolCraftFeatureIds.BASALT_GEODE;
}