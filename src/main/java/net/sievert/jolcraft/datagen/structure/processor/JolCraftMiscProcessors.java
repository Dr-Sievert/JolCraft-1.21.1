package net.sievert.jolcraft.datagen.structure.processor;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.sievert.jolcraft.data.id.worldgen.JolCraftProcessorIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.structure.pool.JolCraftMiscStructurePools;
import net.sievert.jolcraft.datagen.structure.util.AbstractProcessorProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.worldgen.processor.custom.BookshelfTomeProcessor;
import net.sievert.jolcraft.world.worldgen.processor.custom.LanternProcessor;

public class JolCraftMiscProcessors extends AbstractProcessorProvider {

    private static final String DIRECTORY_ID = JolCraftMiscStructurePools.DIRECTORY_ID;

    public static final ResourceKey<StructureProcessorList> LANTERNS =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftStrings.plural(JolCraftDictionary.LANTERN)));

    public static final ResourceKey<StructureProcessorList> BOOKSHELF_TOME =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftProcessorIds.BOOKSHELF_TOME));

    private JolCraftMiscProcessors(BootstrapContext<StructureProcessorList> context) {
        super(context, DIRECTORY_ID);
    }

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        new JolCraftMiscProcessors(context).registerProcessors();
    }

    private void registerProcessors() {
        register(
                LANTERNS,
                new LanternProcessor()
        );

        register(
                BOOKSHELF_TOME,
                new BookshelfTomeProcessor()
        );
    }
}
