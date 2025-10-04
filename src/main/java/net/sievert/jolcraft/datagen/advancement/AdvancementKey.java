package net.sievert.jolcraft.datagen.advancement;

public enum AdvancementKey {
    ROOT,
    READ_LEXICON,

    REP_0_DUMMY,
    TRADE_DWARF,

    TRADE_HISTORIAN,
    ENDORSE_HISTORIAN,

    TRADE_MERCHANT,
    ENDORSE_MERCHANT,

    TRADE_SCRAPPER,
    ENDORSE_SCRAPPER,

    REP_1,
    REP_1_DUMMY,

    TRADE_BREWMASTER,
    ENDORSE_BREWMASTER,

    TRADE_GUARD,
    ENDORSE_GUARD,

    TRADE_KEEPER,
    ENDORSE_KEEPER,

    REP_2,
    REP_2_DUMMY;

    public String id() {
        return name().toLowerCase();
    }
}
