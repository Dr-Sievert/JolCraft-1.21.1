package net.sievert.jolcraft.world.worldgen.structure.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
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
public class ForgeStructure extends AbstractJigsawStructure {

    public static final MapCodec<ForgeStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    AbstractJigsawStructure.codec(instance, ForgeStructure::new)
            );

    public ForgeStructure(
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

                BlockState surfaceBlock = context.chunkGenerator()
                        .getBaseColumn(x, z, context.heightAccessor(), context.randomState())
                        .getBlock(surfaceY - 1);

                if (surfaceBlock.isAir() || !surfaceBlock.getFluidState().isEmpty()) {
                    return false;
                }
            }
        }

        return (maxY - minY) <= 5 && maxY <= 70;
    }

    @Override
    public StructureType<?> type() {
        return JolCraftStructures.FORGE.type().get();
    }
}