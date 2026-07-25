package net.sievert.jolcraft.data.id.block;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftBlockIds extends JolCraftIds {

    private JolCraftBlockIds() {}

    public static final String MANAGED_LIGHT = join(JolCraftDictionary.MANAGED, JolCraftDictionary.LIGHT);

    public static final String DEEPSLATE_MORTAR = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.MORTAR);

    public static final String GEODE_BLOCK = block(JolCraftDictionary.GEODE);

    public static final String LAPIDARY_BENCH = join(JolCraftDictionary.LAPIDARY, JolCraftDictionary.BENCH);

    public static final String DEEPSLATE_MITHRIL_ORE = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.MITHRIL, JolCraftDictionary.ORE);

    public static final String PURE_MITHRIL_BLOCK = block(JolCraftItemIds.PURE_MITHRIL);

    public static final String MITHRIL_BLOCK = block(JolCraftDictionary.MITHRIL);

    public static final String DEEPSLATE_PLATE_BLOCK = block(JolCraftItemIds.DEEPSLATE_PLATE);

    public static final String STRONGBOX = JolCraftDictionary.STRONGBOX;

    public static final String STRONGBOX_DUMMY = join(JolCraftDictionary.STRONGBOX, JolCraftDictionary.DUMMY);

    public static final String HEARTH = JolCraftDictionary.HEARTH;

    public static final String VERDANT_SOIL = join(JolCraftDictionary.VERDANT, JolCraftDictionary.SOIL);

    public static final String VERDANT_FARMLAND = join(JolCraftDictionary.VERDANT, JolCraftDictionary.FARMLAND);

    public static final String DUSKCAP = JolCraftDictionary.DUSKCAP;

    public static final String POTTED_DUSKCAP = join(JolCraftDictionary.POTTED, JolCraftDictionary.DUSKCAP);

    public static final String FESTERLING_CROP = crop(JolCraftDictionary.FESTERLING);

    public static final String FESTERLING = JolCraftDictionary.FESTERLING;

    public static final String POTTED_FESTERLING = join(JolCraftDictionary.POTTED, JolCraftDictionary.FESTERLING);

    public static final String BARLEY_BLOCK = block(JolCraftItemIds.BARLEY);

    public static final String MUFFHORN_FUR_BLOCK = block(JolCraftItemIds.MUFFHORN_FUR);

    public static final String BARLEY_CROP = crop(JolCraftDictionary.BARLEY);

    public static final String DEEPSLATE_BULBS_CROP = crop(join(JolCraftDictionary.DEEPSLATE, plural(JolCraftDictionary.BULB)));

    public static final String ASGARNIAN_CROP_TOP = cropTop(JolCraftDictionary.ASGARNIAN);
    public static final String ASGARNIAN_CROP_BOTTOM = cropBottom(JolCraftDictionary.ASGARNIAN);

    public static final String DUSKHOLD_CROP_TOP = cropTop(JolCraftDictionary.DUSKHOLD);
    public static final String DUSKHOLD_CROP_BOTTOM = cropBottom(JolCraftDictionary.DUSKHOLD);

    public static final String KRANDONIAN_CROP_TOP = cropTop(JolCraftDictionary.KRANDONIAN);
    public static final String KRANDONIAN_CROP_BOTTOM = cropBottom(JolCraftDictionary.KRANDONIAN);

    public static final String YANILLIAN_CROP_TOP = cropTop(JolCraftDictionary.YANILLIAN);
    public static final String YANILLIAN_CROP_BOTTOM = cropBottom(JolCraftDictionary.YANILLIAN);

    public static final String FERMENTING_CAULDRON = join(JolCraftDictionary.FERMENTING, JolCraftDictionary.CAULDRON);
    public static final String FERMENTING_BARREL = join(JolCraftDictionary.FERMENTING, JolCraftDictionary.BARREL);

    /* --------------------------------------------------------------------- */

    private static String block(String id) {
        return join(id, JolCraftDictionary.BLOCK);
    }

    private static String crop(String id) {
        return join(id, JolCraftDictionary.CROP);
    }

    private static String cropTop(String id) {
        return join(id, JolCraftDictionary.CROP, JolCraftDictionary.TOP);
    }

    private static String cropBottom(String id) {
        return join(id, JolCraftDictionary.CROP, JolCraftDictionary.BOTTOM);
    }
}