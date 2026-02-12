package net.sievert.jolcraft.data.id.item;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftCreativeTabIds extends JolCraftIds {

    private JolCraftCreativeTabIds(){}

    public static final String JOLCRAFT_GENERAL_CREATIVE_TAB = tab(plural(JolCraftDictionary.ITEM));
    public static final String JOLCRAFT_EGG_CREATIVE_TAB = tab(plural(JolCraftDictionary.EGG));

    private static String tab(String name) {
        return modJoin(name, JolCraftDictionary.TAB);
    }
}
