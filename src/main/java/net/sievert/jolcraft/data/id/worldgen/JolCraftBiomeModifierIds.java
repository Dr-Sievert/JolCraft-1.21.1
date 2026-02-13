package net.sievert.jolcraft.data.id.worldgen;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftBiomeModifierIds extends JolCraftIds {

    private JolCraftBiomeModifierIds() {}

    // ---------------------------------------------------------------------
    // Vegetation
    // ---------------------------------------------------------------------

    public static final String ADD_DUSKCAP_PATCH =
            add(JolCraftConfiguredFeatureIds.DUSKCAP_PATCH);

    public static final String ADD_DEEPSLATE_BULBS_PATCH =
            add(JolCraftConfiguredFeatureIds.DEEPSLATE_BULBS_PATCH);

    // ---------------------------------------------------------------------
    // Ores
    // ---------------------------------------------------------------------

    public static final String ADD_SMALL_MITHRIL_ORE =
            add(JolCraftConfiguredFeatureIds.SMALL_MITHRIL_ORE);

    public static final String ADD_MEDIUM_MITHRIL_ORE =
            add(JolCraftConfiguredFeatureIds.MEDIUM_MITHRIL_ORE);

    public static final String ADD_LARGE_MITHRIL_ORE =
            add(JolCraftConfiguredFeatureIds.LARGE_MITHRIL_ORE);

    public static final String ADD_SPECIAL_MITHRIL_ORE =
            add(JolCraftConfiguredFeatureIds.SPECIAL_MITHRIL_ORE);

    // ---------------------------------------------------------------------
    // Geodes
    // ---------------------------------------------------------------------

    public static final String ADD_BASALT_GEODE =
            add(JolCraftConfiguredFeatureIds.BASALT_GEODE);

    // ---------------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------------

    private static String add(String... parts) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.ADD,
                JolCraftStrings.underscored(parts)
        );
    }
}
