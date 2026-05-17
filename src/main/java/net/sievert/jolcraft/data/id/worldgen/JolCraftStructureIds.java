package net.sievert.jolcraft.data.id.worldgen;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftStructureIds extends JolCraftIds {

    private JolCraftStructureIds() {}

    public static final String FORGE = JolCraftDictionary.FORGE;
    public static final String DWARVEN_FORTRESS = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.FORTRESS);
    public static final String DWARVEN_TRAIL_RUIN = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.TRAIL, JolCraftDictionary.RUIN);
}
