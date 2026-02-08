package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.client.language.util.JolCraftLanguageCategory;
import net.sievert.jolcraft.datagen.client.language.util.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class MiscLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    // ---------------------------------------------------------------------
    // Root tooltip keys (tooltip.<modid>.<path>)
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_HOLD_KEY = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "hold_key");

    public static final String TOOLTIP_DEV_KEY = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dev_key");

    public static final String TOOLTIP_QUILL_EMPTY = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "quill_empty");

    public static final String TOOLTIP_QUILL = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "quill");

    public static final String TOOLTIP_QUILL_FULL = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "quill_full");

    public static final String TOOLTIP_VANILLA_CROP = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "vanilla_crop");

    public static final String TOOLTIP_HOPS_SEED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "hops_seed");

    public static final String TOOLTIP_DEEPSLATE_BULBS = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "deepslate_bulbs");

    public static final String TOOLTIP_MALT = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "malt");

    public static final String TOOLTIP_HOPS = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "hops");

    public static final String TOOLTIP_YEAST = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "yeast");

    public static final String TOOLTIP_GLASS_MUG = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "glass_mug");

    public static final String TOOLTIP_SPANNER = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "spanner");

    public static final String TOOLTIP_SALVAGEABLE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "salvageable");

    public static final String TOOLTIP_SALVAGE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "salvage");

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // General
        p.putManual(TOOLTIP_HOLD_KEY, "Hold %s for more info");
        p.putManual(TOOLTIP_DEV_KEY, "Used for playtesting.");

        // Items / blocks
        p.putManual(TOOLTIP_QUILL_EMPTY, "Can be filled with ink sacs or by right-clicking a squid.");
        p.putManual(TOOLTIP_QUILL, "Used for writing on paper. Can be filled by right-clicking a squid.");
        p.putManual(TOOLTIP_QUILL_FULL, "Used for writing on paper.");

        p.putManual(TOOLTIP_VANILLA_CROP, "Grows like vanilla crops.");
        p.putManual(TOOLTIP_HOPS_SEED, "Needs two blocks height and a light level of 8 or less to grow.");
        p.putManual(TOOLTIP_DEEPSLATE_BULBS, "Needs a light level of 8 or less and a y-level of 0 or less to grow. Can only be planted on Deepslate, Tuff or Verdant Soil.");

        p.putManual(TOOLTIP_MALT, "Can be used on a water cauldron as a first step in brewing.");
        p.putManual(TOOLTIP_HOPS, "Can be used on a cauldron with malt to add effects to a brew.");
        p.putManual(TOOLTIP_YEAST,
                "Can be used on a fermenting cauldron with malt/hops to start the brewing process. Created by using sugar on a water cauldron and extracted using glass bottles.");
        p.putManual(TOOLTIP_GLASS_MUG, "Can be used to extract a finished dwarven brew from a cauldron.");

        p.putManual(TOOLTIP_SPANNER, "Can be used to produce scrap from salvage. Hold the spanner in one hand and salvage in the other, then right click!");
        p.putManual(TOOLTIP_SALVAGEABLE, "Salvageable");
        p.putManual(TOOLTIP_SALVAGE, "Can be used to produce scrap using a spanner. " + "Hold the spanner in one hand and the salvage in the other, then right click!");
    }
}