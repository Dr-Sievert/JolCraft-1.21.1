package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public class StatsLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {
        p.putManual(JolCraftLanguageKeys.stat("structures_discovered"), "Structures Discovered");
    }
}
