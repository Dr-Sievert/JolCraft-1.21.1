package net.sievert.jolcraft.data.id.worldgen.template_pool;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

@SuppressWarnings("SameParameterValue")
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
    public static final String ARCHIVES = plural(JolCraftDictionary.ARCHIVE);
    public static final String BOOKSHELF = JolCraftDictionary.BOOKSHELF;
    public static final String CATACOMBS = plural(JolCraftDictionary.CATACOMB);
    public static final String TOMB = JolCraftDictionary.TOMB;
    public static final String TOWN = JolCraftDictionary.TOWN;

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

    public static final String POTTED_AZALEA_BUSH = join(POTTED, JolCraftDictionary.AZALEA, JolCraftDictionary.BUSH);
    public static final String POTTED_FLOWERING_AZALEA_BUSH = join(POTTED, JolCraftDictionary.FLOWERING, JolCraftDictionary.AZALEA, JolCraftDictionary.BUSH);
    public static final String POTTED_FESTERLING = join(POTTED, JolCraftBlockIds.FESTERLING);
    public static final String POTTED_DUSKCAP = join(POTTED, JolCraftBlockIds.DUSKCAP);

    public static final String DEEPSLATE_MITHRIL_ORE = JolCraftBlockIds.DEEPSLATE_MITHRIL_ORE;
    public static final String PURE_MITHRIL_BLOCK = JolCraftBlockIds.PURE_MITHRIL_BLOCK;

    public static final String BOOKSHELF_1 = one(BOOKSHELF);
    public static final String BOOKSHELF_2 = two(BOOKSHELF);
    public static final String BOOKSHELF_3 = three(BOOKSHELF);
    public static final String BOOKSHELF_4 = four(BOOKSHELF);
    public static final String BOOKSHELF_5 = five(BOOKSHELF);
    public static final String BOOKSHELF_6 = six(BOOKSHELF);
    public static final String BOOKSHELF_7 = seven(BOOKSHELF);
    public static final String BOOKSHELF_8 = eight(BOOKSHELF);
    public static final String BOOKSHELF_9 = nine(BOOKSHELF);
    public static final String BOOKSHELF_10 = ten(BOOKSHELF);
    public static final String BOOKSHELF_11 = eleven(BOOKSHELF);
    public static final String BOOKSHELF_12 = twelve(BOOKSHELF);
    public static final String BOOKSHELF_13 = thirteen(BOOKSHELF);
    public static final String BOOKSHELF_14 = fourteen(BOOKSHELF);
    public static final String BOOKSHELF_15 = fifteen(BOOKSHELF);
    public static final String BOOKSHELF_16 = sixteen(BOOKSHELF);
    public static final String BOOKSHELF_17 = seventeen(BOOKSHELF);
    public static final String BOOKSHELF_18 = eighteen(BOOKSHELF);
    public static final String BOOKSHELF_19 = nineteen(BOOKSHELF);
    public static final String BOOKSHELF_20 = twenty(BOOKSHELF);
    public static final String BOOKSHELF_21 = twentyOne(BOOKSHELF);
    public static final String BOOKSHELF_22 = twentyTwo(BOOKSHELF);
    public static final String BOOKSHELF_23 = twentyThree(BOOKSHELF);
    public static final String BOOKSHELF_24 = twentyFour(BOOKSHELF);
    public static final String BOOKSHELF_25 = twentyFive(BOOKSHELF);
    public static final String BOOKSHELF_26 = twentySix(BOOKSHELF);
    public static final String BOOKSHELF_27 = twentySeven(BOOKSHELF);
    public static final String BOOKSHELF_28 = twentyEight(BOOKSHELF);
    public static final String BOOKSHELF_29 = twentyNine(BOOKSHELF);
    public static final String BOOKSHELF_30 = thirty(BOOKSHELF);
    public static final String BOOKSHELF_31 = thirtyOne(BOOKSHELF);
    public static final String BOOKSHELF_32 = thirtyTwo(BOOKSHELF);
    public static final String BOOKSHELF_33 = thirtyThree(BOOKSHELF);
    public static final String BOOKSHELF_34 = thirtyFour(BOOKSHELF);
    public static final String BOOKSHELF_35 = thirtyFive(BOOKSHELF);
    public static final String BOOKSHELF_36 = thirtySix(BOOKSHELF);
    public static final String BOOKSHELF_37 = thirtySeven(BOOKSHELF);
    public static final String BOOKSHELF_38 = thirtyEight(BOOKSHELF);
    public static final String BOOKSHELF_39 = thirtyNine(BOOKSHELF);
    public static final String BOOKSHELF_40 = forty(BOOKSHELF);
    public static final String BOOKSHELF_41 = fortyOne(BOOKSHELF);
    public static final String BOOKSHELF_42 = fortyTwo(BOOKSHELF);
    public static final String BOOKSHELF_43 = fortyThree(BOOKSHELF);
    public static final String BOOKSHELF_44 = fortyFour(BOOKSHELF);
    public static final String BOOKSHELF_45 = fortyFive(BOOKSHELF);
    public static final String BOOKSHELF_46 = fortySix(BOOKSHELF);
    public static final String BOOKSHELF_47 = fortySeven(BOOKSHELF);
    public static final String BOOKSHELF_48 = fortyEight(BOOKSHELF);
    public static final String BOOKSHELF_49 = fortyNine(BOOKSHELF);
    public static final String BOOKSHELF_50 = fifty(BOOKSHELF);
    public static final String BOOKSHELF_51 = fiftyOne(BOOKSHELF);
    public static final String BOOKSHELF_52 = fiftyTwo(BOOKSHELF);
    public static final String BOOKSHELF_53 = fiftyThree(BOOKSHELF);
    public static final String BOOKSHELF_54 = fiftyFour(BOOKSHELF);
    public static final String BOOKSHELF_55 = fiftyFive(BOOKSHELF);
    public static final String BOOKSHELF_56 = fiftySix(BOOKSHELF);
    public static final String BOOKSHELF_57 = fiftySeven(BOOKSHELF);
    public static final String BOOKSHELF_58 = fiftyEight(BOOKSHELF);
    public static final String BOOKSHELF_59 = fiftyNine(BOOKSHELF);
    public static final String BOOKSHELF_60 = sixty(BOOKSHELF);
    public static final String BOOKSHELF_61 = sixtyOne(BOOKSHELF);
    public static final String BOOKSHELF_62 = sixtyTwo(BOOKSHELF);
    public static final String BOOKSHELF_63 = sixtyThree(BOOKSHELF);
    public static final String BOOKSHELF_64 = sixtyFour(BOOKSHELF);

    protected static String one(String name) { return JolCraftStrings.underscored(name, "1"); }
    protected static String two(String name) { return JolCraftStrings.underscored(name, "2"); }
    protected static String three(String name) { return JolCraftStrings.underscored(name, "3"); }
    protected static String four(String name) { return JolCraftStrings.underscored(name, "4"); }
    protected static String five(String name) { return JolCraftStrings.underscored(name, "5"); }
    protected static String six(String name) { return JolCraftStrings.underscored(name, "6"); }
    protected static String seven(String name) { return JolCraftStrings.underscored(name, "7"); }
    protected static String eight(String name) { return JolCraftStrings.underscored(name, "8"); }
    protected static String nine(String name) { return JolCraftStrings.underscored(name, "9"); }
    protected static String ten(String name) { return JolCraftStrings.underscored(name, "10"); }
    protected static String eleven(String name) { return JolCraftStrings.underscored(name, "11"); }
    protected static String twelve(String name) { return JolCraftStrings.underscored(name, "12"); }
    protected static String thirteen(String name) { return JolCraftStrings.underscored(name, "13"); }
    protected static String fourteen(String name) { return JolCraftStrings.underscored(name, "14"); }
    protected static String fifteen(String name) { return JolCraftStrings.underscored(name, "15"); }
    protected static String sixteen(String name) { return JolCraftStrings.underscored(name, "16"); }
    protected static String seventeen(String name) { return JolCraftStrings.underscored(name, "17"); }
    protected static String eighteen(String name) { return JolCraftStrings.underscored(name, "18"); }
    protected static String nineteen(String name) { return JolCraftStrings.underscored(name, "19"); }
    protected static String twenty(String name) { return JolCraftStrings.underscored(name, "20"); }
    protected static String twentyOne(String name) { return JolCraftStrings.underscored(name, "21"); }
    protected static String twentyTwo(String name) { return JolCraftStrings.underscored(name, "22"); }
    protected static String twentyThree(String name) { return JolCraftStrings.underscored(name, "23"); }
    protected static String twentyFour(String name) { return JolCraftStrings.underscored(name, "24"); }
    protected static String twentyFive(String name) { return JolCraftStrings.underscored(name, "25"); }
    protected static String twentySix(String name) { return JolCraftStrings.underscored(name, "26"); }
    protected static String twentySeven(String name) { return JolCraftStrings.underscored(name, "27"); }
    protected static String twentyEight(String name) { return JolCraftStrings.underscored(name, "28"); }
    protected static String twentyNine(String name) { return JolCraftStrings.underscored(name, "29"); }
    protected static String thirty(String name) { return JolCraftStrings.underscored(name, "30"); }
    protected static String thirtyOne(String name) { return JolCraftStrings.underscored(name, "31"); }
    protected static String thirtyTwo(String name) { return JolCraftStrings.underscored(name, "32"); }
    protected static String thirtyThree(String name) { return JolCraftStrings.underscored(name, "33"); }
    protected static String thirtyFour(String name) { return JolCraftStrings.underscored(name, "34"); }
    protected static String thirtyFive(String name) { return JolCraftStrings.underscored(name, "35"); }
    protected static String thirtySix(String name) { return JolCraftStrings.underscored(name, "36"); }
    protected static String thirtySeven(String name) { return JolCraftStrings.underscored(name, "37"); }
    protected static String thirtyEight(String name) { return JolCraftStrings.underscored(name, "38"); }
    protected static String thirtyNine(String name) { return JolCraftStrings.underscored(name, "39"); }
    protected static String forty(String name) { return JolCraftStrings.underscored(name, "40"); }
    protected static String fortyOne(String name) { return JolCraftStrings.underscored(name, "41"); }
    protected static String fortyTwo(String name) { return JolCraftStrings.underscored(name, "42"); }
    protected static String fortyThree(String name) { return JolCraftStrings.underscored(name, "43"); }
    protected static String fortyFour(String name) { return JolCraftStrings.underscored(name, "44"); }
    protected static String fortyFive(String name) { return JolCraftStrings.underscored(name, "45"); }
    protected static String fortySix(String name) { return JolCraftStrings.underscored(name, "46"); }
    protected static String fortySeven(String name) { return JolCraftStrings.underscored(name, "47"); }
    protected static String fortyEight(String name) { return JolCraftStrings.underscored(name, "48"); }
    protected static String fortyNine(String name) { return JolCraftStrings.underscored(name, "49"); }
    protected static String fifty(String name) { return JolCraftStrings.underscored(name, "50"); }
    protected static String fiftyOne(String name) { return JolCraftStrings.underscored(name, "51"); }
    protected static String fiftyTwo(String name) { return JolCraftStrings.underscored(name, "52"); }
    protected static String fiftyThree(String name) { return JolCraftStrings.underscored(name, "53"); }
    protected static String fiftyFour(String name) { return JolCraftStrings.underscored(name, "54"); }
    protected static String fiftyFive(String name) { return JolCraftStrings.underscored(name, "55"); }
    protected static String fiftySix(String name) { return JolCraftStrings.underscored(name, "56"); }
    protected static String fiftySeven(String name) { return JolCraftStrings.underscored(name, "57"); }
    protected static String fiftyEight(String name) { return JolCraftStrings.underscored(name, "58"); }
    protected static String fiftyNine(String name) { return JolCraftStrings.underscored(name, "59"); }
    protected static String sixty(String name) { return JolCraftStrings.underscored(name, "60"); }
    protected static String sixtyOne(String name) { return JolCraftStrings.underscored(name, "61"); }
    protected static String sixtyTwo(String name) { return JolCraftStrings.underscored(name, "62"); }
    protected static String sixtyThree(String name) { return JolCraftStrings.underscored(name, "63"); }
    protected static String sixtyFour(String name) { return JolCraftStrings.underscored(name, "64"); }
}
