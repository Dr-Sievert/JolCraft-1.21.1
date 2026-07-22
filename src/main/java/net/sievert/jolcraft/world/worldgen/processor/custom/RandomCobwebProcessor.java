package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class RandomCobwebProcessor extends StructureProcessor {

    public static final MapCodec<RandomCobwebProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf(JolCraftDictionary.CHANCE).forGetter(processor -> processor.chance)
    ).apply(instance, RandomCobwebProcessor::new));

    private final float chance;

    public RandomCobwebProcessor(float chance) {
        this.chance = chance;
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
        Map<BlockPos, BlockState> states = processedBlockInfos.stream()
                .collect(Collectors.toMap(
                        StructureTemplate.StructureBlockInfo::pos,
                        StructureTemplate.StructureBlockInfo::state,
                        (first, second) -> second
                ));

        List<StructureTemplate.StructureBlockInfo> result = new ArrayList<>(processedBlockInfos.size());

        for (StructureTemplate.StructureBlockInfo info : processedBlockInfos) {
            if (shouldReplace(info, states, settings)) {
                result.add(new StructureTemplate.StructureBlockInfo(
                        info.pos(),
                        Blocks.COBWEB.defaultBlockState(),
                        info.nbt()
                ));
            } else {
                result.add(info);
            }
        }

        return result;
    }

    private boolean shouldReplace(
            StructureTemplate.StructureBlockInfo info,
            Map<BlockPos, BlockState> states,
            StructurePlaceSettings settings
    ) {
        return info.state().isAir()
                && settings.getRandom(info.pos()).nextFloat() < chance
                && hasSolidNeighbors(states, info.pos());
    }

    private static boolean hasSolidNeighbors(Map<BlockPos, BlockState> states, BlockPos pos) {
        int solidNeighbors = 0;

        for (Direction direction : Direction.values()) {
            BlockState state = states.get(pos.relative(direction));

            if (state != null && state.blocksMotion() && ++solidNeighbors >= 2) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JolCraftProcessors.RANDOM_COBWEB.type().get();
    }
}