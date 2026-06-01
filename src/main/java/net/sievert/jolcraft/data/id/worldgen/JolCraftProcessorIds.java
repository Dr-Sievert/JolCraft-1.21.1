package net.sievert.jolcraft.data.id.worldgen;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftProcessorIds extends JolCraftIds {

    private JolCraftProcessorIds() {}

    public static final String RANDOM_REPLACE_WITH_LOOT = join(JolCraftDictionary.RANDOM, JolCraftDictionary.REPLACE, JolCraftDictionary.WITH, JolCraftDictionary.LOOT);
    public static final String STRUCTURE_VOID = join(JolCraftDictionary.STRUCTURE, JolCraftDictionary.VOID);
    public static final String ADD_LOOT_TABLE = join(JolCraftDictionary.ADD, JolCraftDictionary.LOOT, JolCraftDictionary.TABLE);
    public static final String RANDOM_COBWEB = join(JolCraftDictionary.RANDOM, JolCraftDictionary.COBWEB);
    public static final String RANDOM_CAVE_IN = join(JolCraftDictionary.RANDOM, JolCraftDictionary.CAVE, JolCraftDictionary.IN);
    public static final String LANTERN = JolCraftDictionary.LANTERN;
}
