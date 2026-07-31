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

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DEV_KEY, "Used for playtesting.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, "Hold %s for more info");

        putManual(translations, JolCraftLanguageKeys.UNKNOWN, "Unknown");

        // Items / blocks

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_QUILL, "Can be filled by right-clicking a squid.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_SALVAGEABLE, "Salvageable");

        putManual(translations, JolCraftLanguageKeys.BREW_AGE, "Age: %s");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_FRESH, "Fresh");
        putManual(translations, JolCraftLanguageKeys.BARREL_BREW_AGE, "This barrel contains %s dwarven brew.");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_AGED, "Aged");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_MATURED, "Matured");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_VINTAGE, "Vintage");
        putManual(translations, JolCraftLanguageKeys.BREW_AGE_NAME, "%1$s %2$s");
    }
}