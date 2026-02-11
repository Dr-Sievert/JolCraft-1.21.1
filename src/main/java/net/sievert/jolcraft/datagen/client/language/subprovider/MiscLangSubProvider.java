package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class MiscLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // General
        p.putManual(JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, "Hold %s for more info");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DEV_KEY, "Used for playtesting.");
        p.putManual(JolCraftLanguageKeys.UNKNOWN, "Unknown");

        // Items / blocks
        p.putManual(JolCraftLanguageKeys.TOOLTIP_QUILL_EMPTY, "Can be filled with ink sacs or by right-clicking a squid.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_QUILL, "Used for writing on paper. Can be filled by right-clicking a squid.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_QUILL_FULL, "Used for writing on paper.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_VANILLA_CROP, "Grows like vanilla crops.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_HOPS_SEED, "Needs two blocks height and a light level of 8 or less to grow.");
        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_BULBS,
                "Needs a light level of 8 or less and a y-level of 0 or less to grow. Can only be planted on Deepslate, Tuff or Verdant Soil."
        );

        p.putManual(JolCraftLanguageKeys.TOOLTIP_MALT, "Can be used on a water cauldron as a first step in brewing.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_HOPS, "Can be used on a cauldron with malt to add effects to a brew.");
        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_YEAST,
                "Can be used on a fermenting cauldron with malt/hops to start the brewing process. Created by using sugar on a water cauldron and extracted using glass bottles."
        );
        p.putManual(JolCraftLanguageKeys.TOOLTIP_GLASS_MUG, "Can be used to extract a finished dwarven brew from a cauldron.");

        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_SPANNER,
                "Can be used to produce scrap from salvage. Hold the spanner in one hand and salvage in the other, then right click!"
        );
        p.putManual(JolCraftLanguageKeys.TOOLTIP_SALVAGEABLE, "Salvageable");
        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_SALVAGE,
                "Can be used to produce scrap using a spanner. Hold the spanner in one hand and the salvage in the other, then right click!"
        );
    }
}