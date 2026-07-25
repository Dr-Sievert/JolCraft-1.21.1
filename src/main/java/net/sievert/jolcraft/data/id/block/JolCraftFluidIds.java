package net.sievert.jolcraft.data.id.block;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public class JolCraftFluidIds extends JolCraftIds {

    private JolCraftFluidIds() {}

    public static final String DWARVEN_BREW = JolCraftItemIds.DWARVEN_BREW;
    public static final String UNFINISHED_DWARVEN_BREW = join(JolCraftDictionary.UNFINISHED, DWARVEN_BREW);
}

