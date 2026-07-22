package net.sievert.jolcraft.world.block.entity.custom.base;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface TickingBlockEntity {

    static <T extends BlockEntity> BlockEntityTicker<T> tickOnBoth() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof TickingBlockEntity tickable) {
                if (level.isClientSide()) {
                    tickable.tickClient();
                } else {
                    tickable.tickServer();
                }
            }
        };
    }

    static <T extends BlockEntity> BlockEntityTicker<T> tickOnClient() {
        return (level, pos, state, blockEntity) -> {
            if (!level.isClientSide()) return;

            if (blockEntity instanceof TickingBlockEntity tickable) {
                tickable.tickClient();
            }
        };
    }

    static <T extends BlockEntity> BlockEntityTicker<T> tickOnServer() {
        return (level, pos, state, blockEntity) -> {
            if (level.isClientSide()) return;

            if (blockEntity instanceof TickingBlockEntity tickable) {
                tickable.tickServer();
            }
        };
    }

    /**
     * Override for block entities that need client-side ticking.
     */
    default void tickClient() {
    }

    /**
     * Override for block entities that need server-side ticking.
     */
    default void tickServer() {
    }
}