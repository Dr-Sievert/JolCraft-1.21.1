package net.sievert.jolcraft.worldgen.custom.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sievert.jolcraft.worldgen.JolCraftProcessors;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * MAKE STRUCTURE VOID PLACED BY PROCESSOR ACTUAL FUNCTION AS STRUCTURE VOID WHEN PLACING
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StructureVoidProcessor extends StructureProcessor {

    public static final MapCodec<StructureVoidProcessor> CODEC = MapCodec.unit(StructureVoidProcessor::new);
    private StructureVoidProcessor() { }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader worldView, BlockPos pos, BlockPos blockPos, StructureTemplate.StructureBlockInfo structureBlockInfoLocal, StructureTemplate.StructureBlockInfo structureBlockInfoWorld, StructurePlaceSettings structurePlacementData) {
        if (structureBlockInfoWorld.state().is(Blocks.STRUCTURE_VOID)) {
            return null;
        }
        return structureBlockInfoWorld;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return JolCraftProcessors.STRUCTURE_VOID_PROCESSOR.get();
    }
}