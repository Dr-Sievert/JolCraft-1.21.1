package net.sievert.jolcraft.worldgen.custom.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.sievert.jolcraft.worldgen.JolCraftProcessors;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RandomReplaceWithLootProcessor extends StructureProcessor {

    public static final MapCodec<RandomReplaceWithLootProcessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input_block").forGetter(p -> p.inputBlock),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output_block").forGetter(p -> p.outputBlock),
                    com.mojang.serialization.Codec.floatRange(0f, 1f).fieldOf("probability").forGetter(p -> p.probability),
                    ResourceLocation.CODEC.fieldOf("loot_table").forGetter(p -> p.lootTable)
            ).apply(instance, RandomReplaceWithLootProcessor::new)
    );

    private final Block inputBlock;
    private final Block outputBlock;
    private final float probability;
    private final ResourceLocation lootTable;

    public RandomReplaceWithLootProcessor(Block inputBlock, Block outputBlock, float probability, ResourceLocation lootTable) {
        this.inputBlock = inputBlock;
        this.outputBlock = outputBlock;
        this.probability = probability;
        this.lootTable = lootTable;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos blockpos,
            BlockPos relativePos,
            StructureTemplate.StructureBlockInfo original,
            StructureTemplate.StructureBlockInfo current,
            StructurePlaceSettings settings
    ) {
        if (current.state().is(inputBlock)) {
            RandomSource random = RandomSource.create();
            BlockPos pos = current.pos();
            long seed = (((long) pos.getX() * 3129871L) ^ ((long) pos.getZ() * 116129781L) ^ (long) pos.getY());
            seed = seed * seed * 42317861L + seed * 11L;
            seed = seed >> 16;
            random.setSeed(Long.remainderUnsigned(seed, Long.MAX_VALUE));

            if (random.nextFloat() < probability) {
                BlockState replacedState = outputBlock.defaultBlockState();
                CompoundTag nbt = new CompoundTag();
                nbt.putString("LootTable", lootTable.toString());

                return new StructureTemplate.StructureBlockInfo(current.pos(), replacedState, nbt);
            }
        }

        return current;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return JolCraftProcessors.RANDOM_REPLACE_WITH_LOOT_PROCESSOR.get();
    }
}
