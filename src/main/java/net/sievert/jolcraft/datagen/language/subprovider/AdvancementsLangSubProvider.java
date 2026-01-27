package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.advancement.AdvancementKey;
import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;

public final class AdvancementsLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        add(p, AdvancementKey.ROOT,
                "The Dwarven Path",
                "A journey through dwarven halls"
        );

        add(p, AdvancementKey.READ_LEXICON,
                "Bilingual",
                "Learn to understand the dwarven language"
        );

        add(p, AdvancementKey.REP_0_DUMMY,
                "What now?",
                "You are a stranger to most dwarves. You need to earn their trust."
        );

        add(p, AdvancementKey.TRADE_DWARF,
                "Dwarven Commerce",
                "Trade with a dwarf"
        );

        add(p, AdvancementKey.TRADE_HISTORIAN,
                "Curious Curator",
                "Trade with a historian"
        );

        add(p, AdvancementKey.ENDORSE_HISTORIAN,
                "Footnote in History",
                "Get endorsed by a master historian"
        );

        add(p, AdvancementKey.TRADE_MERCHANT,
                "Assorted Goods",
                "Trade with a merchant"
        );

        add(p, AdvancementKey.ENDORSE_MERCHANT,
                "Distinguished Fetcher",
                "Get endorsed by a master merchant"
        );

        add(p, AdvancementKey.TRADE_SCRAPPER,
                "First Salvage",
                "Trade with a scrapper"
        );

        add(p, AdvancementKey.ENDORSE_SCRAPPER,
                "Certified Scavenger",
                "Get endorsed by a master scrapper"
        );

        add(p, AdvancementKey.REP_1,
                "Known Face",
                "Reach this rank in reputation"
        );

        add(p, AdvancementKey.REP_1_DUMMY,
                "New Faces",
                "More dwarves are now willing to interact with you."
        );

        add(p, AdvancementKey.TRADE_BREWMASTER,
                "Toasting Traditions",
                "Trade with a brewmaster"
        );

        add(p, AdvancementKey.ENDORSE_BREWMASTER,
                "Honored in Hops",
                "Get endorsed by a master brewmaster"
        );

        add(p, AdvancementKey.TRADE_GUARD,
                "Shield and Service",
                "Trade with a guard"
        );

        add(p, AdvancementKey.ENDORSE_GUARD,
                "Writ of Protection",
                "Get endorsed by a master guard"
        );

        add(p, AdvancementKey.REP_2,
                "Trusted",
                "Reach this rank in reputation"
        );

        add(p, AdvancementKey.REP_2_DUMMY,
                "Gaining Ground",
                "Your deeds echo through the halls. More dwarves are willing to work with you."
        );

        add(p, AdvancementKey.REP_3,
                "Respected",
                "Reach this rank in reputation"
        );

        add(p, AdvancementKey.REP_3_DUMMY,
                "Resounding Renown",
                "Your reputation now precedes you in every corner of the dwarven realm."
        );

        add(p, AdvancementKey.REP_4,
                "Blood-Kin",
                "Reach this rank in reputation"
        );

        add(p, AdvancementKey.REP_4_DUMMY,
                "Legacy Forged",
                "You are celebrated as family by any dwarf."
        );
    }

    private static void add(AbstractLanguageProvider p,
                            AdvancementKey key,
                            String title,
                            String description) {

        String base = "advancement.jolcraft." + key.id();
        p.putManual(base + ".title", title);
        p.putManual(base + ".description", description);
    }
}