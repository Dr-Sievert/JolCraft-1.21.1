package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RandomCaveInProcessor extends StructureProcessor {

    private static final int MAX_SOLID_DEPTH = 4;
    private static final int OPEN_SIDE_DEPTH = 3;
    private static final float EXTRA_PILLAR_CHANCE = 0.65F;

    public static final MapCodec<RandomCaveInProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf(JolCraftParameterIds.CHANCE).forGetter(processor -> processor.chance)
    ).apply(instance, RandomCaveInProcessor::new));

    private final float chance;

    public RandomCaveInProcessor(float chance) {
        this.chance = chance;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor serverLevel,
            BlockPos offset,
            BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings
    ) {
        RandomSource random = pieceRandom(serverLevel, processedBlockInfos);

        if (random.nextFloat() >= chance) {
            return processedBlockInfos;
        }

        Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks = processedBlockInfos.stream()
                .collect(Collectors.toMap(
                        StructureTemplate.StructureBlockInfo::pos,
                        info -> info,
                        (first, second) -> second
                ));

        List<BlockPos> tops = processedBlockInfos.stream()
                .map(StructureTemplate.StructureBlockInfo::pos)
                .filter(posToCheck -> canPlacePillar(blocks, posToCheck))
                .toList();

        if (tops.isEmpty()) {
            return processedBlockInfos;
        }

        BlockPos top = tops.get(random.nextInt(tops.size()));
        int radius = 1 + random.nextInt(3);

        carvePillar(blocks, top, serverLevel);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if ((x == 0 && z == 0) || random.nextFloat() > EXTRA_PILLAR_CHANCE) {
                    continue;
                }

                BlockPos pillarTop = top.offset(x, 0, z);

                if (canPlacePillar(blocks, pillarTop)) {
                    carvePillar(blocks, pillarTop, serverLevel);
                }
            }
        }

        return new ArrayList<>(blocks.values());
    }

    private static RandomSource pieceRandom(
            ServerLevelAccessor serverLevel,
            List<StructureTemplate.StructureBlockInfo> blocks
    ) {
        long seed = serverLevel.getLevel().getSeed();

        if (!blocks.isEmpty()) {
            seed ^= blocks.getFirst().pos().asLong();
        }

        return RandomSource.create(seed);
    }

    private static void carvePillar(
            Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks,
            BlockPos start,
            ServerLevelAccessor serverLevel
    ) {
        BlockPos.MutableBlockPos cursor = start.mutable();
        StructureTemplate.StructureBlockInfo ceiling = null;

        while (cursor.getY() >= serverLevel.getMinBuildHeight()) {
            StructureTemplate.StructureBlockInfo info = blocks.get(cursor);

            if (info != null && info.state().blocksMotion()) {
                ceiling = info;
                break;
            }

            cursor.move(0, -1, 0);
        }

        if (ceiling == null) {
            return;
        }

        cursor.set(ceiling.pos());
        StructureTemplate.StructureBlockInfo lastSolid = null;

        while (cursor.getY() >= serverLevel.getMinBuildHeight()) {
            StructureTemplate.StructureBlockInfo info = blocks.get(cursor);

            if (info == null || info.state().isAir()) {
                if (lastSolid != null) {
                    set(blocks, lastSolid.pos(), Blocks.AIR);
                }

                return;
            }

            if (!info.state().hasBlockEntity()) {
                set(blocks, info.pos(), Blocks.STRUCTURE_VOID);
                lastSolid = info;
            }

            cursor.move(0, -1, 0);
        }
    }

    private static boolean canPlacePillar(
            Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks,
            BlockPos pos
    ) {
        return isCeiling(blocks, pos) && hasOpenSide(blocks, pos);
    }

    private static boolean isCeiling(
            Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks,
            BlockPos pos
    ) {
        if (blocks.containsKey(pos.above())) {
            return false;
        }

        for (int i = 1; i <= MAX_SOLID_DEPTH; i++) {
            StructureTemplate.StructureBlockInfo below = blocks.get(pos.below(i));

            if (below == null || !below.state().blocksMotion()) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasOpenSide(
            Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks,
            BlockPos pos
    ) {
        for (int y = 0; y >= -OPEN_SIDE_DEPTH; y--) {
            BlockPos check = pos.offset(0, y, 0);

            if (isOpen(blocks, check.north())
                    || isOpen(blocks, check.south())
                    || isOpen(blocks, check.east())
                    || isOpen(blocks, check.west())) {
                return true;
            }
        }

        return false;
    }

    private static boolean isOpen(
            Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks,
            BlockPos pos
    ) {
        StructureTemplate.StructureBlockInfo info = blocks.get(pos);
        return info == null || info.state().isAir();
    }

    private static void set(
            Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks,
            BlockPos pos,
            Block block
    ) {
        blocks.put(pos, new StructureTemplate.StructureBlockInfo(
                pos,
                block.defaultBlockState(),
                null
        ));
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return JolCraftProcessors.RANDOM_CAVE_IN.type().get();
    }
}