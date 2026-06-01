package net.sievert.jolcraft.datagen.structure.processor;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftTemplatePoolIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.structure.util.AbstractProcessorProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
import net.sievert.jolcraft.world.worldgen.processor.custom.*;
import net.sievert.jolcraft.world.worldgen.test.custom.RandomNotAirRuleTest;

import java.util.List;

@SuppressWarnings("deprecation")
public final class JolCraftDwarvenFortressProcessors extends AbstractProcessorProvider {

    private static final String DIRECTORY_ID = JolCraftStructureIds.DWARVEN_FORTRESS;

    public static final ResourceKey<StructureProcessorList> DWARVEN_FORTRESS =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, DIRECTORY_ID));

    public static final ResourceKey<StructureProcessorList> ABANDONED =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftTemplatePoolIds.ABANDONED));

    public static final ResourceKey<StructureProcessorList> BROKEN =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftTemplatePoolIds.BROKEN));

    public static final ResourceKey<StructureProcessorList> CAVE_IN =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftStrings.underscored(JolCraftDictionary.CAVE, JolCraftDictionary.IN)));

    public static final ResourceKey<StructureProcessorList> COLLAPSED =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftTemplatePoolIds.COLLAPSED));

    public static final ResourceKey<StructureProcessorList> STRONGBOX =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftBlockIds.STRONGBOX));

    public static final ResourceKey<StructureProcessorList> ARCHAEOLOGY =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDictionary.ARCHAEOLOGY));

    private JolCraftDwarvenFortressProcessors(BootstrapContext<StructureProcessorList> context) {
        super(context, DIRECTORY_ID);
    }

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        new JolCraftDwarvenFortressProcessors(context).registerProcessors();
    }

    private void registerProcessors() {

        register(
                DWARVEN_FORTRESS,
                crackedBricks(0.10F)
        );

        register(
                CAVE_IN,
                crackedBricks(0.10F),
                new RandomCaveInProcessor(0.10F),
                new StructureVoidProcessor()
        );

        register(
                COLLAPSED,
                crackedBricks(0.10F),
                new RuleProcessor(List.of(
                        new ProcessorRule(
                                new RandomNotAirRuleTest(0.10F),
                                AlwaysTrueTest.INSTANCE,
                                Blocks.STRUCTURE_VOID.defaultBlockState()
                        )
                )),
                new StructureVoidProcessor(),
                new RandomCobwebProcessor(0.10F)
        );

        register(
                ABANDONED,
                new RandomCobwebProcessor(0.10F)
        );

        register(
                BROKEN,
                new RuleProcessor(List.of(
                        new ProcessorRule(
                                new RandomNotAirRuleTest(0.20F),
                                AlwaysTrueTest.INSTANCE,
                                Blocks.AIR.defaultBlockState()
                        )
                ))
        );

        register(
                STRONGBOX,
                new AddLootTableProcessor(
                        JolCraftBlocks.STRONGBOX.getId(),
                        JolCraftLootTables.Strongbox.DWARVEN_FORTRESS
                )
        );

        register(
                ARCHAEOLOGY,
                new RandomReplaceWithLootProcessor(
                        Blocks.GRAVEL.builtInRegistryHolder().key().location(),
                        Blocks.SUSPICIOUS_GRAVEL.builtInRegistryHolder().key().location(),
                        0.01F,
                        JolCraftLootTables.Archaeology.DWARVEN_FORTRESS_RARE.location()
                ),
                new RandomReplaceWithLootProcessor(
                        Blocks.GRAVEL.builtInRegistryHolder().key().location(),
                        Blocks.SUSPICIOUS_GRAVEL.builtInRegistryHolder().key().location(),
                        0.08F,
                        JolCraftLootTables.Archaeology.DWARVEN_FORTRESS_COMMON.location()
                )
        );
    }

    private static RuleProcessor crackedBricks(float chance) {
        return new RuleProcessor(List.of(
                new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.DEEPSLATE_BRICKS, chance),
                        AlwaysTrueTest.INSTANCE,
                        Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState()
                )
        ));
    }
}