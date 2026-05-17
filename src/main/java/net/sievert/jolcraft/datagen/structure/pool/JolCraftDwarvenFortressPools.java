package net.sievert.jolcraft.datagen.structure.pool;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftDwarvenFortressPoolIds;
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

    private JolCraftDwarvenFortressPools(BootstrapContext<StructureTemplatePool> context) {
        super(context, DIRECTORY_ID);
    }

    @Override
    protected ResourceKey<StructureProcessorList> defaultProcessor() {
        return JolCraftDwarvenFortressProcessors.DWARVEN_FORTRESS;
    }

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        new JolCraftDwarvenFortressPools(context).registerPools();
    }

    private void registerPools() {

        register(
                START_POOL,
                JolCraftDwarvenFortressPoolIds.START
        );

        register(
                ENTRANCE_POOL,
                JolCraftDwarvenFortressPoolIds.ENTRANCE
        );

        register(
                RIGHT_TOWER_POOL,
                JolCraftDwarvenFortressPoolIds.RIGHT_TOWER
        );

        register(
                LEFT_TOWER_POOL,
                JolCraftDwarvenFortressPoolIds.LEFT_TOWER
        );



        register(
                MAIN_SHAFT_POOL,
                JolCraftDwarvenFortressPoolIds.MAIN_SHAFT
        );

        register(
                MAIN_START_POOL,
                entry(JolCraftDwarvenFortressPoolIds.MAIN_LARGE_JUNCTION, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_MEDIUM_JUNCTION, 2),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_SMALL_JUNCTION, 3)
        );

        register(
                MAIN_POOL,
                entry(JolCraftDwarvenFortressPoolIds.MAIN_LARGE_JUNCTION, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_MEDIUM_JUNCTION, 2),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_SMALL_JUNCTION, 3),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_LARGE_CORRIDOR, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_MEDIUM_CORRIDOR, 2),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_SMALL_CORRIDOR, 3),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_STAIRS, 3),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_COLLAPSED, 1),
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
                entry(JolCraftDwarvenFortressPoolIds.MAIN_COLLAPSED, 1),
                entry(JolCraftDwarvenFortressPoolIds.MAIN_END, 3)
        );



        register(
                LARGE_POOL,
                entry(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_3, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_3, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_STAIRS, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_COLLAPSED, 1),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_END, 3)
        );

        register(
                LARGE_STAIRS_POOL,
                entry(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_JUNCTION_3, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_CORRIDOR_3, 3),
                entry(JolCraftDwarvenFortressPoolIds.LARGE_STAIRS, 8)
        );



        register(
                MEDIUM_POOL,
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_JUNCTION_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_JUNCTION_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_3, 3),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_STAIRS, 3),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_COLLAPSED, 1),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_END, 3)
        );

        register(
                MEDIUM_STAIRS_POOL,
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_JUNCTION_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_JUNCTION_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_CORRIDOR_3, 3),
                entry(JolCraftDwarvenFortressPoolIds.MEDIUM_STAIRS, 8)
        );



        register(
                SMALL_POOL,
                entry(JolCraftDwarvenFortressPoolIds.SMALL_JUNCTION_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_3, 3),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_SHAFT, 3),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_COLLAPSED, 1),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_END, 3)
        );

        register(
                SMALL_SHAFT_POOL,
                entry(JolCraftDwarvenFortressPoolIds.SMALL_JUNCTION_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_1, 1),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_2, 2),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_CORRIDOR_3, 3),
                entry(JolCraftDwarvenFortressPoolIds.SMALL_SHAFT, 3)
        );
    }

    private static ResourceKey<StructureTemplatePool> poolKey(String name) {
        return poolKey(DIRECTORY_ID, name);
    }

    private void register(ResourceKey<StructureTemplatePool> poolKey, String... templates) {
        registerRigid(poolKey, templates);
    }

    private void register(ResourceKey<StructureTemplatePool> poolKey, PoolEntry... entries) {
        registerRigid(poolKey, entries);
    }
}