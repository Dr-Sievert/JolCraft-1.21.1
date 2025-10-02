package net.sievert.jolcraft.worldgen.custom.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.sievert.jolcraft.worldgen.structure.JolCraftStructures;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ForgeStructure extends Structure {
    public static final MapCodec<ForgeStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ForgeStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
            ).apply(instance, ForgeStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    public ForgeStructure(StructureSettings config,
                          Holder<StructureTemplatePool> startPool,
                          Optional<ResourceLocation> startJigsawName,
                          int size,
                          HeightProvider startHeight,
                          Optional<Heightmap.Types> projectStartToHeightmap,
                          int maxDistanceFromCenter,
                          DimensionPadding dimensionPadding,
                          LiquidSettings liquidSettings) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    private static boolean extraSpawningChecks(GenerationContext context) {
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
                        context.heightAccessor(), context.randomState()
                );

                if (surfaceY > maxY) maxY = surfaceY;
                if (surfaceY < minY) minY = surfaceY;

                BlockPos surfacePos = new BlockPos(x, surfaceY - 1, z);
                BlockState surfaceBlock = context.chunkGenerator()
                        .getBaseColumn(surfacePos.getX(), surfacePos.getZ(), context.heightAccessor(), context.randomState())
                        .getBlock(surfaceY - 1);

                if (surfaceBlock.isAir() || !surfaceBlock.getFluidState().isEmpty()) {
                    return false;
                }
            }
        }

        return maxY - minY <= 5 && maxY <= 70;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!extraSpawningChecks(context)) {
            return Optional.empty();
        }

        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        return JigsawPlacement.addPieces(
                context,
                this.startPool,
                this.startJigsawName,
                this.size,
                blockPos,
                false,
                this.projectStartToHeightmap,
                this.maxDistanceFromCenter,
                PoolAliasLookup.EMPTY,
                this.dimensionPadding,
                this.liquidSettings
        );
    }

    @Override
    public StructureType<?> type() {
        return JolCraftStructures.FORGE_STRUCTURE.get();
    }
}