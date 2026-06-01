package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.StrongboxBlockEntity;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.gui.slot.JolCraftSlot;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class LockMenu extends AbstractContainerMenu {

    // ---------------------------------------------------------------------
    // Data indices (ContainerData)
    // ---------------------------------------------------------------------

    private static final int DATA_PROGRESS = 0;
    private static final int DATA_CORRECT_BUTTON = 1;
    private static final int DATA_LAYER_PULSE = 2;
    private static final int DATA_DECAY_TICKS = 3;
    private static final int DATA_PROGRESS_BOOST = 4;
    private static final int DATA_UNLOCK_SLOT = 5;
    private static final int DATA_COUNT = 6;

    // ---------------------------------------------------------------------
    // Slot layout constants
    // ---------------------------------------------------------------------

    private static final int SLOT_LOCKPICK_X = 16;
    private static final int SLOT_LOCKPICK_Y = 16;

    private static final int INV_X = 8;
    private static final int INV_Y = 68;
    private static final int HOTBAR_Y = 126;

    // ---------------------------------------------------------------------
    // Menu state
    // ---------------------------------------------------------------------

    @Nullable
    public final StrongboxBlockEntity blockEntity;
    private final Level level;

    private final ContainerData data = new LockData();

    public LockMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(
                id,
                inv,
                extraData != null
                        ? inv.player.level().getBlockEntity(extraData.readBlockPos())
                        : null
        );
    }

    public LockMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(JolCraftMenuTypes.LOCK_MENU.get(), id);

        this.level = inv.player.level();
        this.blockEntity = blockEntity instanceof StrongboxBlockEntity strongbox ? strongbox : null;

        addDataSlots(this.data);

        SimpleContainer lockpickContainer = new SimpleContainer(1);

        this.addSlot(
                new JolCraftSlot(lockpickContainer, 0, SLOT_LOCKPICK_X, SLOT_LOCKPICK_Y)
                        .mayPlaceRule(stack -> stack.is(JolCraftItems.LOCKPICK))
                        .onSlotChanged(() -> {
                            if (!level.isClientSide && this.blockEntity != null) {
                                this.blockEntity.setHasLockpickInserted(!getLockpickSlotItem().isEmpty());
                            }
                        })
        );

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, INV_X + col * 18, INV_Y + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, INV_X + col * 18, HOTBAR_Y));
        }
    }

    // ---------------------------------------------------------------------
    // Sync + click forwarding
    // ---------------------------------------------------------------------

    @Override
    public void broadcastChanges() {
        if (!level.isClientSide && blockEntity != null) {
            data.set(DATA_PROGRESS, blockEntity.getLockpickProgress());
            data.set(DATA_CORRECT_BUTTON, blockEntity.getCorrectButtonId());
            data.set(DATA_LAYER_PULSE, blockEntity.getButtonLayerUpdatePulse());
            data.set(DATA_DECAY_TICKS, blockEntity.getDecayTicks());
            data.set(DATA_PROGRESS_BOOST, blockEntity.getProgressBoost());
            data.set(DATA_UNLOCK_SLOT, blockEntity.getUnlockSlotId());
        }

        super.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int buttonId) {
        if (level.isClientSide || blockEntity == null) return false;

        if (player instanceof ServerPlayer sp) {
            return blockEntity.handleLockButtonPress(sp, buttonId, getLockpickSlotItem());
        }

        return false;
    }

    // ---------------------------------------------------------------------
    // Shift-click handling
    // ---------------------------------------------------------------------

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        int lockSize = 1;
        int invEnd = lockSize + 27;
        int hotbarEnd = invEnd + 9;

        boolean fromLockpick = index < lockSize;
        if (fromLockpick) {
            if (!moveItemStackTo(stack, lockSize, hotbarEnd, true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, lockSize, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    // ---------------------------------------------------------------------
    // Validity + close behavior
    // ---------------------------------------------------------------------

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (blockEntity == null) {
            return false;
        }

        if (!blockEntity.isLocked()) {
            return false;
        }

        return stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player,
                blockEntity.getBlockState().getBlock()
        );
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        if (!level.isClientSide && blockEntity != null) {
            blockEntity.setHasLockpickInserted(false);
        }

        if (blockEntity != null && blockEntity.getCurrentInteractingPlayer() == player) {
            blockEntity.clearCurrentInteractingPlayer(player);
        }

        if (!this.slots.isEmpty()) {
            ItemStack lockpick = getLockpickSlotItem();
            if (!lockpick.isEmpty()) {
                dropOrPlaceInInventory(player, lockpick);
                this.slots.getFirst().set(ItemStack.EMPTY);
            }
        }
    }

    private static void dropOrPlaceInInventory(Player player, ItemStack stack) {
        boolean droppedBecauseRemoved = player.isRemoved()
                && player.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;

        boolean droppedBecauseDisconnected = player instanceof ServerPlayer sp && sp.hasDisconnected();

        if (droppedBecauseRemoved || droppedBecauseDisconnected) {
            player.drop(stack, false);
        } else if (player instanceof ServerPlayer) {
            player.getInventory().placeItemBackInInventory(stack);
        }
    }

    // ---------------------------------------------------------------------
    // Convenience accessors
    // ---------------------------------------------------------------------

    public ItemStack getLockpickSlotItem() {
        return this.slots.getFirst().getItem();
    }

    public int getLockpickProgress() { return data.get(DATA_PROGRESS); }
    public int getCorrectButtonId() { return data.get(DATA_CORRECT_BUTTON); }
    public int getButtonLayerUpdatePulse() { return data.get(DATA_LAYER_PULSE); }
    public int getDecayTicks() { return data.get(DATA_DECAY_TICKS); }
    public int getProgressBoost() { return data.get(DATA_PROGRESS_BOOST); }
    public int getUnlockSlotId() { return data.get(DATA_UNLOCK_SLOT); }

    // ---------------------------------------------------------------------
    // ContainerData impl
    // ---------------------------------------------------------------------

    private static final class LockData implements ContainerData {
        private int lockpickProgress = 0;
        private int correctButtonId = 0;
        private int buttonLayerUpdatePulse = 0;
        private int decayTicks = 1;
        private int progressBoost = 0;
        private int unlockSlotId = -1;

        @Override
        public int get(int idx) {
            return switch (idx) {
                case DATA_PROGRESS -> lockpickProgress;
                case DATA_CORRECT_BUTTON -> correctButtonId;
                case DATA_LAYER_PULSE -> buttonLayerUpdatePulse;
                case DATA_DECAY_TICKS -> decayTicks;
                case DATA_PROGRESS_BOOST -> progressBoost;
                case DATA_UNLOCK_SLOT -> unlockSlotId;
                default -> 0;
            };
        }

        @Override
        public void set(int idx, int value) {
            switch (idx) {
                case DATA_PROGRESS -> lockpickProgress = value;
                case DATA_CORRECT_BUTTON -> correctButtonId = value;
                case DATA_LAYER_PULSE -> buttonLayerUpdatePulse = value;
                case DATA_DECAY_TICKS -> decayTicks = value;
                case DATA_PROGRESS_BOOST -> progressBoost = value;
                case DATA_UNLOCK_SLOT -> unlockSlotId = value;
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    }
}