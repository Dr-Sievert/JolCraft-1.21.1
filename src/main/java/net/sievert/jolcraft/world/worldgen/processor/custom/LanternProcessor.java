package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class LanternProcessor extends StructureProcessor {

    public static final MapCodec<LanternProcessor> CODEC = MapCodec.unit(LanternProcessor::new);

    @Override
    public @NotNull List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor level,
            BlockPos offset,
            BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings
    ) {
        BlockPos top = processedBlockInfos.stream()
                .map(StructureTemplate.StructureBlockInfo::pos)
                .max(Comparator.comparingInt(BlockPos::getY))
                .orElse(null);

        if (top == null) {
            return processedBlockInfos;
        }

        BlockState stateAbove = level.getBlockState(top.above());

        if (stateAbove.isAir() || !stateAbove.blocksMotion()) {
            return processedBlockInfos.stream()
                    .map(info -> new StructureTemplate.StructureBlockInfo(
                            info.pos(),
                            Blocks.AIR.defaultBlockState(),
                            null
                    ))
                    .toList();
        }

        return processedBlockInfos;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JolCraftProcessors.LANTERN.type().get();
    }
}