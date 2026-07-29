package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class MiscLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.MISC;
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        // General
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, "Hold %s for more info");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DEV_KEY, "Used for playtesting.");
        putManual(translations, JolCraftLanguageKeys.UNKNOWN, "Unknown");

        // Items / blocks
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_QUILL_EMPTY, "Can be filled with ink sacs or by right-clicking a squid.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_QUILL, "Used for writing on paper. Can be filled by right-clicking a squid.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_QUILL_FULL, "Used for writing on paper.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_VANILLA_CROP, "Grows like vanilla crops.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HOPS_SEEDS, "Needs two blocks of vertical space and a light level of 8 or less to grow.");
        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_BULBS,
                "Needs a light level of 8 or less and a Y-level of 0 or below to grow. Can only be planted on deepslate, tuff, or verdant soil."
        );

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_MALT, "Can be used on a water cauldron as a first step in creating a dwarven brew.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HOPS, "Can be used on a cauldron to add effects to a an unfinished brew.");
        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_YEAST,
                "Can be used on a fermenting cauldron to start the brewing process of an unfinished brew."
        );
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_GLASS_MUG, "Can be used to extract a finished dwarven brew from a cauldron.");

        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_SPANNER,
                "Can be used to produce scrap from salvage. Hold the spanner in one hand and salvage in the other, then right-click!"
        );
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_SALVAGEABLE, "Salvageable");
        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_SALVAGE,
                "Can be used to produce scrap using a spanner. Hold the spanner in one hand and the salvage in the other, then right-click!"
        );

        putManual(translations, JolCraftLanguageKeys.BREW_AGE, "Age: %s");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_FRESH, "Fresh");
        putManual(translations, JolCraftLanguageKeys.BARREL_BREW_AGE, "This barrel contains %s dwarven brew.");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_AGED, "Aged");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_MATURED, "Matured");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_VINTAGE, "Vintage");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_NAME, "%1$s %2$s");
    }
}