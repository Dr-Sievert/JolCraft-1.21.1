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

import java.util.function.Supplier;

@SuppressWarnings("DataFlowIssue")
public final class JolCraftBlockEntities {

    private JolCraftBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JolCraft.MOD_ID);

    public static final Supplier<BlockEntityType<LapidaryBenchBlockEntity>> LAPIDARY_BENCH =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.LAPIDARY_BENCH, () ->
                    BlockEntityType.Builder.of(
                            LapidaryBenchBlockEntity::new,
                            JolCraftBlocks.LAPIDARY_BENCH.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<StrongboxBlockEntity>> STRONGBOX =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.STRONGBOX, () ->
                    BlockEntityType.Builder.of(
                            StrongboxBlockEntity::new,
                            JolCraftBlocks.STRONGBOX.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<FermentingCauldronBlockEntity>> FERMENTING_CAULDRON =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.FERMENTING_CAULDRON, () ->
                    BlockEntityType.Builder.of(
                            FermentingCauldronBlockEntity::new,
                            JolCraftBlocks.FERMENTING_CAULDRON.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<HearthBlockEntity>> HEARTH =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.HEARTH, () ->
                    BlockEntityType.Builder.of(
                            HearthBlockEntity::new,
                            JolCraftBlocks.HEARTH.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<ManagedLightBlockEntity>> MANAGED_LIGHT =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.MANAGED_LIGHT, () ->
                    BlockEntityType.Builder.of(
                            ManagedLightBlockEntity::new,
                            JolCraftBlocks.MANAGED_LIGHT.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}