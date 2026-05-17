package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;
import org.jetbrains.annotations.NotNull;

/**
 * MAKE STRUCTURE VOID PLACED BY PROCESSOR ACTUAL FUNCTION AS STRUCTURE VOID WHEN PLACING
 */
public class StructureVoidProcessor extends StructureProcessor {

    public static final MapCodec<StructureVoidProcessor> CODEC = MapCodec.unit(StructureVoidProcessor::new);

    @SuppressWarnings("deprecation")
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            @NotNull LevelReader worldView,
            @NotNull BlockPos pos, @NotNull
            BlockPos blockPos,
            StructureTemplate.@NotNull StructureBlockInfo structureBlockInfoLocal,
            StructureTemplate.StructureBlockInfo structureBlockInfoWorld,
            @NotNull StructurePlaceSettings structurePlacementData) {
        if (structureBlockInfoWorld.state().is(Blocks.STRUCTURE_VOID)) {
            return null;
        }
        return structureBlockInfoWorld;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JolCraftProcessors.STRUCTURE_VOID.type().get();
    }
}