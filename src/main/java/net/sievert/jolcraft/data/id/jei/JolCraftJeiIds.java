package net.sievert.jolcraft.data.id.jei;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public class JolCraftJeiIds extends JolCraftIds {

    private JolCraftJeiIds(){}

    public static final String JEI_PLUGIN = join(JolCraftDictionary.JEI, JolCraftDictionary.PLUGIN);

    public static final String INFO_PAGE = join(JolCraftDictionary.INFO, JolCraftDictionary.PAGE);
    public static final String DWARF_TRADE = JolCraftRecipeIds.DWARF_TRADE;
    public static final String LAPIDARY_BENCH = JolCraftRecipeIds.LAPIDARY_BENCH;
    public static final String HAND_INTERACTION = JolCraftRecipeIds.HAND_INTERACTION;
    public static final String FERMENTING_CAULDRON = JolCraftRecipeIds.FERMENTING_CAULDRON;
    public static final String FERMENTING_BARREL = JolCraftBlockIds.FERMENTING_BARREL;
    public static final String BOUNTY_TASK = JolCraftRecipeIds.BOUNTY_TASK;
    public static final String BOUNTY_REWARD = JolCraftRecipeIds.BOUNTY_REWARD;
}
