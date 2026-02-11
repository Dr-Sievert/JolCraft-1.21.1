package net.sievert.jolcraft.data.id.block;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.key.JolCraftDictionary;

public final class JolCraftBlockIds extends JolCraftIds {

    private JolCraftBlockIds() {}

    public static final String MANAGED_LIGHT = "managed_light";
    public static final String DEEPSLATE_MORTAR = "deepslate_mortar";
    public static final String GEODE_BLOCK = block(JolCraftDictionary.GEODE);
    public static final String LAPIDARY_BENCH = "lapidary_bench";

    public static final String DEEPSLATE_MITHRIL_ORE = "deepslate_mithril_ore";
    public static final String PURE_MITHRIL_BLOCK = block(JolCraftItemIds.PURE_MITHRIL);
    public static final String MITHRIL_BLOCK = block(JolCraftDictionary.MITHRIL);

    public static final String DEEPSLATE_PLATE_BLOCK = block(JolCraftItemIds.DEEPSLATE_PLATE);

    public static final String STRONGBOX = "strongbox";
    public static final String STRONGBOX_DUMMY = "strongbox_dummy";

    public static final String HEARTH = "hearth";

    public static final String VERDANT_SOIL = "verdant_soil";
    public static final String VERDANT_FARMLAND = "verdant_farmland";

    public static final String DUSKCAP = "duskcap";
    public static final String POTTED_DUSKCAP = "potted_duskcap";

    public static final String FESTERLING_CROP = "festerling_crop";
    public static final String FESTERLING = "festerling";
    public static final String POTTED_FESTERLING = "potted_festerling";

    public static final String BARLEY_BLOCK = "barley_block";
    public static final String MUFFHORN_FUR_BLOCK = "muffhorn_fur_block";

    public static final String BARLEY_CROP = "barley_crop";
    public static final String DEEPSLATE_BULBS_CROP = "deepslate_bulbs_crop";

    public static final String ASGARNIAN_CROP_TOP = "asgarnian_crop_top";
    public static final String ASGARNIAN_CROP_BOTTOM = "asgarnian_crop_bottom";

    public static final String DUSKHOLD_CROP_TOP = "duskhold_crop_top";
    public static final String DUSKHOLD_CROP_BOTTOM = "duskhold_crop_bottom";

    public static final String KRANDONIAN_CROP_TOP = "krandonian_crop_top";
    public static final String KRANDONIAN_CROP_BOTTOM = "krandonian_crop_bottom";

    public static final String YANILLIAN_CROP_TOP = "yanillian_crop_top";
    public static final String YANILLIAN_CROP_BOTTOM = "yanillian_crop_bottom";

    public static final String FERMENTING_CAULDRON = "fermenting_cauldron";

    private static String block(String id){
        return suffixed(id, JolCraftDictionary.BLOCK);
    }
}