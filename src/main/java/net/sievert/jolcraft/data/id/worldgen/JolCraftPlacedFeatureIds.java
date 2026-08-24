package net.sievert.jolcraft.data.id.worldgen;

import net.minecraft.world.level.biome.Biomes;
import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftPlacedFeatureIds extends JolCraftIds {

    private JolCraftPlacedFeatureIds() {}

    // ---------------------------------------------------------------------
    // Vegetation
    // ---------------------------------------------------------------------

    public static final String BLOODROOT_PLACED =
            placed(JolCraftConfiguredFeatureIds.BLOODROOT);

    public static final String BLOODROOT_SPECIAL_PLACED =
            placed(join(JolCraftConfiguredFeatureIds.BLOODROOT, JolCraftDictionary.SPECIAL));

    public static final String CYANELLA_PATCH_PLACED =
            placed(JolCraftConfiguredFeatureIds.CYANELLA_PATCH);

    public static final String SKYBELL_PATCH_PLACED =
            placed(JolCraftConfiguredFeatureIds.SKYBELL_PATCH);

    public static final String SKYBELL_PATCH_SPECIAL_PLACED =
            join(JolCraftConfiguredFeatureIds.SKYBELL_PATCH, JolCraftDictionary.SPECIAL, JolCraftDictionary.PLACED);

    public static final String DUSKCAP_PATCH_PLACED =
            placed(JolCraftConfiguredFeatureIds.DUSKCAP_PATCH);

    public static final String DEEPSLATE_BULBS_PATCH_PLACED =
            placed(JolCraftConfiguredFeatureIds.DEEPSLATE_BULBS_PATCH);

    // ---------------------------------------------------------------------
    // Ores
    // ---------------------------------------------------------------------

    public static final String ORE_MITHRIL_SMALL_PLACED =
            placed(JolCraftConfiguredFeatureIds.ORE_MITHRIL_SMALL);

    public static final String ORE_MITHRIL_MEDIUM_PLACED =
            placed(JolCraftConfiguredFeatureIds.ORE_MITHRIL_MEDIUM);

    public static final String ORE_MITHRIL_LARGE_PLACED =
            placed(JolCraftConfiguredFeatureIds.ORE_MITHRIL_LARGE);

    public static final String ORE_MITHRIL_SPECIAL_PLACED =
            placed(JolCraftConfiguredFeatureIds.ORE_MITHRIL_SPECIAL);

    public static final String ORE_MITHRIL_BURIED_PLACED =
            placed(JolCraftConfiguredFeatureIds.ORE_MITHRIL_BURIED);

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