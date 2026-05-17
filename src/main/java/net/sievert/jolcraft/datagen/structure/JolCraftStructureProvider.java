package net.sievert.jolcraft.datagen.structure;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.datagen.structure.pool.JolCraftDwarvenFortressPools;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;
import net.sievert.jolcraft.world.worldgen.structure.custom.DwarvenFortressStructure;

import java.util.Optional;

public final class JolCraftStructureProvider {

    private JolCraftStructureProvider() {}

    public static void bootstrap(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);

        context.register(
                JolCraftStructures.DWARVEN_FORTRESS.key(),
                new DwarvenFortressStructure(
                        new Structure.StructureSettings.Builder(biomes.getOrThrow(JolCraftTags.Biomes.DWARVEN))
                                .generationStep(GenerationStep.Decoration.UNDERGROUND_DECORATION)
                                .terrainAdapation(TerrainAdjustment.NONE)
                                .build(),
                        pools.getOrThrow(JolCraftDwarvenFortressPools.START_POOL),
                        Optional.empty(),
                        30,
                        ConstantHeight.of(VerticalAnchor.absolute(0)),
                        Optional.empty(),
                        128,
                        DimensionPadding.ZERO,
                        LiquidSettings.IGNORE_WATERLOGGING
                )
        );
    }
}