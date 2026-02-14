package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.stat.JolCraftStatIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;

@OnlyIn(Dist.CLIENT)
public class StatLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        p.putManual(stat(JolCraftStatIds.TALK_TO_DWARF), "Talked to Dwarves");
        p.putManual(stat(JolCraftStatIds.TRADE_WITH_DWARF), "Traded with Dwarves");
        p.putManual(stat(JolCraftStatIds.COINS_SPENT), "Coins spent");

        p.putManual(stat(JolCraftStatIds.DWARVEN_TOMES_IDENTIFIED), "Dwarven tomes identified");
        p.putManual(stat(JolCraftStatIds.DWARVEN_BOUNTIES_COMPLETED), "Dwarven bounties completed");
        p.putManual(stat(JolCraftStatIds.DWARVEN_BREWS_CREATED), "Dwarven brews created");

        p.putManual(stat(JolCraftStatIds.DISCOVERED_STRUCTURES), "Deepslate Compass Structures Discovered");

        p.putManual(stat(JolCraftStatIds.GEODES_CRACKED), "Geodes cracked");
        p.putManual(stat(JolCraftStatIds.GEMS_CRUSHED), "Gems crushed");
        p.putManual(stat(JolCraftStatIds.GEMS_CUT), "Gems cut");
    }

    public static String stat(String path) { return AbstractLanguageKeys.category(JolCraftDictionary.STAT, path); }
}
