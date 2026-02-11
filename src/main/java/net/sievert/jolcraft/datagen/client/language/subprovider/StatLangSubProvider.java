package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.stat.JolCraftStatIds;
import net.sievert.jolcraft.data.key.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.AbstractLanguageKeys;

@OnlyIn(Dist.CLIENT)
public class StatLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {
        p.putManual(stat(JolCraftStatIds.STRUCTURES_DISCOVERED), "Deepslate Compass Structures Discovered");
    }

    public static String stat(String path) { return AbstractLanguageKeys.category(JolCraftDictionary.STAT, path); }
}
