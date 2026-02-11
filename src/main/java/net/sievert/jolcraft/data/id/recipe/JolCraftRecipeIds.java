package net.sievert.jolcraft.data.id.recipe;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.key.JolCraftDictionary;

public final class JolCraftRecipeIds extends JolCraftIds {

    private JolCraftRecipeIds(){}

    public static final String ATTRIBUTE_SMITHING_TRIM = "attribute_smithing_trim";
    public static final String DWARF_TRADE = join(JolCraftDwarfIds.DWARF, JolCraftDictionary.TRADE);
    public static final String LAPIDARY_BENCH = JolCraftBlockIds.LAPIDARY_BENCH;
    public static final String FERMENTING_CAULDRON = JolCraftBlockIds.FERMENTING_CAULDRON;
}
