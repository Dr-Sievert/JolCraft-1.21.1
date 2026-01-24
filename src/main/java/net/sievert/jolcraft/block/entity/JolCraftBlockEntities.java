package net.sievert.jolcraft.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.entity.custom.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.block.entity.custom.HearthBlockEntity;
import net.sievert.jolcraft.block.entity.custom.LapidaryBenchBlockEntity;
import net.sievert.jolcraft.block.entity.custom.StrongboxBlockEntity;

import java.util.Set;
import java.util.function.Supplier;

public class JolCraftBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, JolCraft.MOD_ID);

    public static final Supplier<BlockEntityType<LapidaryBenchBlockEntity>> LAPIDARY_BENCH =
            BLOCK_ENTITIES.register("lapidary_bench", () ->
                    new BlockEntityType<>(LapidaryBenchBlockEntity::new, Set.of(JolCraftBlocks.LAPIDARY_BENCH.get())));

    public static final Supplier<BlockEntityType<StrongboxBlockEntity>> STRONGBOX =
            BLOCK_ENTITIES.register("strongbox", () ->
                    new BlockEntityType<>(StrongboxBlockEntity::new, Set.of(JolCraftBlocks.STRONGBOX.get())));

    public static final Supplier<BlockEntityType<FermentingCauldronBlockEntity>> FERMENTING_CAULDRON =
            BLOCK_ENTITIES.register("fermenting_cauldron", () ->
                    new BlockEntityType<>(FermentingCauldronBlockEntity::new, Set.of(JolCraftBlocks.FERMENTING_CAULDRON.get())));

    public static final Supplier<BlockEntityType<HearthBlockEntity>> HEARTH =
            BLOCK_ENTITIES.register("hearth", () ->
                    new BlockEntityType<>(HearthBlockEntity::new, Set.of(JolCraftBlocks.HEARTH.get())));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}