package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public final class SubtitlesLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        p.putManual(subtitles("entity.dwarf_ambient"), "Dwarf mumbles");
        p.putManual(subtitles("entity.dwarf_hit"), "Dwarf hurts");
        p.putManual(subtitles("entity.dwarf_death"), "Dwarf dies");
        p.putManual(subtitles("entity.dwarf_yes"), "Dwarf agrees");
        p.putManual(subtitles("entity.dwarf_no"), "Dwarf disagrees");
        p.putManual(subtitles("entity.dwarf_trade"), "Dwarf haggles");
    }

    public static String subtitles(String path) {
        return "subtitles." + JolCraft.MOD_ID + "." + path;
    }

}
