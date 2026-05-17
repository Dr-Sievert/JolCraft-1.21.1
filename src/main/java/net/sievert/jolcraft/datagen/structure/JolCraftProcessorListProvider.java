package net.sievert.jolcraft.datagen.structure;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.sievert.jolcraft.datagen.structure.processor.JolCraftDwarvenFortressProcessors;

public class JolCraftProcessorListProvider {

    private JolCraftProcessorListProvider() {}

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        JolCraftDwarvenFortressProcessors.bootstrap(context);
    }
}
