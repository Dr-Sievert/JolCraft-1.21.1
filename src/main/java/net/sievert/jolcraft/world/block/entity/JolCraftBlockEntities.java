package net.sievert.jolcraft.world.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.block.entity.JolCraftBlockEntityIds;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.HearthBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.LapidaryBenchBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.ManagedLightBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.StrongboxBlockEntity;

import java.util.Set;
import java.util.function.Supplier;

public final class JolCraftBlockEntities {

    private JolCraftBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JolCraft.MOD_ID);

    public static final Supplier<BlockEntityType<LapidaryBenchBlockEntity>> LAPIDARY_BENCH =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.LAPIDARY_BENCH, () ->
                    new BlockEntityType<>(LapidaryBenchBlockEntity::new,
                            Set.of(JolCraftBlocks.LAPIDARY_BENCH.get())));

    public static final Supplier<BlockEntityType<StrongboxBlockEntity>> STRONGBOX =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.STRONGBOX, () ->
                    new BlockEntityType<>(StrongboxBlockEntity::new,
                            Set.of(JolCraftBlocks.STRONGBOX.get())));

    public static final Supplier<BlockEntityType<FermentingCauldronBlockEntity>> FERMENTING_CAULDRON =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.FERMENTING_CAULDRON, () ->
                    new BlockEntityType<>(FermentingCauldronBlockEntity::new,
                            Set.of(JolCraftBlocks.FERMENTING_CAULDRON.get())));

    public static final Supplier<BlockEntityType<HearthBlockEntity>> HEARTH =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.HEARTH, () ->
                    new BlockEntityType<>(HearthBlockEntity::new,
                            Set.of(JolCraftBlocks.HEARTH.get())));

    public static final Supplier<BlockEntityType<ManagedLightBlockEntity>> MANAGED_LIGHT =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.MANAGED_LIGHT, () ->
                    new BlockEntityType<>(ManagedLightBlockEntity::new,
                            Set.of(JolCraftBlocks.MANAGED_LIGHT.get())));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}