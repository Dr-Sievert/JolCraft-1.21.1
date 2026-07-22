package net.sievert.jolcraft.world.block.entity.custom.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public interface SyncingBlockEntity {

    @NotNull
    Packet<ClientGamePacketListener> getUpdatePacket();

    @NotNull
    CompoundTag getUpdateTag(HolderLookup.Provider registries);

    default @NotNull ClientboundBlockEntityDataPacket defaultUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create((BlockEntity) this);
    }

    default void sync() {
        syncClient();
    }

    default void syncClient() {
        syncWithFlags(Block.UPDATE_CLIENTS);
    }

    default void syncAll() {
        syncWithFlags(Block.UPDATE_ALL);
    }

    default void syncIf(boolean condition) {
        if (condition) syncClient();
    }

    default void syncAllIf(boolean condition) {
        if (condition) syncAll();
    }

    private void syncWithFlags(int flags) {
        BlockEntity be = (BlockEntity) this;
        if (be.getLevel() == null || be.getLevel().isClientSide()) return;

        be.setChanged();
        be.getLevel().sendBlockUpdated(
                be.getBlockPos(),
                be.getBlockState(),
                be.getBlockState(),
                flags
        );
    }
}