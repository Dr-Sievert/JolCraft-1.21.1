package net.sievert.jolcraft.data.id.worldgen.template_pool;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
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
    public static final String ABANDONED = JolCraftDictionary.ABANDONED;
    public static final String BROKEN = JolCraftDictionary.BROKEN;
    public static final String MAIN =      JolCraftDictionary.MAIN;
    public static final String SHAFT =     JolCraftDictionary.SHAFT;
    public static final String CORRIDOR =  JolCraftDictionary.CORRIDOR;
    public static final String GARDEN =  JolCraftDictionary.GARDEN;
    public static final String HALL =  JolCraftDictionary.HALL;
    public static final String WALL =  JolCraftDictionary.WALL;
    public static final String VAULT =  JolCraftDictionary.VAULT;
    public static final String JUNCTION =  JolCraftDictionary.JUNCTION;
    public static final String TRANSITION = JolCraftDictionary.TRANSITION;
    public static final String CORNER = JolCraftDictionary.CORNER;
    public static final String MIDDLE = JolCraftDictionary.MIDDLE;
    public static final String ROOM = JolCraftDictionary.ROOM;
    public static final String STAIRS =    JolCraftDictionary.STAIRS;
    public static final String END =       JolCraftDictionary.END;
    public static final String COLLAPSED = JolCraftDictionary.COLLAPSED;
    public static final String TOWER = JolCraftDictionary.TOWER;
    public static final String CHAIN = JolCraftDictionary.CHAIN;
    public static final String LANTERN = JolCraftDictionary.LANTERN;
    public static final String CANDLE = JolCraftDictionary.CANDLE;
    public static final String BRAZIER = JolCraftDictionary.BRAZIER;
    public static final String STRONGBOX = JolCraftDictionary.STRONGBOX;
    public static final String STRONGBOX_LOCKED = join(STRONGBOX, JolCraftDictionary.LOCKED);
    public static final String GRAVEL = JolCraftDictionary.GRAVEL;
    public static final String DIGSITE = JolCraftDictionary.DIGSITE;
    public static final String FORGE = JolCraftDictionary.FORGE;
    public static final String CRUCIBLE = JolCraftDictionary.CRUCIBLE;
    public static final String ANVIL = JolCraftDictionary.ANVIL;
    public static final String POTTED = JolCraftDictionary.POTTED;
    public static final String BARREL = JolCraftDictionary.BARREL;
    public static final String FLOWER = JolCraftDictionary.FLOWER;
    public static final String FLOWER_BED = join(JolCraftDictionary.FLOWER, JolCraftDictionary.BED);
    public static final String LOOT = JolCraftDictionary.LOOT;

    public static final String LARGE_JUNCTION =  join(LARGE, JUNCTION);
    public static final String MEDIUM_JUNCTION = join(MEDIUM, JUNCTION);
    public static final String SMALL_JUNCTION =  join(SMALL, JUNCTION);
    public static final String LARGE_CORRIDOR =  join(LARGE, CORRIDOR);
    public static final String MEDIUM_CORRIDOR = join(MEDIUM, CORRIDOR);
    public static final String SMALL_CORRIDOR =  join(SMALL, CORRIDOR);

    public static final String CHAIN_1 = one(CHAIN);
    public static final String CHAIN_2 = two(CHAIN);
    public static final String CHAIN_3 = three(CHAIN);
    public static final String CHAIN_4 = four(CHAIN);

    public static final String LANTERN_1 = one(LANTERN);
    public static final String LANTERN_2 = two(LANTERN);
    public static final String LANTERN_3 = three(LANTERN);
    public static final String LANTERN_4 = four(LANTERN);
    public static final String LANTERN_5 = five(LANTERN);
    public static final String PLACED_LANTERN = join(JolCraftDictionary.PLACED, LANTERN);

    public static final String GRAVEL_1 = one(GRAVEL);
    public static final String GRAVEL_2 = two(GRAVEL);
    public static final String GRAVEL_3 = three(GRAVEL);
    public static final String GRAVEL_4 = four(GRAVEL);
    public static final String GRAVEL_5 = five(GRAVEL);

    public static final String BARREL_1 = one(BARREL);
    public static final String BARREL_2 = two(BARREL);
    public static final String BARREL_3 = three(BARREL);
    public static final String BARREL_4 = four(BARREL);
    public static final String BARREL_5 = five(BARREL);

    public static final String CANDLE_1 = one(CANDLE);
    public static final String CANDLE_2 = two(CANDLE);
    public static final String CANDLE_3 = three(CANDLE);
    public static final String CANDLE_4 = four(CANDLE);

    public static final String FLOWER_1 = one(FLOWER);
    public static final String FLOWER_2 = two(FLOWER);
    public static final String FLOWER_3 = three(FLOWER);
    public static final String FLOWER_4 = four(FLOWER);

    public static final String POTTED_AZALEA_BUSH = join(POTTED, JolCraftDictionary.AZALEA, JolCraftDictionary.BUSH);
    public static final String POTTED_FLOWERING_AZALEA_BUSH = join(POTTED, JolCraftDictionary.FLOWERING, JolCraftDictionary.AZALEA, JolCraftDictionary.BUSH);
    public static final String POTTED_FESTERLING = join(POTTED, JolCraftBlockIds.FESTERLING);
    public static final String POTTED_DUSKCAP = join(POTTED, JolCraftBlockIds.DUSKCAP);

    public static final String DEEPSLATE_MITHRIL_ORE = JolCraftBlockIds.DEEPSLATE_MITHRIL_ORE;
    public static final String PURE_MITHRIL_BLOCK = JolCraftBlockIds.PURE_MITHRIL_BLOCK;

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

    protected static String five(String name) {
        return JolCraftStrings.underscored(name, "5");
    }

}
