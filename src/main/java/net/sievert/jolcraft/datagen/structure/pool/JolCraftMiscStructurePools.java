package net.sievert.jolcraft.datagen.structure.pool;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftTemplatePoolIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.structure.util.AbstractPoolProvider;
import net.sievert.jolcraft.util.JolCraftStrings;

public class JolCraftMiscStructurePools extends AbstractPoolProvider {

    private static final String DIRECTORY_ID = JolCraftDictionary.MISC;

    public static final ResourceKey<StructureTemplatePool> BRAZIER_POOL = poolKey(JolCraftTemplatePoolIds.BRAZIER);

    public static final ResourceKey<StructureTemplatePool> CHAIN_POOL = poolKey(JolCraftTemplatePoolIds.CHAIN);
    public static final ResourceKey<StructureTemplatePool> CHAIN_1_POOL = poolKey(JolCraftTemplatePoolIds.CHAIN_1);
    public static final ResourceKey<StructureTemplatePool> CHAIN_2_POOL = poolKey(JolCraftTemplatePoolIds.CHAIN_2);
    public static final ResourceKey<StructureTemplatePool> CHAIN_3_POOL = poolKey(JolCraftTemplatePoolIds.CHAIN_3);

    public static final ResourceKey<StructureTemplatePool> LANTERN_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN);
    public static final ResourceKey<StructureTemplatePool> LANTERN_1_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_1);
    public static final ResourceKey<StructureTemplatePool> LANTERN_2_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_2);
    public static final ResourceKey<StructureTemplatePool> LANTERN_3_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_3);
    public static final ResourceKey<StructureTemplatePool> LANTERN_4_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_4);

    public static final ResourceKey<StructureTemplatePool> LARGE_LANTERNS_POOL = poolKey(JolCraftStrings.underscored(
            JolCraftTemplatePoolIds.LARGE, JolCraftStrings.plural(JolCraftTemplatePoolIds.LANTERN)));

    public static final ResourceKey<StructureTemplatePool> MEDIUM_LANTERNS_POOL = poolKey(JolCraftStrings.underscored(
            JolCraftTemplatePoolIds.MEDIUM, JolCraftStrings.plural(JolCraftTemplatePoolIds.LANTERN)));

    public static final ResourceKey<StructureTemplatePool> SMALL_LANTERNS_POOL = poolKey(JolCraftStrings.underscored(
            JolCraftTemplatePoolIds.SMALL, JolCraftStrings.plural(JolCraftTemplatePoolIds.LANTERN)));

    private JolCraftMiscStructurePools(BootstrapContext<StructureTemplatePool> context) {
        super(context, DIRECTORY_ID);
    }

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        new JolCraftMiscStructurePools(context).registerPools();
    }

    private void registerPools() {

        register(
                BRAZIER_POOL,
                JolCraftTemplatePoolIds.BRAZIER
        );

        register(
                CHAIN_POOL,
                JolCraftTemplatePoolIds.CHAIN
        );

        register(
                CHAIN_1_POOL,
                JolCraftTemplatePoolIds.CHAIN_1
        );

        register(
                CHAIN_2_POOL,
                JolCraftTemplatePoolIds.CHAIN_2
        );

        register(
                CHAIN_3_POOL,
                JolCraftTemplatePoolIds.CHAIN_3
        );



        register(
                LANTERN_POOL,
                JolCraftTemplatePoolIds.LANTERN
        );

        register(
                LANTERN_1_POOL,
                JolCraftTemplatePoolIds.LANTERN_1
        );

        register(
                LANTERN_2_POOL,
                JolCraftTemplatePoolIds.LANTERN_2
        );

        register(
                LANTERN_3_POOL,
                JolCraftTemplatePoolIds.LANTERN_3
        );

        register(
                LANTERN_4_POOL,
                JolCraftTemplatePoolIds.LANTERN_4
        );



        register(
                LARGE_LANTERNS_POOL,
                JolCraftTemplatePoolIds.CHAIN,
                JolCraftTemplatePoolIds.CHAIN_1,
                JolCraftTemplatePoolIds.CHAIN_2,
                JolCraftTemplatePoolIds.CHAIN_3,
                JolCraftTemplatePoolIds.LANTERN_1,
                JolCraftTemplatePoolIds.LANTERN_2,
                JolCraftTemplatePoolIds.LANTERN_3,
                JolCraftTemplatePoolIds.LANTERN_4
        );

        register(
                MEDIUM_LANTERNS_POOL,
                JolCraftTemplatePoolIds.CHAIN,
                JolCraftTemplatePoolIds.CHAIN_1,
                JolCraftTemplatePoolIds.LANTERN_1,
                JolCraftTemplatePoolIds.LANTERN_2
        );

        register(
                SMALL_LANTERNS_POOL,
                entry(JolCraftTemplatePoolIds.LANTERN),
                empty()
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
