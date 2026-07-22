package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class StructureVoidProcessor extends StructureProcessor {

    public static final MapCodec<StructureVoidProcessor> CODEC = MapCodec.unit(StructureVoidProcessor::new);

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            @NotNull LevelReader level,
            @NotNull BlockPos offset,
            @NotNull BlockPos pos,
            StructureTemplate.@NotNull StructureBlockInfo originalInfo,
            StructureTemplate.StructureBlockInfo currentInfo,
            @NotNull StructurePlaceSettings settings
    ) {
        return currentInfo.state().is(Blocks.STRUCTURE_VOID)
                ? null
                : currentInfo;
    }

    @Override
    public @NotNull List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor serverLevel,
            BlockPos offset,
            BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings
    ) {
        return processedBlockInfos.stream()
                .filter(info -> !info.state().is(Blocks.STRUCTURE_VOID))
                .toList();
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JolCraftProcessors.STRUCTURE_VOID.type().get();
    }
}