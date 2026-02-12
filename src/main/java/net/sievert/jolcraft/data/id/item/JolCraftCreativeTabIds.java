package net.sievert.jolcraft.data.id.item;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftCreativeTabIds extends JolCraftIds {

    private JolCraftCreativeTabIds(){}

    public static final String JOLCRAFT_GENERAL_CREATIVE_TAB = tab("items");
    public static final String JOLCRAFT_EGG_CREATIVE_TAB = tab("eggs");

    private static String tab(String name) {
        return modJoin(name, JolCraftDictionary.TAB);
    }
}
