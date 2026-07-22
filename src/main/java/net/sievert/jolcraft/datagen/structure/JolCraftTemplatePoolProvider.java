package net.sievert.jolcraft.datagen.structure;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.sievert.jolcraft.datagen.structure.pool.JolCraftDwarvenFortressPools;
import net.sievert.jolcraft.datagen.structure.pool.JolCraftMiscStructurePools;

public final class JolCraftTemplatePoolProvider {

    private JolCraftTemplatePoolProvider() {}

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        JolCraftDwarvenFortressPools.bootstrap(context);
        JolCraftMiscStructurePools.bootstrap(context);
    }
}