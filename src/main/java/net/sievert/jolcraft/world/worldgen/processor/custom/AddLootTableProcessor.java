package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.LootTable;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AddLootTableProcessor extends StructureProcessor {

    public static final MapCodec<AddLootTableProcessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC
                            .fieldOf(JolCraftDictionary.INPUT)
                            .forGetter(processor -> processor.targetBlockId),
                    ResourceKey.codec(Registries.LOOT_TABLE)
                            .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.LOOT, JolCraftDictionary.TABLE))
                            .forGetter(processor -> processor.lootTable)
            ).apply(instance, AddLootTableProcessor::new)
    );

    private final ResourceLocation targetBlockId;
    private final ResourceKey<LootTable> lootTable;

    private transient Block cachedTargetBlock;

    public AddLootTableProcessor(ResourceLocation targetBlockId, ResourceKey<LootTable> lootTable) {
        this.targetBlockId = targetBlockId;
        this.lootTable = lootTable;
    }

    private Block resolveTarget(LevelReader level) {
        if (cachedTargetBlock != null) {
            return cachedTargetBlock;
        }

        cachedTargetBlock = level.registryAccess()
                .lookupOrThrow(Registries.BLOCK)
                .get(ResourceKey.create(Registries.BLOCK, targetBlockId))
                .map(Holder::value)
                .orElseThrow(() -> new IllegalStateException("Unknown input block: " + targetBlockId));

        return cachedTargetBlock;
    }

    @SuppressWarnings("deprecation")
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            @NotNull LevelReader level,
            @NotNull BlockPos pos,
            @NotNull BlockPos relativePos,
            StructureTemplate.@NotNull StructureBlockInfo original,
            StructureTemplate.@NotNull StructureBlockInfo current,
            @NotNull StructurePlaceSettings settings
    ) {
        Block target = resolveTarget(level);

        if (!current.state().is(target)) {
            return current;
        }

        CompoundTag nbt = current.nbt() == null
                ? new CompoundTag()
                : current.nbt().copy();

        nbt.putString(RandomizableContainer.LOOT_TABLE_TAG, lootTable.location().toString());
        nbt.putLong(RandomizableContainer.LOOT_TABLE_SEED_TAG, settings.getRandom(current.pos()).nextLong());

        return new StructureTemplate.StructureBlockInfo(
                current.pos(),
                current.state(),
                nbt
        );
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JolCraftProcessors.ADD_LOOT_TABLE.type().get();
    }
}