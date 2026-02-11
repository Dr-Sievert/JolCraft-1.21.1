package net.sievert.jolcraft.data.id.network;

import net.sievert.jolcraft.data.id.JolCraftIds;

public class JolCraftNetworkIds extends JolCraftIds {

    private JolCraftNetworkIds(){}

    public static final String PROTOCOL = "1.0";

    //C2S
    public static final String DWARF_SELECT_TRADE = "dwarf_select_trade";
    public static final String PLAY_SOUND = "play_sound";
    public static final String SPAWN_PARTICLE = "spawn_particle";

    //S2C
    public static final String SYNC_ANCIENT_DWARVEN_LANGUAGE = "sync_ancient_dwarven_language";
    public static final String DELIRIUM_CURSE = "delirium_curse";
    public static final String DWARF_MERCHANT_OFFERS = "dwarf_merchant_offers";
    public static final String SYNC_ENDORSEMENTS = "sync_endorsements";
    public static final String SYNC_DWARVEN_LANGUAGE = "sync_dwarven_language";
    public static final String SYNC_TOME_UNLOCKS = "sync_tome_unlocks";
    public static final String SYNC_REPUTATION = "sync_reputation";

}
