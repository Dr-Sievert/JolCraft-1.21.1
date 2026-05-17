package net.sievert.jolcraft.datagen.structure.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.sievert.jolcraft.JolCraft;

import java.util.List;

public abstract class AbstractProcessorProvider {

    protected final BootstrapContext<StructureProcessorList> context;
    protected final String structureId;

    protected AbstractProcessorProvider(BootstrapContext<StructureProcessorList> context, String structureId) {
        this.context = context;
        this.structureId = structureId;
    }

    protected static ResourceKey<StructureProcessorList> processorKey(String name) {
        return ResourceKey.create(
                Registries.PROCESSOR_LIST,
                JolCraft.location(name)
        );
    }

    protected final void register(
            ResourceKey<StructureProcessorList> key,
            StructureProcessor... processors
    ) {
        context.register(
                key,
                new StructureProcessorList(List.of(processors))
        );
    }
}