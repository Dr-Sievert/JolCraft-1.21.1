package net.sievert.jolcraft.block.entity;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.entity.custom.*;

import java.util.Map;
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

    private static final Map<Supplier<? extends BlockEntityType<?>>, BlockEntityTicker<?>> TICKERS =
            ImmutableMap.<Supplier<? extends BlockEntityType<?>>, BlockEntityTicker<?>>builder()
                    .put(FERMENTING_CAULDRON, (level, pos, state, blockEntity) -> {
                        if (level instanceof ServerLevel && blockEntity instanceof FermentingCauldronBlockEntity fermentingCauldron) {
                            fermentingCauldron.tick();
                        }
                    })
                    .put(HEARTH, (level, pos, state, blockEntity) -> {
                        if (level instanceof ServerLevel && blockEntity instanceof HearthBlockEntity hearth) {
                            hearth.tick();
                        }
                    })
                    .put(STRONGBOX, (level, pos, state, blockEntity) -> {
                        if (blockEntity instanceof StrongboxBlockEntity strongbox) {
                            if (level instanceof Level) {
                                StrongboxBlockEntity.tick(level, pos, state, strongbox);
                            }
                        }
                    })
                    .build();

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!(level instanceof ServerLevel)) return null;

        for (var entry : TICKERS.entrySet()) {
            if (entry.getKey().get() == type) {
                return (BlockEntityTicker<T>) entry.getValue();
            }
        }
        return null;
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
