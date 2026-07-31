package net.sievert.jolcraft.data.id.worldgen;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftFeatureIds extends JolCraftIds {

    private JolCraftFeatureIds() {}

    public static final String BASALT_GEODE = join(JolCraftDictionary.BASALT, JolCraftDictionary.GEODE);
    public static final String HUGE_DUSKCAP = join(JolCraftDictionary.HUGE, JolCraftBlockIds.DUSKCAP);
    public static final String HUGE_FESTERLING = join(JolCraftDictionary.HUGE, JolCraftBlockIds.FESTERLING);
}