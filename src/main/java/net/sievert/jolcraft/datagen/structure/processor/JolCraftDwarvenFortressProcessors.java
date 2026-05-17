package net.sievert.jolcraft.datagen.structure.processor;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.datagen.structure.util.AbstractProcessorProvider;
import net.sievert.jolcraft.world.worldgen.processor.custom.StructureVoidProcessor;

import java.util.List;

public final class JolCraftDwarvenFortressProcessors extends AbstractProcessorProvider {

    private static final String DIRECTORY_ID = JolCraftStructureIds.DWARVEN_FORTRESS;

    public static final ResourceKey<StructureProcessorList> DWARVEN_FORTRESS = processorKey(DIRECTORY_ID);

    private JolCraftDwarvenFortressProcessors(BootstrapContext<StructureProcessorList> context) {
        super(context, DIRECTORY_ID);
    }

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        new JolCraftDwarvenFortressProcessors(context).registerProcessors();
    }

    private void registerProcessors() {
        register(
                DWARVEN_FORTRESS,
                new RuleProcessor(List.of(
                        new ProcessorRule(
                                new RandomBlockMatchTest(Blocks.DEEPSLATE_BRICKS, 0.1F),
                                AlwaysTrueTest.INSTANCE,
                                Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState()
                        ),
                        new ProcessorRule(
                                new RandomBlockMatchTest(Blocks.DEEPSLATE_BRICKS, 0.1F),
                                AlwaysTrueTest.INSTANCE,
                                Blocks.STRUCTURE_VOID.defaultBlockState()
                        )
                )),
                new StructureVoidProcessor()
        );
    }
}