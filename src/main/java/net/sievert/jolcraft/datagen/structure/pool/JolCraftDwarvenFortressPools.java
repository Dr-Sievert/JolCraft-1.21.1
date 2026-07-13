package net.sievert.jolcraft.datagen.structure.pool;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftDwarvenFortressPoolIds;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftTemplatePoolIds;
import net.sievert.jolcraft.datagen.structure.processor.JolCraftDwarvenFortressProcessors;
import net.sievert.jolcraft.datagen.structure.util.AbstractPoolProvider;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftDwarvenFortressPools extends AbstractPoolProvider {

    private static final String DIRECTORY_ID = JolCraftStructureIds.DWARVEN_FORTRESS;

    public static final ResourceKey<StructureTemplatePool> START_POOL = poolKey(JolCraftDwarvenFortressPoolIds.START);
    public static final ResourceKey<StructureTemplatePool> ENTRANCE_POOL = poolKey(JolCraftDwarvenFortressPoolIds.ENTRANCE);
    public static final ResourceKey<StructureTemplatePool> RIGHT_TOWER_POOL = poolKey(JolCraftDwarvenFortressPoolIds.RIGHT_TOWER);
    public static final ResourceKey<StructureTemplatePool> LEFT_TOWER_POOL = poolKey(JolCraftDwarvenFortressPoolIds.LEFT_TOWER);

    public static final ResourceKey<StructureTemplatePool> MAIN_SHAFT_POOL = poolKey(JolCraftDwarvenFortressPoolIds.MAIN_SHAFT);
    public static final ResourceKey<StructureTemplatePool> MAIN_START_POOL = poolKey(JolCraftStrings.underscored(JolCraftDwarvenFortressPoolIds.MAIN, JolCraftDwarvenFortressPoolIds.START));
    public static final ResourceKey<StructureTemplatePool> MAIN_POOL = poolKey(JolCraftDwarvenFortressPoolIds.MAIN);
    public static final ResourceKey<StructureTemplatePool> MAIN_STAIRS_POOL = poolKey(JolCraftDwarvenFortressPoolIds.MAIN_STAIRS);

    public static final ResourceKey<StructureTemplatePool> LARGE_POOL = poolKey(JolCraftDwarvenFortressPoolIds.LARGE);
    public static final ResourceKey<StructureTemplatePool> LARGE_STAIRS_POOL = poolKey(JolCraftDwarvenFortressPoolIds.LARGE_STAIRS);

    public static final ResourceKey<StructureTemplatePool> MEDIUM_POOL = poolKey(JolCraftDwarvenFortressPoolIds.MEDIUM);
    public static final ResourceKey<StructureTemplatePool> MEDIUM_STAIRS_POOL = poolKey(JolCraftDwarvenFortressPoolIds.MEDIUM_STAIRS);

    public static final ResourceKey<StructureTemplatePool> SMALL_POOL = poolKey(JolCraftDwarvenFortressPoolIds.SMALL);
    public static final ResourceKey<StructureTemplatePool> SMALL_SHAFT_POOL = poolKey(JolCraftDwarvenFortressPoolIds.SMALL_SHAFT);

    public static final ResourceKey<StructureTemplatePool> ROOM_POOL = poolKey(JolCraftDwarvenFortressPoolIds.ROOM);

    public static final ResourceKey<StructureTemplatePool> GRAVEL_LARGE_POOL = poolKey(JolCraftDwarvenFortressPoolIds.GRAVEL_LARGE);
    public static final ResourceKey<StructureTemplatePool> GRAVEL_POOL = poolKey(JolCraftTemplatePoolIds.GRAVEL);

    public static final ResourceKey<StructureTemplatePool> CRUCIBLE_POOL = poolKey(JolCraftDwarvenFortressPoolIds.CRUCIBLE);
    public static final ResourceKey<StructureTemplatePool> FORGE_LOOT_POOL = poolKey(JolCraftDwarvenFortressPoolIds.FORGE_LOOT);

    public static final ResourceKey<StructureTemplatePool> HALL_TABLE_DECORATION_POOL = poolKey(JolCraftDwarvenFortressPoolIds.HALL_TABLE_DECORATION);
    public static final ResourceKey<StructureTemplatePool> BARREL_POOL = poolKey(JolCraftTemplatePoolIds.BARREL);

    public static final ResourceKey<StructureTemplatePool> VAULT_LOOT_POOL = poolKey(JolCraftDwarvenFortressPoolIds.VAULT_LOOT);

    public static final ResourceKey<StructureTemplatePool> GARDEN_CORNER_POOL = poolKey(JolCraftDwarvenFortressPoolIds.GARDEN_CORNER);
    public static final ResourceKey<StructureTemplatePool> GARDEN_MIDDLE_POOL = poolKey(JolCraftDwarvenFortressPoolIds.GARDEN_MIDDLE);
    public static final ResourceKey<StructureTemplatePool> GARDEN_LARGE_POOL = poolKey(JolCraftDwarvenFortressPoolIds.GARDEN_LARGE);
    public static final ResourceKey<StructureTemplatePool> GARDEN_LARGE_WALL_POOL = poolKey(JolCraftDwarvenFortressPoolIds.GARDEN_LARGE_WALL);
    public static final ResourceKey<StructureTemplatePool> GARDEN_FLOWERS_POOL = poolKey(JolCraftDwarvenFortressPoolIds.GARDEN_FLOWERS);
    public static final ResourceKey<StructureTemplatePool> GARDEN_LOOT_POOL = poolKey(JolCraftDwarvenFortressPoolIds.GARDEN_LOOT);

    public static final ResourceKey<StructureTemplatePool> ARCHIVES_LOOT_POOL = poolKey(JolCraftDwarvenFortressPoolIds.ARCHIVES_LOOT);

    private JolCraftDwarvenFortressPools(BootstrapContext<StructureTemplatePool> context) {
        super(context, DIRECTORY_ID, JolCraftDwarvenFortressProcessors.DWARVEN_FORTRESS);
    }

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        new JolCraftDwarvenFortressPools(context).registerPools();
    }

    private void registerPools() {
        register(START_POOL, JolCraftDwarvenFortressPoolIds.START);
        register(ENTRANCE_POOL, JolCraftDwarvenFortressPoolIds.ENTRANCE);
        register(RIGHT_TOWER_POOL, JolCraftDwarvenFortressPoolIds.RIGHT_TOWER);
        register(LEFT_TOWER_POOL, JolCraftDwarvenFortressPoolIds.LEFT_TOWER);

        register(MAIN_SHAFT_POOL, JolCraftDwarvenFortressPoolIds.MAIN_SHAFT);
        register(MAIN_START_POOL, JolCraftDwarvenFortressPoolIds.MAIN_LARGE_JUNCTION);

        register(
                MAIN_POOL,
                entry(JolCraftDwarvenFortressPoolIds.MAIN_LARGE_JUNCTION, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_MEDIUM_JUNCTION, 2),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_SMALL_JUNCTION, 3),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_LARGE_CORRIDOR, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_MEDIUM_CORRIDOR, 2),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_SMALL_CORRIDOR, 3),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_STAIRS, 3),
                processed(JolCraftDwarvenFortressPoolIds.MAIN_COLLAPSED, JolCraftDwarvenFortressProcessors.COLLAPSED, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_END, 3)
        );

        register(
                MAIN_STAIRS_POOL,
                entry(JolCraftDwarvenFortressPoolIds.MAIN_LARGE_JUNCTION, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_MEDIUM_JUNCTION, 2),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_SMALL_JUNCTION, 3),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_LARGE_CORRIDOR, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_MEDIUM_CORRIDOR, 2),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_SMALL_CORRIDOR, 3),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_STAIRS, 8),
                processed(JolCraftDwarvenFortressPoolIds.MAIN_COLLAPSED, JolCraftDwarvenFortressProcessors.COLLAPSED, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_END, 3)
        );

        register(
                LARGE_POOL,
                room(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_1, 1),
                room(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_2, 2),
                room(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_3, 3),
                room(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_1, 1),
                room(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_2, 2),
                room(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_3, 3),
                room(JolCraftDwarvenFortressPoolIds.LARGE_STAIRS, 3),
                room(JolCraftDwarvenFortressPoolIds.LARGE_COLLAPSED, JolCraftDwarvenFortressProcessors.COLLAPSED, 1),
                room(JolCraftDwarvenFortressPoolIds.LARGE_END, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_TRANSITION, 1)
        );

        register(
                LARGE_STAIRS_POOL,
                room(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_1, 1),
                room(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_2, 2),
                room(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_3, 3),
                room(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_1, 1),
                room(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_2, 2),
                room(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_3, 3),
                room(JolCraftDwarvenFortressPoolIds.LARGE_STAIRS, 8),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_TRANSITION, 1)
        );

        register(
                MEDIUM_POOL,
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_JUNCTION_1, 1),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_JUNCTION_2, 2),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_1, 1),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_2, 2),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_3, 3),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_STAIRS, 3),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_COLLAPSED, JolCraftDwarvenFortressProcessors.COLLAPSED, 1),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_END, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_MEDIUM_TRANSITION, 1)
        );

        register(
                MEDIUM_STAIRS_POOL,
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_JUNCTION_1, 1),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_JUNCTION_2, 2),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_1, 1),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_2, 2),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_3, 3),
                room(JolCraftDwarvenFortressPoolIds.MEDIUM_STAIRS, 8),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_MEDIUM_TRANSITION, 1)
        );

        register(
                SMALL_POOL,
                room(JolCraftDwarvenFortressPoolIds.SMALL_JUNCTION_1, 1),
                room(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_1, 1),
                room(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_2, 2),
                room(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_3, 3),
                room(JolCraftDwarvenFortressPoolIds.SMALL_SHAFT, 3),
                room(JolCraftDwarvenFortressPoolIds.SMALL_COLLAPSED, JolCraftDwarvenFortressProcessors.COLLAPSED, 1),
                room(JolCraftDwarvenFortressPoolIds.SMALL_END, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_SMALL_TRANSITION, 1)
        );

        register(
                SMALL_SHAFT_POOL,
                room(JolCraftDwarvenFortressPoolIds.SMALL_JUNCTION_1, 1),
                room(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_1, 1),
                room(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_2, 2),
                room(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_3, 3),
                room(JolCraftDwarvenFortressPoolIds.SMALL_SHAFT, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_SMALL_TRANSITION, 1)
        );

        register(
                ROOM_POOL,
                processed(JolCraftDwarvenFortressPoolIds.DIGSITE, JolCraftDwarvenFortressProcessors.ARCHAEOLOGY),
                entry(JolCraftDwarvenFortressPoolIds.FORGE),
                entry(JolCraftDwarvenFortressPoolIds.HALL),
                entry(JolCraftDwarvenFortressPoolIds.VAULT),
                processed(JolCraftDwarvenFortressPoolIds.GARDEN, JolCraftDwarvenFortressProcessors.ABANDONED),
                entry(JolCraftDwarvenFortressPoolIds.ARCHIVES, 100)
        );

        register(
                GRAVEL_LARGE_POOL,
                processed(JolCraftDwarvenFortressPoolIds.GRAVEL_LARGE, JolCraftDwarvenFortressProcessors.ARCHAEOLOGY)
        );

        register(
                GRAVEL_POOL,
                misc(JolCraftTemplatePoolIds.GRAVEL_1, JolCraftDwarvenFortressProcessors.ARCHAEOLOGY, 3),
                misc(JolCraftTemplatePoolIds.GRAVEL_2, JolCraftDwarvenFortressProcessors.ARCHAEOLOGY, 3),
                misc(JolCraftTemplatePoolIds.GRAVEL_3, JolCraftDwarvenFortressProcessors.ARCHAEOLOGY, 2),
                misc(JolCraftTemplatePoolIds.GRAVEL_4, JolCraftDwarvenFortressProcessors.ARCHAEOLOGY, 2),
                misc(JolCraftTemplatePoolIds.GRAVEL_5, JolCraftDwarvenFortressProcessors.ARCHAEOLOGY, 1),
                empty(3)
        );

        register(
                BARREL_POOL,
                misc(JolCraftTemplatePoolIds.BARREL_1, JolCraftDwarvenFortressProcessors.BARREL, 5),
                misc(JolCraftTemplatePoolIds.BARREL_2, JolCraftDwarvenFortressProcessors.BARREL, 4),
                misc(JolCraftTemplatePoolIds.BARREL_3, JolCraftDwarvenFortressProcessors.BARREL, 3),
                misc(JolCraftTemplatePoolIds.BARREL_4, JolCraftDwarvenFortressProcessors.BARREL, 2),
                misc(JolCraftTemplatePoolIds.BARREL_5, JolCraftDwarvenFortressProcessors.BARREL, 1),
                empty(7)
        );

        register(
                CRUCIBLE_POOL,
                entry(JolCraftDwarvenFortressPoolIds.CRUCIBLE, 6),
                processed(JolCraftDwarvenFortressPoolIds.CRUCIBLE_ABANDONED, JolCraftDwarvenFortressProcessors.ABANDONED, 1),
                processed(JolCraftDwarvenFortressPoolIds.CRUCIBLE, JolCraftDwarvenFortressProcessors.BROKEN, 1)
        );

        register(
                FORGE_LOOT_POOL,
                misc(JolCraftTemplatePoolIds.STRONGBOX_LOCKED, JolCraftDwarvenFortressProcessors.FORGE_LOOT, 2),
                misc(JolCraftTemplatePoolIds.STRONGBOX, JolCraftDwarvenFortressProcessors.FORGE_LOOT, 1),
                empty(1)
        );

        register(
                HALL_TABLE_DECORATION_POOL,
                misc(JolCraftTemplatePoolIds.POTTED_AZALEA_BUSH),
                misc(JolCraftTemplatePoolIds.POTTED_FLOWERING_AZALEA_BUSH),
                misc(JolCraftTemplatePoolIds.CANDLE_1, 3),
                misc(JolCraftTemplatePoolIds.CANDLE_2, 3),
                misc(JolCraftTemplatePoolIds.CANDLE_3, 2),
                misc(JolCraftTemplatePoolIds.CANDLE_4, 2)
        );

        register(
                VAULT_LOOT_POOL,
                misc(JolCraftTemplatePoolIds.STRONGBOX_LOCKED, JolCraftDwarvenFortressProcessors.VAULT_LOOT, 30),
                misc(JolCraftTemplatePoolIds.STRONGBOX, JolCraftDwarvenFortressProcessors.VAULT_LOOT, 10),
                misc(JolCraftTemplatePoolIds.DEEPSLATE_MITHRIL_ORE, JolCraftDwarvenFortressProcessors.VAULT_LOOT, 9),
                misc(JolCraftTemplatePoolIds.PURE_MITHRIL_BLOCK, JolCraftDwarvenFortressProcessors.VAULT_LOOT, 1),
                empty(50)
        );

        register(
                GARDEN_CORNER_POOL,
                processed(JolCraftDwarvenFortressPoolIds.GARDEN_FLOWER_BED, JolCraftDwarvenFortressProcessors.ABANDONED, 1)
        );

        register(
                GARDEN_FLOWERS_POOL,
                entry(JolCraftDwarvenFortressPoolIds.GARDEN_FLOWER_1),
                entry(JolCraftDwarvenFortressPoolIds.GARDEN_FLOWER_2),
                entry(JolCraftDwarvenFortressPoolIds.GARDEN_FLOWER_3),
                entry(JolCraftDwarvenFortressPoolIds.GARDEN_FLOWER_4)
        );

        register(
                GARDEN_MIDDLE_POOL,
                processed(JolCraftDwarvenFortressPoolIds.GARDEN_MIDDLE_1, JolCraftDwarvenFortressProcessors.ABANDONED, 1),
                processed(JolCraftDwarvenFortressPoolIds.GARDEN_MIDDLE_2, JolCraftDwarvenFortressProcessors.ABANDONED, 1),
                processed(JolCraftDwarvenFortressPoolIds.GARDEN_MIDDLE_3, JolCraftDwarvenFortressProcessors.ABANDONED, 1)
        );

        register(
                GARDEN_LARGE_POOL,
                processed(JolCraftDwarvenFortressPoolIds.GARDEN_LARGE_1, JolCraftDwarvenFortressProcessors.ABANDONED, 1),
                processed(JolCraftDwarvenFortressPoolIds.GARDEN_LARGE_2, JolCraftDwarvenFortressProcessors.ABANDONED, 1)
        );


        register(
                GARDEN_LARGE_WALL_POOL,
                processed(JolCraftDwarvenFortressPoolIds.GARDEN_LARGE_WALL_1, JolCraftDwarvenFortressProcessors.ABANDONED, 1),
                processed(JolCraftDwarvenFortressPoolIds.GARDEN_LARGE_WALL_2, JolCraftDwarvenFortressProcessors.ABANDONED, 1)
        );

        register(
                GARDEN_LOOT_POOL,
                misc(JolCraftTemplatePoolIds.STRONGBOX_LOCKED, JolCraftDwarvenFortressProcessors.GARDEN_LOOT, 2),
                misc(JolCraftTemplatePoolIds.STRONGBOX, JolCraftDwarvenFortressProcessors.GARDEN_LOOT, 1),
                empty(1)
        );

        register(
                ARCHIVES_LOOT_POOL,
                misc(JolCraftTemplatePoolIds.STRONGBOX_LOCKED, JolCraftDwarvenFortressProcessors.ARCHIVES_LOOT, 2),
                misc(JolCraftTemplatePoolIds.STRONGBOX, JolCraftDwarvenFortressProcessors.ARCHIVES_LOOT, 1),
                empty(1)
        );
    }

    private static ResourceKey<StructureTemplatePool> poolKey(String name) {
        return poolKey(DIRECTORY_ID, name);
    }

    private PoolEntry misc(String template, ResourceKey<StructureProcessorList> processor, int weight) {
        return external(JolCraftMiscStructurePools.DIRECTORY_ID, template, processor, weight);
    }

    private PoolEntry misc(String template, int weight) {
        return external(JolCraftMiscStructurePools.DIRECTORY_ID, template, null, weight);
    }

    private PoolEntry misc(String template) {
        return external(JolCraftMiscStructurePools.DIRECTORY_ID, template, null, 1);
    }

    private PoolEntry room(String template, int weight) {
        return processed(template, JolCraftDwarvenFortressProcessors.CAVE_IN, weight);
    }

    private PoolEntry room(String template, ResourceKey<StructureProcessorList> processor, int weight) {
        return processed(template, processor, weight);
    }
}