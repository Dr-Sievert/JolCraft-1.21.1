package net.sievert.jolcraft.data.id.item;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.data_component.JolCraftDataComponentIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftItemPropertyIds extends JolCraftIds {

    private JolCraftItemPropertyIds() {}

    // Select item model properties
    public static final String COIN_POUCH_AMOUNT = JolCraftDataComponentIds.COIN_POUCH_AMOUNT;
    public static final String LORE_KEY = JolCraftDataComponentIds.DWARF_LORE_KEY;
    public static final String REWARD_CRATE_SOURCE  = JolCraftDataComponentIds.REWARD_CRATE_SOURCE;

    // Range select item model properties
    public static final String DEEPSLATE_COMPASS_ANGLE = join(JolCraftItemIds.DEEPSLATE_COMPASS, "angle");
}
