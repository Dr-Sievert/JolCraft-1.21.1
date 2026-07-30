package net.sievert.jolcraft.data.id.advancement;

import net.sievert.jolcraft.world.player.attachment.custom.reputation.DwarvenReputationAttachment;
import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftAdvancementIds extends JolCraftIds {

    private JolCraftAdvancementIds() {}

    // ---------------------------------------------------------------------
    // IDs
    // ---------------------------------------------------------------------

    public static final String ROOT_1 = join(JolCraftDictionary.ROOT, "1");

    public static final String READ_LEXICON = join(JolCraftDictionary.READ, JolCraftDictionary.LEXICON);

    public static final String READ_ANCIENT_LEXICON = join(JolCraftDictionary.READ, JolCraftDictionary.ANCIENT, JolCraftDictionary.LEXICON);

    public static final String REP_0 = reputation(DwarvenReputationAttachment.Tier.STRANGER);
    public static final String REP_0_DUMMY = join(REP_0, JolCraftDictionary.DUMMY);

    public static final String TRADE_DWARF = join(JolCraftDictionary.TRADE, JolCraftDictionary.DWARF);

    public static final String TRADE_HISTORIAN = join(JolCraftDictionary.TRADE, JolCraftDictionary.HISTORIAN);
    public static final String ENDORSE_HISTORIAN = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.HISTORIAN);

    public static final String TRADE_MERCHANT = join(JolCraftDictionary.TRADE, JolCraftDictionary.MERCHANT);
    public static final String ENDORSE_MERCHANT = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.MERCHANT);

    public static final String TRADE_SCRAPPER = join(JolCraftDictionary.TRADE, JolCraftDictionary.SCRAPPER);
    public static final String ENDORSE_SCRAPPER = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.SCRAPPER);

    public static final String REP_1 = reputation(DwarvenReputationAttachment.Tier.KNOWN_FACE);
    public static final String REP_1_DUMMY = join(REP_1, JolCraftDictionary.DUMMY);

    public static final String TRADE_BREWMASTER = join(JolCraftDictionary.TRADE, JolCraftDictionary.BREWMASTER);
    public static final String ENDORSE_BREWMASTER = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.BREWMASTER);

    public static final String TRADE_GUARD = join(JolCraftDictionary.TRADE, JolCraftDictionary.GUARD);
    public static final String ENDORSE_GUARD = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.GUARD);

    public static final String TRADE_KEEPER = join(JolCraftDictionary.TRADE, JolCraftDictionary.KEEPER);
    public static final String ENDORSE_KEEPER = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.KEEPER);

    public static final String REP_2 = reputation(DwarvenReputationAttachment.Tier.TRUSTED);
    public static final String REP_2_DUMMY = join(REP_2, JolCraftDictionary.DUMMY);

    public static final String TRADE_ARTISAN = join(JolCraftDictionary.TRADE, JolCraftDictionary.ARTISAN);
    public static final String ENDORSE_ARTISAN = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.ARTISAN);

    public static final String TRADE_EXPLORER = join(JolCraftDictionary.TRADE, JolCraftDictionary.EXPLORER);
    public static final String ENDORSE_EXPLORER = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.EXPLORER);

    public static final String TRADE_MINER = join(JolCraftDictionary.TRADE, JolCraftDictionary.MINER);
    public static final String ENDORSE_MINER = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.MINER);

    public static final String REP_3 = reputation(DwarvenReputationAttachment.Tier.RESPECTED);
    public static final String REP_3_DUMMY = join(REP_3, JolCraftDictionary.DUMMY);

    public static final String TRADE_ALCHEMIST = join(JolCraftDictionary.TRADE, JolCraftDictionary.ALCHEMIST);
    public static final String ENDORSE_ALCHEMIST = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.ALCHEMIST);

    public static final String TRADE_ARCANIST = join(JolCraftDictionary.TRADE, JolCraftDictionary.ARCANIST);
    public static final String ENDORSE_ARCANIST = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.ARCANIST);

    public static final String TRADE_PRIEST = join(JolCraftDictionary.TRADE, JolCraftDictionary.PRIEST);
    public static final String ENDORSE_PRIEST = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.PRIEST);

    public static final String REP_4 = reputation(DwarvenReputationAttachment.Tier.BLOOD_KIN);
    public static final String REP_4_DUMMY = join(REP_4, JolCraftDictionary.DUMMY);

    public static final String TRADE_BLACKSMITH = join(JolCraftDictionary.TRADE, JolCraftDictionary.BLACKSMITH);
    public static final String ENDORSE_BLACKSMITH = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.BLACKSMITH);

    public static final String TRADE_CHAMPION = join(JolCraftDictionary.TRADE, JolCraftDictionary.CHAMPION);
    public static final String ENDORSE_CHAMPION = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.CHAMPION);

    public static final String TRADE_SMELTER = join(JolCraftDictionary.TRADE, JolCraftDictionary.SMELTER);
    public static final String ENDORSE_SMELTER = join(JolCraftDictionary.ENDORSE, JolCraftDictionary.SMELTER);

    // ---------------------------------------------------------------------
    // Util
    // ---------------------------------------------------------------------

    public static String reputation(DwarvenReputationAttachment.Tier tier) {
        return join(
                JolCraftDictionary.REPUTATION,
                tier.getSerializedName()
        );
    }
}