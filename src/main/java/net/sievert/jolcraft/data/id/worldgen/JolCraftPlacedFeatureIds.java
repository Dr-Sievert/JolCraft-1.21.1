package net.sievert.jolcraft.data.id.worldgen;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftPlacedFeatureIds extends JolCraftIds {

    private JolCraftPlacedFeatureIds() {}

    // ---------------------------------------------------------------------
    // Vegetation
    // ---------------------------------------------------------------------

    public static final String DUSKCAP_PATCH_PLACED =
            placed(JolCraftConfiguredFeatureIds.DUSKCAP_PATCH);

    public static final String DEEPSLATE_BULBS_PATCH_PLACED =
            placed(JolCraftConfiguredFeatureIds.DEEPSLATE_BULBS_PATCH);

    // ---------------------------------------------------------------------
    // Ores
    // ---------------------------------------------------------------------

    public static final String SMALL_MITHRIL_ORE_PLACED =
            placed(JolCraftConfiguredFeatureIds.SMALL_MITHRIL_ORE);

    public static final String MEDIUM_MITHRIL_ORE_PLACED =
            placed(JolCraftConfiguredFeatureIds.MEDIUM_MITHRIL_ORE);

    public static final String LARGE_MITHRIL_ORE_PLACED =
            placed(JolCraftConfiguredFeatureIds.LARGE_MITHRIL_ORE);

    public static final String SPECIAL_MITHRIL_ORE_PLACED =
            placed(JolCraftConfiguredFeatureIds.SPECIAL_MITHRIL_ORE);

    // ---------------------------------------------------------------------
    // Geodes
    // ---------------------------------------------------------------------

    public static final String BASALT_GEODE_PLACED =
            placed(JolCraftConfiguredFeatureIds.BASALT_GEODE);

    // ---------------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------------

    private static String placed(String baseId) {
        return JolCraftStrings.underscored(baseId, JolCraftDictionary.PLACED);
    }
}