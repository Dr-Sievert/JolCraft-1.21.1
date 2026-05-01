package net.sievert.jolcraft.data.id.loot;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftLootModifierIds extends JolCraftIds {

    private JolCraftLootModifierIds() {}

    public static final String ADD_ITEM = join(JolCraftDictionary.ADD, JolCraftDictionary.ITEM);
    public static final String REPLACE_WITH_ITEM = join(JolCraftDictionary.REPLACE, JolCraftDictionary.WITH, JolCraftDictionary.ITEM);
}
