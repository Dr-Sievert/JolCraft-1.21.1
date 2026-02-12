package net.sievert.jolcraft.world.worldgen.structure.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;
import net.sievert.jolcraft.world.worldgen.structure.util.AbstractJigsawStructure;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarvenTrailStructure extends AbstractJigsawStructure {

    public static final MapCodec<DwarvenTrailStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    AbstractJigsawStructure.codec(instance, DwarvenTrailStructure::new)
            );

    public DwarvenTrailStructure(
            StructureSettings config,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawName,
            int size,
            HeightProvider startHeight,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings
    ) {
        super(
                config,
                startPool,
                startJigsawName,
                size,
                startHeight,
                projectStartToHeightmap,
                maxDistanceFromCenter,
                dimensionPadding,
                liquidSettings
        );
    }

    @Override
    protected boolean extraSpawningChecks(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int centerX = chunkPos.getMinBlockX() + 8;
        int centerZ = chunkPos.getMinBlockZ() + 8;

        int[] offsets = {0, -7, 7};
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;

        for (int dx : offsets) {
            for (int dz : offsets) {
                int x = centerX + dx;
                int z = centerZ + dz;

                int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                        x, z,
                        Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(),
                        context.randomState()
                );

                if (surfaceY > maxY) maxY = surfaceY;
                if (surfaceY < minY) minY = surfaceY;

                int blockY = surfaceY - 1;

                BlockState state = context.chunkGenerator()
                        .getBaseColumn(x, z, context.heightAccessor(), context.randomState())
                        .getBlock(blockY);

                if (state.isAir() || !state.getFluidState().isEmpty() || state.is(Blocks.ICE)) {
                    return false;
                }
            }
        }

        return (maxY - minY) <= 5 && maxY <= 90;
    }

    @Override
    public StructureType<?> type() {
        return JolCraftStructures.DWARVEN_TRAIL_RUIN.type().get();
    }
}