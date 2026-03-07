package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.advancement.JolCraftAdvancementIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.util.JolCraftStrings;

@OnlyIn(Dist.CLIENT)
public final class AdvancementsLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        add(p, JolCraftAdvancementIds.ROOT,
                "The Dwarven Path",
                "A journey through dwarven halls"
        );

        add(p, JolCraftAdvancementIds.READ_LEXICON,
                "Bilingual",
                "Learn to understand the dwarven language"
        );

        add(p, JolCraftAdvancementIds.REP_0_DUMMY,
                "What now?",
                "You are a stranger to most dwarves. You need to earn their trust."
        );

        add(p, JolCraftAdvancementIds.TRADE_DWARF,
                "Dwarven Commerce",
                "Trade with a dwarf"
        );

        add(p, JolCraftAdvancementIds.TRADE_HISTORIAN,
                "Curious Curator",
                "Trade with a historian"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_HISTORIAN,
                "Footnote in History",
                "Get endorsed by a masterTask historian"
        );

        add(p, JolCraftAdvancementIds.TRADE_MERCHANT,
                "Assorted Goods",
                "Trade with a merchant"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_MERCHANT,
                "Distinguished Fetcher",
                "Get endorsed by a masterTask merchant"
        );

        add(p, JolCraftAdvancementIds.TRADE_SCRAPPER,
                "First Salvage",
                "Trade with a scrapper"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_SCRAPPER,
                "Certified Scavenger",
                "Get endorsed by a masterTask scrapper"
        );

        add(p, JolCraftAdvancementIds.REP_1,
                "Known Face",
                "Reach this rank in reputation"
        );

        add(p, JolCraftAdvancementIds.REP_1_DUMMY,
                "New Faces",
                "More dwarves are now willing to interact with you."
        );

        add(p, JolCraftAdvancementIds.TRADE_BREWMASTER,
                "Toasting Traditions",
                "Trade with a brewmaster"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_BREWMASTER,
                "Honored in Hops",
                "Get endorsed by a masterTask brewmaster"
        );

        add(p, JolCraftAdvancementIds.TRADE_GUARD,
                "Shield and Service",
                "Trade with a guard"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_GUARD,
                "Writ of Protection",
                "Get endorsed by a masterTask guard"
        );

        add(p, JolCraftAdvancementIds.TRADE_KEEPER,
                "Granary Guest",
                "Trade with a keeper"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_KEEPER,
                "Favored of the Fold",
                "Get endorsed by a masterTask keeper"
        );

        add(p, JolCraftAdvancementIds.REP_2,
                "Trusted",
                "Reach this rank in reputation"
        );

        add(p, JolCraftAdvancementIds.REP_2_DUMMY,
                "Gaining Ground",
                "Your deeds echo through the halls. More dwarves are willing to work with you."
        );

        // ARTISAN
        add(p, JolCraftAdvancementIds.TRADE_ARTISAN,
                "Chisel and Craft",
                "Trade with an artisan"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_ARTISAN,
                "Polished to Perfection",
                "Get endorsed by a masterTask artisan"
        );

        // EXPLORER
        add(p, JolCraftAdvancementIds.TRADE_EXPLORER,
                "Charted Paths",
                "Trade with an explorer"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_EXPLORER,
                "Navigator of the Needle",
                "Get endorsed by a masterTask explorer"
        );

        // MINER
        add(p, JolCraftAdvancementIds.TRADE_MINER,
                "Vein Commission",
                "Trade with a miner"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_MINER,
                "Prime Pick",
                "Get endorsed by a masterTask miner"
        );

        add(p, JolCraftAdvancementIds.REP_3,
                "Respected",
                "Reach this rank in reputation"
        );

        add(p, JolCraftAdvancementIds.REP_3_DUMMY,
                "Resounding Renown",
                "Your reputation now precedes you in every corner of the dwarven realm."
        );

        // ALCHEMIST
        add(p, JolCraftAdvancementIds.TRADE_ALCHEMIST,
                "Mortar and Mixture",
                "Trade with an alchemist"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_ALCHEMIST,
                "Perfected Formula",
                "Get endorsed by a masterTask alchemist"
        );

        // ARCANIST
        add(p, JolCraftAdvancementIds.TRADE_ARCANIST,
                "Runes and Resonance",
                "Trade with an arcanist"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_ARCANIST,
                "Sigil of the Arcane",
                "Get endorsed by a masterTask arcanist"
        );

        // PRIEST
        add(p, JolCraftAdvancementIds.TRADE_PRIEST,
                "Blessed Exchange",
                "Trade with a priest"
        );

        add(p, JolCraftAdvancementIds.ENDORSE_PRIEST,
                "Lightbearer",
                "Get endorsed by a masterTask priest"
        );

        add(p, JolCraftAdvancementIds.REP_4,
                "Blood-Kin",
                "Reach this rank in reputation"
        );

        add(p, JolCraftAdvancementIds.REP_4_DUMMY,
                "Legacy Forged",
                "You are recognized as kin by any dwarf."
        );
    }

    private static void add(
            AbstractLanguageProvider p,
            String idPath,
            String title,
            String description
    ) {
        String base = JolCraftStrings.dotted(
                JolCraftDictionary.ADVANCEMENT,
                JolCraft.MOD_ID,
                idPath
        );

        p.putManual(JolCraftStrings.dotted(base, JolCraftDictionary.TITLE), title);
        p.putManual(JolCraftStrings.dotted(base, JolCraftDictionary.DESCRIPTION), description);
    }
}