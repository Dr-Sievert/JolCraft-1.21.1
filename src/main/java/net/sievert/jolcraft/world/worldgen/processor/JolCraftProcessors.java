package net.sievert.jolcraft.world.worldgen.processor;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftProcessorIds;
import net.sievert.jolcraft.world.worldgen.processor.custom.RandomReplaceWithLootProcessor;
import net.sievert.jolcraft.world.worldgen.processor.custom.StructureVoidProcessor;

public final class JolCraftProcessors {

    private JolCraftProcessors(){}

    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, JolCraft.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<RandomReplaceWithLootProcessor>> RANDOM_REPLACE_WITH_LOOT_PROCESSOR =
            PROCESSOR_TYPES.register(JolCraftProcessorIds.RANDOM_REPLACE_WITH_LOOT, () -> () -> RandomReplaceWithLootProcessor.CODEC);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<StructureVoidProcessor>> STRUCTURE_VOID_PROCESSOR =
            PROCESSOR_TYPES.register(JolCraftProcessorIds.STRUCTURE_VOID_PROCESSOR, () -> () -> StructureVoidProcessor.CODEC);

    public static void register(IEventBus eventBus) {
        PROCESSOR_TYPES.register(eventBus);
    }
}