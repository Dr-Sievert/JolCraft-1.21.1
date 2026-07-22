package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class SubtitleLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.SUBTITLE);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        // ------------------------------------------------------------------
        // Dwarf
        // ------------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.SUBTITLE_DWARF_AMBIENT, "Dwarf mumbles");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_DWARF_HIT, "Dwarf hurts");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_DWARF_DEATH, "Dwarf dies");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_DWARF_YES, "Dwarf agrees");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_DWARF_NO, "Dwarf disagrees");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_DWARF_TRADE, "Dwarf haggles");

        // ------------------------------------------------------------------
        // Misc
        // ------------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.SUBTITLE_LEVEL_UP, "Celebration");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_ARMOR_EQUIP_DEEPSLATE, "Deepslate armor rumbles");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_GEM_CUT, "Gem cut");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_CURSE, "Curse");

        // ------------------------------------------------------------------
        // Strongbox
        // ------------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_OPEN, "Strongbox opens");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_CLOSE, "Strongbox closes");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_LOCKPICK, "Lock being picked");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_LOCKPICK_BREAK, "Lockpick breaks");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_UNLOCK, "Strongbox unlocked");

        // ------------------------------------------------------------------
        // Coins
        // ------------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.SUBTITLE_COIN_STACK, "Coins clink");
        putManual(translations, JolCraftLanguageKeys.SUBTITLE_COIN_SINGLE, "Coin clinks");
    }
}