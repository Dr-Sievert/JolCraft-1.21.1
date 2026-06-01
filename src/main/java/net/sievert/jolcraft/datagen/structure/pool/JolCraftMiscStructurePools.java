package net.sievert.jolcraft.datagen.structure.pool;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftTemplatePoolIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.structure.processor.JolCraftMiscProcessors;
import net.sievert.jolcraft.datagen.structure.util.AbstractPoolProvider;
import net.sievert.jolcraft.util.JolCraftStrings;

public class JolCraftMiscStructurePools extends AbstractPoolProvider {

    public static final String DIRECTORY_ID = JolCraftDictionary.MISC;

    public static final ResourceKey<StructureTemplatePool> STRONGBOX_POOL = poolKey(JolCraftTemplatePoolIds.STRONGBOX);

    public static final ResourceKey<StructureTemplatePool> ANVIL_POOL = poolKey(JolCraftTemplatePoolIds.ANVIL);
    public static final ResourceKey<StructureTemplatePool> BRAZIER_POOL = poolKey(JolCraftTemplatePoolIds.BRAZIER);

    public static final ResourceKey<StructureTemplatePool> CHAIN_1_POOL = poolKey(JolCraftTemplatePoolIds.CHAIN_1);
    public static final ResourceKey<StructureTemplatePool> CHAIN_2_POOL = poolKey(JolCraftTemplatePoolIds.CHAIN_2);
    public static final ResourceKey<StructureTemplatePool> CHAIN_3_POOL = poolKey(JolCraftTemplatePoolIds.CHAIN_3);
    public static final ResourceKey<StructureTemplatePool> CHAIN_4_POOL = poolKey(JolCraftTemplatePoolIds.CHAIN_4);

    public static final ResourceKey<StructureTemplatePool> LANTERN_1_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_1);
    public static final ResourceKey<StructureTemplatePool> LANTERN_2_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_2);
    public static final ResourceKey<StructureTemplatePool> LANTERN_3_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_3);
    public static final ResourceKey<StructureTemplatePool> LANTERN_4_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_4);
    public static final ResourceKey<StructureTemplatePool> LANTERN_5_POOL = poolKey(JolCraftTemplatePoolIds.LANTERN_5);

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
        register(ANVIL_POOL, JolCraftTemplatePoolIds.ANVIL);
        register(BRAZIER_POOL, JolCraftTemplatePoolIds.BRAZIER);

        register(CHAIN_1_POOL, lantern(JolCraftTemplatePoolIds.CHAIN_1));
        register(CHAIN_2_POOL, lantern(JolCraftTemplatePoolIds.CHAIN_2));
        register(CHAIN_3_POOL, lantern(JolCraftTemplatePoolIds.CHAIN_3));
        register(CHAIN_4_POOL, lantern(JolCraftTemplatePoolIds.CHAIN_4));

        register(LANTERN_1_POOL, lantern(JolCraftTemplatePoolIds.LANTERN_1));
        register(LANTERN_2_POOL, lantern(JolCraftTemplatePoolIds.LANTERN_2));
        register(LANTERN_3_POOL, lantern(JolCraftTemplatePoolIds.LANTERN_3));
        register(LANTERN_4_POOL, lantern(JolCraftTemplatePoolIds.LANTERN_4));
        register(LANTERN_5_POOL, lantern(JolCraftTemplatePoolIds.LANTERN_5));

        register(
                LARGE_LANTERNS_POOL,
                lantern(JolCraftTemplatePoolIds.CHAIN_1),
                lantern(JolCraftTemplatePoolIds.CHAIN_2),
                lantern(JolCraftTemplatePoolIds.CHAIN_3),
                lantern(JolCraftTemplatePoolIds.CHAIN_4),
                lantern(JolCraftTemplatePoolIds.LANTERN_1),
                lantern(JolCraftTemplatePoolIds.LANTERN_2),
                lantern(JolCraftTemplatePoolIds.LANTERN_3),
                lantern(JolCraftTemplatePoolIds.LANTERN_4),
                lantern(JolCraftTemplatePoolIds.LANTERN_5)
        );

        register(
                MEDIUM_LANTERNS_POOL,
                lantern(JolCraftTemplatePoolIds.CHAIN_1),
                lantern(JolCraftTemplatePoolIds.CHAIN_2),
                lantern(JolCraftTemplatePoolIds.LANTERN_1),
                lantern(JolCraftTemplatePoolIds.LANTERN_2),
                lantern(JolCraftTemplatePoolIds.LANTERN_3)
        );

        register(
                SMALL_LANTERNS_POOL,
                lantern(JolCraftTemplatePoolIds.LANTERN_1),
                empty()
        );
    }

    private static ResourceKey<StructureTemplatePool> poolKey(String name) {
        return poolKey(DIRECTORY_ID, name);
    }

    private PoolEntry lantern(String template) {
        return processed(template, JolCraftMiscProcessors.LANTERNS);
    }
}