package net.sievert.jolcraft.data.id.network;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public class JolCraftNetworkIds extends JolCraftIds {

    private JolCraftNetworkIds(){}

    public static final String PROTOCOL = "1.0";

    //C2S
    public static final String DWARF_SELECT_TRADE = join(JolCraftDictionary.DWARF, JolCraftDictionary.SELECT, JolCraftDictionary.TRADE);
    public static final String PLAY_SOUND = join(JolCraftDictionary.PLAY, JolCraftDictionary.SOUND);
    public static final String SPAWN_PARTICLE = join(JolCraftDictionary.SPAWN, JolCraftDictionary.PARTICLE);

    //S2C
    public static final String DELIRIUM_CURSE = JolCraftEffectIds.DELIRIUM_CURSE;
    public static final String DWARF_MERCHANT_OFFERS = join(JolCraftDictionary.DWARF, JolCraftDictionary.MERCHANT, plural(JolCraftDictionary.OFFER));
    public static final String SYNC_ANCIENT_DWARVEN_LANGUAGE = join(JolCraftDictionary.SYNC, JolCraftAttachmentIds.ANCIENT_DWARVEN_LANGUAGE);
    public static final String SYNC_DWARVEN_LANGUAGE = join(JolCraftDictionary.SYNC, JolCraftAttachmentIds.DWARVEN_LANGUAGE);
    public static final String SYNC_DWARF_TOME_UNLOCKS = join(JolCraftDictionary.SYNC, JolCraftAttachmentIds.DWARF_TOME_UNLOCKS);
    public static final String SYNC_DWARVEN_REPUTATION = join(JolCraftDictionary.SYNC, JolCraftAttachmentIds.DWARVEN_REPUTATION);
    public static final String SYNC_DWARVEN_ENDORSEMENTS = join(JolCraftDictionary.SYNC, JolCraftDictionary.DWARVEN, plural(JolCraftDictionary.ENDORSEMENT));

}
