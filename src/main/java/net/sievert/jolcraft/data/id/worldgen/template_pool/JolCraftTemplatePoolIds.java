package net.sievert.jolcraft.data.id.worldgen.template_pool;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public abstract class JolCraftTemplatePoolIds extends JolCraftIds {

    public static final String START = JolCraftDictionary.START;
    public static final String ENTRANCE = JolCraftDictionary.ENTRANCE;
    public static final String RIGHT =     JolCraftDictionary.RIGHT;
    public static final String LEFT =      JolCraftDictionary.LEFT;
    public static final String LARGE =     JolCraftDictionary.LARGE;
    public static final String MEDIUM =    JolCraftDictionary.MEDIUM;
    public static final String SMALL =     JolCraftDictionary.SMALL;
    public static final String MAIN =      JolCraftDictionary.MAIN;
    public static final String SHAFT =     JolCraftDictionary.SHAFT;
    public static final String CORRIDOR =  JolCraftDictionary.CORRIDOR;
    public static final String JUNCTION =  JolCraftDictionary.JUNCTION;
    public static final String STAIRS =    JolCraftDictionary.STAIRS;
    public static final String END =       JolCraftDictionary.END;
    public static final String COLLAPSED = JolCraftDictionary.COLLAPSED;
    public static final String TOWER = JolCraftDictionary.TOWER;
    public static final String CHAIN = JolCraftDictionary.CHAIN;
    public static final String LANTERN = JolCraftDictionary.LANTERN;
    public static final String BRAZIER = JolCraftDictionary.BRAZIER;

    public static final String LARGE_JUNCTION =  join(LARGE, JUNCTION);
    public static final String MEDIUM_JUNCTION = join(MEDIUM, JUNCTION);
    public static final String SMALL_JUNCTION =  join(SMALL, JUNCTION);
    public static final String LARGE_CORRIDOR =  join(LARGE, CORRIDOR);
    public static final String MEDIUM_CORRIDOR = join(MEDIUM, CORRIDOR);
    public static final String SMALL_CORRIDOR =  join(SMALL, CORRIDOR);

    public static final String CHAIN_1 = one(CHAIN);
    public static final String CHAIN_2 = two(CHAIN);
    public static final String CHAIN_3 = three(CHAIN);

    public static final String LANTERN_1 = one(LANTERN);
    public static final String LANTERN_2 = two(LANTERN);
    public static final String LANTERN_3 = three(LANTERN);
    public static final String LANTERN_4 = four(LANTERN);

    protected static String one(String name) {
        return JolCraftStrings.underscored(name, "1");
    }

    protected static String two(String name) {
        return JolCraftStrings.underscored(name, "2");
    }

    protected static String three(String name) {
        return JolCraftStrings.underscored(name, "3");
    }

    protected static String four(String name) {
        return JolCraftStrings.underscored(name, "4");
    }
}
