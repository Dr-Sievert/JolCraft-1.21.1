package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LockMenu extends JolCraftMenu {

    private static final int DATA_PROGRESS = 0;
    private static final int DATA_CORRECT_BUTTON = 1;
    private static final int DATA_LAYER_PULSE = 2;
    private static final int DATA_UNLOCK_SLOT = 3;
    private static final int DATA_COUNT = 4;

    private static final int MENU_HEIGHT_TILES = 5;

    private static final int LOCKPICK_SLOT_TILE_X = 2;
    private static final int LOCKPICK_SLOT_TILE_Y = 3;

    @Nullable
    public final StrongboxBlockEntity blockEntity;

    private final Level level;
    private final ContainerData data = new LockData();

    public LockMenu(
            int id,
            Inventory inventory,
            FriendlyByteBuf extraData
    ) {
        this(
                id,
                inventory,
                inventory.player.level().getBlockEntity(
                        extraData.readBlockPos()
                )
        );
    }

    public LockMenu(
            int id,
            Inventory inventory,
            @Nullable BlockEntity blockEntity
    ) {
        super(
                JolCraftMenuTypes.LOCK_MENU.get(),
                id,
                STANDARD_WIDTH_TILES,
                MENU_HEIGHT_TILES
        );

        this.level =
                inventory.player.level();

        this.blockEntity =
                blockEntity instanceof StrongboxBlockEntity strongbox
                        ? strongbox
                        : null;

        SimpleContainer lockpickContainer =
                new SimpleContainer(1);

        this.addSlot(
                new JolCraftSlot(
                        lockpickContainer,
                        0,
                        slot(LOCKPICK_SLOT_TILE_X),
                        slot(LOCKPICK_SLOT_TILE_Y)
                )
                        .mayPlaceRule(
                                stack -> stack.is(
                                        JolCraftItems.LOCKPICK
                                )
                        )
                        .onSlotChanged(() -> {
                            if (!this.level.isClientSide
                                    && this.blockEntity != null) {
                                this.blockEntity.setHasLockpickInserted(
                                        !getLockpickSlotItem().isEmpty()
                                );
                            }
                        })
        );

        appendPlayerInventory(
                inventory
        );

        addDataSlots(
                this.data
        );
    }

    @Override
    public void broadcastChanges() {
        if (!this.level.isClientSide
                && this.blockEntity != null) {
            this.data.set(
                    DATA_PROGRESS,
                    this.blockEntity.getLockpickProgress()
            );

            this.data.set(
                    DATA_CORRECT_BUTTON,
                    this.blockEntity.getCorrectButtonId()
            );

            this.data.set(
                    DATA_LAYER_PULSE,
                    this.blockEntity.getButtonLayerUpdatePulse()
            );

            this.data.set(
                    DATA_UNLOCK_SLOT,
                    this.blockEntity.getUnlockSlotId()
            );
        }

        super.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(
            Player player,
            int buttonId
    ) {
        if (this.level.isClientSide
                || this.blockEntity == null) {
            return false;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            return this.blockEntity.handleLockButtonPress(
                    serverPlayer,
                    buttonId,
                    getLockpickSlotItem()
            );
        }

        return false;
    }

    @Override
    public ItemStack quickMoveStack(
            Player player,
            int index
    ) {
        Slot slot =
                this.slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack =
                slot.getItem();

        ItemStack copy =
                stack.copy();

        if (index == 0) {
            if (!moveItemStackTo(
                    stack,
                    getPlayerInventorySlotStart(),
                    getPlayerInventorySlotEnd(),
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(
                stack,
                0,
                1,
                false
        )) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(
                    ItemStack.EMPTY
            );
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    public boolean stillValid(
            Player player
    ) {
        if (this.blockEntity == null
                || !this.blockEntity.isLocked()) {
            return false;
        }

        return stillValid(
                ContainerLevelAccess.create(
                        this.level,
                        this.blockEntity.getBlockPos()
                ),
                player,
                this.blockEntity.getBlockState().getBlock()
        );
    }

    @Override
    public void removed(
            Player player
    ) {
        super.removed(
                player
        );

        if (!this.level.isClientSide
                && this.blockEntity != null) {
            this.blockEntity.setHasLockpickInserted(
                    false
            );
        }

        if (this.blockEntity != null
                && this.blockEntity.getCurrentInteractingPlayer()
                == player) {
            this.blockEntity.clearCurrentInteractingPlayer(
                    player
            );
        }

        ItemStack lockpick =
                getLockpickSlotItem();

        if (!lockpick.isEmpty()) {
            dropOrPlaceInInventory(
                    player,
                    lockpick
            );

            this.slots.getFirst().set(
                    ItemStack.EMPTY
            );
        }
    }

    private static void dropOrPlaceInInventory(
            Player player,
            ItemStack stack
    ) {
        boolean droppedBecauseRemoved =
                player.isRemoved()
                        && player.getRemovalReason()
                        != Entity.RemovalReason.CHANGED_DIMENSION;

        boolean droppedBecauseDisconnected =
                player instanceof ServerPlayer serverPlayer
                        && serverPlayer.hasDisconnected();

        if (droppedBecauseRemoved
                || droppedBecauseDisconnected) {
            player.drop(
                    stack,
                    false
            );
        } else if (player instanceof ServerPlayer) {
            player.getInventory()
                    .placeItemBackInInventory(
                            stack
                    );
        }
    }

    public ItemStack getLockpickSlotItem() {
        return this.slots.getFirst()
                .getItem();
    }

    public int getLockpickProgress() {
        return this.data.get(
                DATA_PROGRESS
        );
    }

    public int getCorrectButtonId() {
        return this.data.get(
                DATA_CORRECT_BUTTON
        );
    }

    public int getButtonLayerUpdatePulse() {
        return this.data.get(
                DATA_LAYER_PULSE
        );
    }

    public int getUnlockSlotId() {
        return this.data.get(
                DATA_UNLOCK_SLOT
        );
    }

    private static final class LockData
            implements ContainerData {

        private int lockpickProgress;
        private int correctButtonId;
        private int buttonLayerUpdatePulse;
        private int unlockSlotId = -1;

        @Override
        public int get(
                int index
        ) {
            return switch (index) {
                case DATA_PROGRESS ->
                        this.lockpickProgress;
                case DATA_CORRECT_BUTTON ->
                        this.correctButtonId;
                case DATA_LAYER_PULSE ->
                        this.buttonLayerUpdatePulse;
                case DATA_UNLOCK_SLOT ->
                        this.unlockSlotId;
                default ->
                        0;
            };
        }

        @Override
        public void set(
                int index,
                int value
        ) {
            switch (index) {
                case DATA_PROGRESS ->
                        this.lockpickProgress = value;
                case DATA_CORRECT_BUTTON ->
                        this.correctButtonId = value;
                case DATA_LAYER_PULSE ->
                        this.buttonLayerUpdatePulse = value;
                case DATA_UNLOCK_SLOT ->
                        this.unlockSlotId = value;
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    }
}