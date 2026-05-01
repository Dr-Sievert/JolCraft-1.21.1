package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.stat.JolCraftStatIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;

@OnlyIn(Dist.CLIENT)
public class StatLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.STAT);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        putManual(translations, stat(JolCraftStatIds.TALK_TO_DWARF), "Talked to Dwarves");
        putManual(translations, stat(JolCraftStatIds.TRADE_WITH_DWARF), "Traded with Dwarves");
        putManual(translations, stat(JolCraftStatIds.COINS_SPENT), "Coins spent");

        putManual(translations, stat(JolCraftStatIds.DWARVEN_TOMES_IDENTIFIED), "Dwarven tomes identified");
        putManual(translations, stat(JolCraftStatIds.DWARVEN_BOUNTIES_COMPLETED), "Dwarven bounties completed");
        putManual(translations, stat(JolCraftStatIds.DWARVEN_BREWS_CREATED), "Dwarven brews created");

        putManual(translations, stat(JolCraftStatIds.DISCOVERED_STRUCTURES), "Deepslate Compass Structures Discovered");

        putManual(translations, stat(JolCraftStatIds.GEODES_CRACKED), "Geodes cracked");
        putManual(translations, stat(JolCraftStatIds.GEMS_CRUSHED), "Gems crushed");
        putManual(translations, stat(JolCraftStatIds.GEMS_CUT), "Gems cut");
    }

    public static String stat(String path) { return AbstractLanguageKeys.category(JolCraftDictionary.STAT, path); }
}
