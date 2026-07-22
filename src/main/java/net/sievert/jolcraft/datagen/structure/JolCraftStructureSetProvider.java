package net.sievert.jolcraft.datagen.structure;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;

public final class JolCraftStructureSetProvider {

    private JolCraftStructureSetProvider() {}

    public static void bootstrap(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        context.register(
                JolCraftStructures.DWARVEN_FORTRESS.setKey(),
                new StructureSet(
                        structures.getOrThrow(JolCraftStructures.DWARVEN_FORTRESS.key()),
                        new RandomSpreadStructurePlacement(
                                30,
                                18,
                                RandomSpreadType.LINEAR,
                                615157278
                        )
                )
        );
    }
}