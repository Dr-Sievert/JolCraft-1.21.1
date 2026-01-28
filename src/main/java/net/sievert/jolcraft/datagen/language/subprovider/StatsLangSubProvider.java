package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageCategory;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public class StatsLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {
        p.putManual(stat("structures_discovered"), "Compass Structures Discovered");
    }

    public static String stat(String path)      { return JolCraftLanguageKeys.category(JolCraftLanguageCategory.STAT, path); }

}
