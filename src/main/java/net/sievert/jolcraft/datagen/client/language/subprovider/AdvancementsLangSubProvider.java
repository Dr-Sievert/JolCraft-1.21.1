package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.advancement.JolCraftAdvancementIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;

import java.util.Map;

import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class AdvancementsLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.ADVANCEMENT);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        add(translations, JolCraftAdvancementIds.ROOT,
                "The Dwarven Path",
                "A journey through dwarven halls"
        );

        add(translations, JolCraftAdvancementIds.READ_LEXICON,
                "Bilingual",
                "Learn to understand the dwarven language"
        );

        add(translations, JolCraftAdvancementIds.READ_ANCIENT_LEXICON,
                "Lost Language",
                "Learn to understand the ancient dwarven language"
        );

        add(translations, JolCraftAdvancementIds.REP_0_DUMMY,
                "What now?",
                "You are a stranger to most dwarves. You need to earn their trust."
        );

        add(translations, JolCraftAdvancementIds.TRADE_DWARF,
                "Dwarven Commerce",
                "Trade with a dwarf"
        );

        add(translations, JolCraftAdvancementIds.TRADE_HISTORIAN,
                "Curious Curator",
                "Trade with a historian"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_HISTORIAN,
                "Footnote in History",
                "Get endorsed by a master-level historian"
        );

        add(translations, JolCraftAdvancementIds.TRADE_MERCHANT,
                "Assorted Goods",
                "Trade with a merchant"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_MERCHANT,
                "Distinguished Fetcher",
                "Get endorsed by a master-level merchant"
        );

        add(translations, JolCraftAdvancementIds.TRADE_SCRAPPER,
                "First Salvage",
                "Trade with a scrapper"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_SCRAPPER,
                "Certified Scavenger",
                "Get endorsed by a master-level scrapper"
        );

        add(translations, JolCraftAdvancementIds.REP_1,
                "Known Face",
                "Reach this rank in reputation"
        );

        add(translations, JolCraftAdvancementIds.REP_1_DUMMY,
                "New Faces",
                "More dwarves are now willing to interact with you."
        );

        add(translations, JolCraftAdvancementIds.TRADE_BREWMASTER,
                "Toasting Traditions",
                "Trade with a brewmaster"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_BREWMASTER,
                "Honored in Hops",
                "Get endorsed by a master-level brewmaster"
        );

        add(translations, JolCraftAdvancementIds.TRADE_GUARD,
                "Shield and Service",
                "Trade with a guard"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_GUARD,
                "Writ of Protection",
                "Get endorsed by a master-level guard"
        );

        add(translations, JolCraftAdvancementIds.TRADE_KEEPER,
                "Granary Guest",
                "Trade with a keeper"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_KEEPER,
                "Favored of the Fold",
                "Get endorsed by a master-level keeper"
        );

        add(translations, JolCraftAdvancementIds.REP_2,
                "Trusted",
                "Reach this rank in reputation"
        );

        add(translations, JolCraftAdvancementIds.REP_2_DUMMY,
                "Gaining Ground",
                "Your deeds echo through the halls. More dwarves are willing to work with you."
        );

        // ARTISAN
        add(translations, JolCraftAdvancementIds.TRADE_ARTISAN,
                "Chisel and Craft",
                "Trade with an artisan"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_ARTISAN,
                "Polished to Perfection",
                "Get endorsed by a master-level artisan"
        );

        // EXPLORER
        add(translations, JolCraftAdvancementIds.TRADE_EXPLORER,
                "Charted Paths",
                "Trade with an explorer"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_EXPLORER,
                "Navigator of the Needle",
                "Get endorsed by a master-level explorer"
        );

        // MINER
        add(translations, JolCraftAdvancementIds.TRADE_MINER,
                "Vein Commission",
                "Trade with a miner"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_MINER,
                "Prime Pick",
                "Get endorsed by a master-level miner"
        );

        add(translations, JolCraftAdvancementIds.REP_3,
                "Respected",
                "Reach this rank in reputation"
        );

        add(translations, JolCraftAdvancementIds.REP_3_DUMMY,
                "Resounding Renown",
                "Your reputation now precedes you in every corner of the dwarven realm."
        );

        // ALCHEMIST
        add(translations, JolCraftAdvancementIds.TRADE_ALCHEMIST,
                "Mortar and Mixture",
                "Trade with an alchemist"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_ALCHEMIST,
                "Perfected Formula",
                "Get endorsed by a master-level alchemist"
        );

        // ARCANIST
        add(translations, JolCraftAdvancementIds.TRADE_ARCANIST,
                "Runes and Resonance",
                "Trade with an arcanist"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_ARCANIST,
                "Sigil of the Arcane",
                "Get endorsed by a master-level arcanist"
        );

        // PRIEST
        add(translations, JolCraftAdvancementIds.TRADE_PRIEST,
                "Blessed Exchange",
                "Trade with a priest"
        );

        add(translations, JolCraftAdvancementIds.ENDORSE_PRIEST,
                "Lightbearer",
                "Get endorsed by a master-level priest"
        );

        add(translations, JolCraftAdvancementIds.REP_4,
                "Blood-Kin",
                "Reach this rank in reputation"
        );

        add(translations, JolCraftAdvancementIds.REP_4_DUMMY,
                "Legacy Forged",
                "You are recognized as kin by any dwarf."
        );
    }

    private void add(
            Map<String, String> translations,
            String idPath,
            String title,
            String description
    ) {
        String base = JolCraftStrings.dotted(
                JolCraftDictionary.ADVANCEMENT,
                JolCraft.MOD_ID,
                idPath
        );

        putManual(translations, JolCraftStrings.dotted(base, JolCraftDictionary.TITLE), title);
        putManual(translations, JolCraftStrings.dotted(base, JolCraftDictionary.DESCRIPTION), description);
    }
}
