package net.sievert.jolcraft.world.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.block.entity.JolCraftBlockEntityIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.MortarBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingBarrelBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.HearthBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.LapidaryBenchBlockEntity;
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

    public static final Supplier<BlockEntityType<MortarBlockEntity>> MORTAR =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.MORTAR, () ->
                    BlockEntityType.Builder.of(
                            MortarBlockEntity::new,
                            JolCraftBlocks.MORTAR.get()
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

    public static final Supplier<BlockEntityType<FermentingBarrelBlockEntity>> FERMENTING_BARREL =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.FERMENTING_BARREL, () ->
                    BlockEntityType.Builder.of(
                            FermentingBarrelBlockEntity::new,
                            JolCraftBlocks.FERMENTING_BARREL.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<HearthBlockEntity>> HEARTH =
            BLOCK_ENTITIES.register(JolCraftBlockEntityIds.HEARTH, () ->
                    BlockEntityType.Builder.of(
                            HearthBlockEntity::new,
                            JolCraftBlocks.HEARTH.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} block entity types",
                BLOCK_ENTITIES.getEntries().size()
        );
    }
}