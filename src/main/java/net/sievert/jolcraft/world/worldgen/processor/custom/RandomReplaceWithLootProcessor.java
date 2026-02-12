package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RandomReplaceWithLootProcessor extends StructureProcessor {

    public static final MapCodec<RandomReplaceWithLootProcessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf(JolCraftDictionary.INPUT).forGetter(p -> p.inputBlockId),
                    ResourceLocation.CODEC.fieldOf(JolCraftDictionary.OUTPUT).forGetter(p -> p.outputBlockId),
                    Codec.floatRange(0f, 1f).fieldOf(JolCraftDictionary.CHANCE).forGetter(p -> p.probability),
                    ResourceLocation.CODEC.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.LOOT, JolCraftDictionary.TABLE)).forGetter(p -> p.lootTable)
            ).apply(instance, RandomReplaceWithLootProcessor::new)
    );

    private final ResourceLocation inputBlockId;
    private final ResourceLocation outputBlockId;
    private final float probability;
    private final ResourceLocation lootTable;

    // Cached resolved blocks (avoid repeated registry lookups in hot path)
    private transient Block cachedInputBlock;
    private transient Block cachedOutputBlock;

    public RandomReplaceWithLootProcessor(
            ResourceLocation inputBlockId,
            ResourceLocation outputBlockId,
            float probability,
            ResourceLocation lootTable
    ) {
        this.inputBlockId = inputBlockId;
        this.outputBlockId = outputBlockId;
        this.probability = probability;
        this.lootTable = lootTable;
    }

    private Block resolveInput(LevelReader level) {
        if (cachedInputBlock != null) return cachedInputBlock;

        cachedInputBlock = level.registryAccess()
                .lookupOrThrow(Registries.BLOCK)
                .get(inputBlockId)
                .map(Holder::value)
                .orElseThrow(() -> new IllegalStateException("Unknown input_block: " + inputBlockId));

        return cachedInputBlock;
    }

    private Block resolveOutput(LevelReader level) {
        if (cachedOutputBlock != null) return cachedOutputBlock;

        cachedOutputBlock = level.registryAccess()
                .lookupOrThrow(Registries.BLOCK)
                .get(outputBlockId)
                .map(Holder::value)
                .orElseThrow(() -> new IllegalStateException("Unknown output_block: " + outputBlockId));

        return cachedOutputBlock;
    }



    @SuppressWarnings("deprecation")
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos blockpos,
            BlockPos relativePos,
            StructureTemplate.StructureBlockInfo original,
            StructureTemplate.StructureBlockInfo current,
            StructurePlaceSettings settings
    ) {
        Block input = resolveInput(level);

        if (current.state().is(input)) {
            if (settings.getRandom(current.pos()).nextFloat() < probability) {
                Block output = resolveOutput(level);
                BlockState replacedState = output.defaultBlockState();

                CompoundTag nbt = new CompoundTag();
                nbt.putString(JolCraftStrings.underscored(JolCraftDictionary.LOOT, JolCraftDictionary.TABLE), lootTable.toString());

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