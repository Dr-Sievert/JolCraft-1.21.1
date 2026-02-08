package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.client.language.util.JolCraftLanguageCategory;
import net.sievert.jolcraft.datagen.client.language.util.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public class StatLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {
        p.putManual(stat("structures_discovered"), "Compass Structures Discovered");
    }

    public static String stat(String path)      { return JolCraftLanguageKeys.category(JolCraftLanguageCategory.STAT, path); }

}
