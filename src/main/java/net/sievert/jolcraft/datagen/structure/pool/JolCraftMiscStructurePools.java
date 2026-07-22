package net.sievert.jolcraft.datagen.structure.pool;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftTemplatePoolIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.structure.processor.JolCraftDwarvenFortressProcessors;
import net.sievert.jolcraft.datagen.structure.processor.JolCraftMiscProcessors;
import net.sievert.jolcraft.datagen.structure.util.AbstractPoolProvider;
import net.sievert.jolcraft.util.JolCraftStrings;

public class JolCraftMiscStructurePools extends AbstractPoolProvider {

    public static final String DIRECTORY_ID = JolCraftDictionary.MISC;

    public static final ResourceKey<StructureTemplatePool> ANVIL_POOL = poolKey(JolCraftTemplatePoolIds.ANVIL);
    public static final ResourceKey<StructureTemplatePool> BRAZIER_POOL = poolKey(JolCraftTemplatePoolIds.BRAZIER);
    public static final ResourceKey<StructureTemplatePool> BOOKSHELF_POOL = poolKey(JolCraftTemplatePoolIds.BOOKSHELF);
    public static final ResourceKey<StructureTemplatePool> CANDLE_POOL = poolKey(JolCraftTemplatePoolIds.CANDLE);

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

        register(
                CANDLE_POOL,
                entry(JolCraftTemplatePoolIds.CANDLE_1),
                entry(JolCraftTemplatePoolIds.CANDLE_2),
                entry(JolCraftTemplatePoolIds.CANDLE_3),
                entry(JolCraftTemplatePoolIds.CANDLE_4),
                empty()
        );

        register(
                BOOKSHELF_POOL,
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_1),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_2),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_3),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_4),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_5),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_6),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_7),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_8),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_9),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_10),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_11),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_12),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_13),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_14),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_15),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_16),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_17),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_18),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_19),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_20),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_21),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_22),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_23),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_24),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_25),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_26),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_27),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_28),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_29),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_30),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_31),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_32),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_33),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_34),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_35),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_36),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_37),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_38),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_39),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_40),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_41),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_42),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_43),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_44),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_45),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_46),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_47),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_48),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_49),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_50),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_51),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_52),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_53),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_54),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_55),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_56),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_57),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_58),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_59),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_60),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_61),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_62),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_63),
                bookshelf(JolCraftTemplatePoolIds.BOOKSHELF_64)
        );
    }

    private static ResourceKey<StructureTemplatePool> poolKey(String name) {
        return poolKey(DIRECTORY_ID, name);
    }

    private PoolEntry lantern(String template) {
        return processed(template, JolCraftMiscProcessors.LANTERNS);
    }

    protected final PoolEntry bookshelf(String template) {
        return processed(JolCraftStrings.slashed(JolCraftTemplatePoolIds.BOOKSHELF, template), JolCraftMiscProcessors.BOOKSHELF_TOME, 1);
    }
}