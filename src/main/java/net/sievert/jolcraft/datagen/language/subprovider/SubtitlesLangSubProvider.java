package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public final class SubtitlesLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        p.putManual(JolCraftLanguageKeys.subtitles("entity.dwarf_ambient"), "Dwarf mumbles");
        p.putManual(JolCraftLanguageKeys.subtitles("entity.dwarf_hit"), "Dwarf hurts");
        p.putManual(JolCraftLanguageKeys.subtitles("entity.dwarf_death"), "Dwarf dies");
        p.putManual(JolCraftLanguageKeys.subtitles("entity.dwarf_yes"), "Dwarf agrees");
        p.putManual(JolCraftLanguageKeys.subtitles("entity.dwarf_no"), "Dwarf disagrees");
        p.putManual(JolCraftLanguageKeys.subtitles("entity.dwarf_trade"), "Dwarf haggles");
    }
}
