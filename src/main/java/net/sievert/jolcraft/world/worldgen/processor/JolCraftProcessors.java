package net.sievert.jolcraft.world.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftProcessorIds;
import net.sievert.jolcraft.world.worldgen.processor.custom.*;

public final class JolCraftProcessors {

    private JolCraftProcessors() {}

    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, JolCraft.MOD_ID);

    public static final ProcessorDef<RandomReplaceWithLootProcessor> RANDOM_REPLACE_WITH_LOOT =
            registerProcessor(JolCraftProcessorIds.RANDOM_REPLACE_WITH_LOOT, RandomReplaceWithLootProcessor.CODEC);

    public static final ProcessorDef<AddLootTableProcessor> ADD_LOOT_TABLE =
            registerProcessor(JolCraftProcessorIds.ADD_LOOT_TABLE, AddLootTableProcessor.CODEC);

    public static final ProcessorDef<RandomCobwebProcessor> RANDOM_COBWEB =
            registerProcessor(JolCraftProcessorIds.RANDOM_COBWEB, RandomCobwebProcessor.CODEC);

    public static final ProcessorDef<RandomCaveInProcessor> RANDOM_CAVE_IN =
            registerProcessor(JolCraftProcessorIds.RANDOM_CAVE_IN, RandomCaveInProcessor.CODEC);

    public static final ProcessorDef<LanternProcessor> LANTERN =
            registerProcessor(JolCraftProcessorIds.LANTERN, LanternProcessor.CODEC);

    public static final ProcessorDef<StructureVoidProcessor> STRUCTURE_VOID =
            registerProcessor(JolCraftProcessorIds.STRUCTURE_VOID, StructureVoidProcessor.CODEC);

    public static final ProcessorDef<BookshelfTomeProcessor> BOOKSHELF_TOME =
            registerProcessor(JolCraftProcessorIds.BOOKSHELF_TOME, BookshelfTomeProcessor.CODEC);

    public record ProcessorDef<T extends StructureProcessor>(
            DeferredHolder<StructureProcessorType<?>, StructureProcessorType<T>> type,
            ResourceKey<StructureProcessorList> list
    ) {}

    private static <T extends StructureProcessor> ProcessorDef<T> registerProcessor(
            String id,
            MapCodec<T> codec
    ) {
        return new ProcessorDef<>(
                PROCESSOR_TYPES.register(id, () -> () -> codec),
                ResourceKey.create(Registries.PROCESSOR_LIST, JolCraft.location(id))
        );
    }

    public static void register(IEventBus eventBus) {
        PROCESSOR_TYPES.register(eventBus);
    }
}