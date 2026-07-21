package net.sievert.jolcraft.data.id.worldgen.template_pool;

import net.sievert.jolcraft.data.language.JolCraftDictionary;

public class JolCraftDwarvenFortressPoolIds extends JolCraftTemplatePoolIds {

    private JolCraftDwarvenFortressPoolIds() {}

    //Start

    public static final String RIGHT_TOWER = join(RIGHT, TOWER);
    public static final String LEFT_TOWER = join(LEFT, TOWER);

    //Main

    public static final String MAIN_SHAFT =           join(MAIN, SHAFT);
    public static final String MAIN_LARGE_JUNCTION =  join(MAIN, LARGE_JUNCTION);
    public static final String MAIN_MEDIUM_JUNCTION = join(MAIN, MEDIUM_JUNCTION);
    public static final String MAIN_SMALL_JUNCTION =  join(MAIN, SMALL_JUNCTION);
    public static final String MAIN_LARGE_CORRIDOR =  join(MAIN, LARGE_CORRIDOR);
    public static final String MAIN_MEDIUM_CORRIDOR = join(MAIN, MEDIUM_CORRIDOR);
    public static final String MAIN_SMALL_CORRIDOR =  join(MAIN, SMALL_CORRIDOR);
    public static final String MAIN_STAIRS =          join(MAIN, STAIRS);
    public static final String MAIN_END =            join(MAIN, END);
    public static final String MAIN_COLLAPSED =      join(MAIN, COLLAPSED);

    //Large

    public static final String LARGE_JUNCTION_1 =  one(LARGE_JUNCTION);
    public static final String LARGE_JUNCTION_2 = two(LARGE_JUNCTION);
    public static final String LARGE_JUNCTION_3 =  three(LARGE_JUNCTION);
    public static final String LARGE_CORRIDOR_1 = one(LARGE_CORRIDOR);
    public static final String LARGE_CORRIDOR_2 = two(LARGE_CORRIDOR);
    public static final String LARGE_CORRIDOR_3 =  three(LARGE_CORRIDOR);
    public static final String LARGE_STAIRS =    join(LARGE, STAIRS);
    public static final String LARGE_END =    join(LARGE, END);
    public static final String LARGE_COLLAPSED =    join(LARGE, COLLAPSED);
    public static final String LARGE_TRANSITION =    join(LARGE, TRANSITION);
    public static final String LARGE_MEDIUM_TRANSITION =    join(LARGE, MEDIUM, TRANSITION);
    public static final String LARGE_SMALL_TRANSITION =    join(LARGE, SMALL, TRANSITION);

    //Medium

    public static final String MEDIUM_JUNCTION_1 = one(MEDIUM_JUNCTION);
    public static final String MEDIUM_JUNCTION_2 =  two(MEDIUM_JUNCTION);
    public static final String MEDIUM_CORRIDOR_1 = one(MEDIUM_CORRIDOR);
    public static final String MEDIUM_CORRIDOR_2 = two(MEDIUM_CORRIDOR);
    public static final String MEDIUM_CORRIDOR_3 =  three(MEDIUM_CORRIDOR);
    public static final String MEDIUM_STAIRS =   join(MEDIUM, STAIRS);
    public static final String MEDIUM_END =    join(MEDIUM, END);
    public static final String MEDIUM_COLLAPSED =    join(MEDIUM, COLLAPSED);

    //Small

    public static final String SMALL_JUNCTION_1 =  one(SMALL_JUNCTION);
    public static final String SMALL_CORRIDOR_1 = one(SMALL_CORRIDOR);
    public static final String SMALL_CORRIDOR_2 = two(SMALL_CORRIDOR);
    public static final String SMALL_CORRIDOR_3 =  three(SMALL_CORRIDOR);
    public static final String SMALL_SHAFT =    join(SMALL, SHAFT);
    public static final String SMALL_END =    join(SMALL, END);
    public static final String SMALL_COLLAPSED =    join(SMALL, COLLAPSED);

    //Misc

    public static final String GRAVEL_LARGE = join(GRAVEL, LARGE);

    public static final String CRUCIBLE_ABANDONED = join(CRUCIBLE, ABANDONED);
    public static final String FORGE_LOOT = join(FORGE, LOOT);

    public static final String HALL_TABLE_DECORATION = join(JolCraftDictionary.HALL, JolCraftDictionary.TABLE, JolCraftDictionary.DECORATION);

    public static final String VAULT_LOOT = join(VAULT, LOOT);

    public static final String GARDEN_CORNER = join(GARDEN, CORNER);

    public static final String GARDEN_FLOWER = join(GARDEN, FLOWER);
    public static final String GARDEN_FLOWER_BED = join(GARDEN, FLOWER_BED);
    public static final String GARDEN_FLOWERS = plural(GARDEN_FLOWER);
    public static final String GARDEN_FLOWER_1 = one(GARDEN_FLOWER);
    public static final String GARDEN_FLOWER_2 = two(GARDEN_FLOWER);
    public static final String GARDEN_FLOWER_3 = three(GARDEN_FLOWER);
    public static final String GARDEN_FLOWER_4 = four(GARDEN_FLOWER);

    public static final String GARDEN_MIDDLE = join(GARDEN, MIDDLE);
    public static final String GARDEN_MIDDLE_1 = one(GARDEN_MIDDLE);
    public static final String GARDEN_MIDDLE_2 = two(GARDEN_MIDDLE);
    public static final String GARDEN_MIDDLE_3 = three(GARDEN_MIDDLE);

    public static final String GARDEN_LARGE = join(GARDEN, LARGE);
    public static final String GARDEN_LARGE_1 = one(GARDEN_LARGE);
    public static final String GARDEN_LARGE_2 = two(GARDEN_LARGE);

    public static final String GARDEN_LARGE_WALL = join(GARDEN_LARGE, WALL);
    public static final String GARDEN_LARGE_WALL_1 = one(GARDEN_LARGE_WALL);
    public static final String GARDEN_LARGE_WALL_2 = two(GARDEN_LARGE_WALL);

    public static final String GARDEN_LOOT = join(GARDEN, LOOT);

    public static final String ARCHIVES_LOOT = join(ARCHIVES, LOOT);

    public static final String CATACOMBS_CORRIDOR = join(CATACOMBS, CORRIDOR);
    public static final String CATACOMBS_CORRIDOR_START = join(CATACOMBS_CORRIDOR, START);
    public static final String CATACOMBS_CORRIDOR_1 = one(CATACOMBS_CORRIDOR);
    public static final String CATACOMBS_CORRIDOR_2 = two(CATACOMBS_CORRIDOR);
    public static final String CATACOMBS_CORRIDOR_3 = three(CATACOMBS_CORRIDOR);
    public static final String CATACOMBS_CORRIDOR_4 = four(CATACOMBS_CORRIDOR);
    public static final String CATACOMBS_CORRIDOR_END = join(CATACOMBS_CORRIDOR, END);
    public static final String CATACOMBS_CORRIDOR_END_1 = one(CATACOMBS_CORRIDOR_END);
    public static final String CATACOMBS_CORRIDOR_END_2 = two(CATACOMBS_CORRIDOR_END);

    public static final String CATACOMBS_TOMB = join(CATACOMBS, TOMB);
    public static final String CATACOMBS_TOMB_SMALL = join(CATACOMBS_TOMB, SMALL);
    public static final String CATACOMBS_TOMB_SMALL_1 = one(CATACOMBS_TOMB_SMALL);
    public static final String CATACOMBS_TOMB_SMALL_2 = two(CATACOMBS_TOMB_SMALL);
    public static final String CATACOMBS_TOMB_LARGE = join(CATACOMBS_TOMB, LARGE);

    public static final String CATACOMBS_LOOT = join(CATACOMBS, LOOT);

    public static final String TOWN_ENTRANCE = join(TOWN, ENTRANCE);
}
