package net.sievert.jolcraft.data.id.worldgen;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftBiomeModifierIds extends JolCraftIds {

    private JolCraftBiomeModifierIds() {}

    // ---------------------------------------------------------------------
    // Vegetation
    // ---------------------------------------------------------------------

    public static final String ADD_BLOODROOT =
            add(JolCraftConfiguredFeatureIds.BLOODROOT);

    public static final String ADD_BLOODROOT_SPECIAL =
            add(join(JolCraftConfiguredFeatureIds.BLOODROOT, JolCraftDictionary.SPECIAL));

    public static final String ADD_CYANELLA_PATCH =
            add(JolCraftConfiguredFeatureIds.CYANELLA_PATCH);

    public static final String ADD_SKYBELL_PATCH =
            add(JolCraftConfiguredFeatureIds.SKYBELL_PATCH);

    public static final String ADD_SKYBELL_SPECIAL_PATCH =
            add(join(JolCraftBlockIds.SKYBELL, JolCraftDictionary.SPECIAL, JolCraftDictionary.PATCH));

    public static final String ADD_DUSKCAP_PATCH =
            add(JolCraftConfiguredFeatureIds.DUSKCAP_PATCH);

    public static final String ADD_DEEPSLATE_BULBS_PATCH =
            add(JolCraftConfiguredFeatureIds.DEEPSLATE_BULBS_PATCH);

    // ---------------------------------------------------------------------
    // Ores
    // ---------------------------------------------------------------------

    public static final String ADD_ORE_TUFF_VITRIOL =
            add(JolCraftConfiguredFeatureIds.ORE_TUFF_VITRIOL);

    public static final String ADD_ORE_MITHRIL_SMALL =
            add(JolCraftConfiguredFeatureIds.ORE_MITHRIL_SMALL);

    public static final String ADD_ORE_MITHRIL_MEDIUM =
            add(JolCraftConfiguredFeatureIds.ORE_MITHRIL_MEDIUM);

    public static final String ADD_ORE_MITHRIL_LARGE =
            add(JolCraftConfiguredFeatureIds.ORE_MITHRIL_LARGE);

    public static final String ADD_ORE_MITHRIL_SPECIAL =
            add(JolCraftConfiguredFeatureIds.ORE_MITHRIL_SPECIAL);

    public static final String ADD_ORE_MITHRIL_BURIED =
            add(JolCraftConfiguredFeatureIds.ORE_MITHRIL_BURIED);

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
