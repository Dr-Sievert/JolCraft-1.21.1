package net.sievert.jolcraft.data.id.network;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public class JolCraftNetworkIds extends JolCraftIds {

    private JolCraftNetworkIds(){}

    public static final String PROTOCOL = "1.1";

    //C2S
    public static final String DWARF_SELECT_TRADE = join(JolCraftDictionary.DWARF, JolCraftDictionary.SELECT, JolCraftDictionary.TRADE);
    public static final String PLAY_SOUND = join(JolCraftDictionary.PLAY, JolCraftDictionary.SOUND);
    public static final String SPAWN_PARTICLE = join(JolCraftDictionary.SPAWN, JolCraftDictionary.PARTICLE);

    //S2C
    public static final String DELIRIUM_CURSE = JolCraftEffectIds.DELIRIUM_CURSE;
    public static final String DWARF_MERCHANT_OFFERS = join(JolCraftDictionary.DWARF, JolCraftDictionary.MERCHANT, plural(JolCraftDictionary.OFFER));
    public static final String REWARD_LOOT_TABLES = join(JolCraftDictionary.REWARD, JolCraftDictionary.LOOT, plural(JolCraftDictionary.TABLE));

}
