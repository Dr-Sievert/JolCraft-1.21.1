package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.StrongboxBlockEntity;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class StrongboxMenu extends AbstractContainerMenu {
    private static final int STRONGBOX_SIZE = 18;

    @Nullable
    public final StrongboxBlockEntity blockEntity;

    private final Level level;

    public StrongboxMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(
                id,
                inv,
                extraData != null
                        ? inv.player.level().getBlockEntity(extraData.readBlockPos())
                        : null
        );
    }

    public @Nullable StrongboxBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public StrongboxMenu(int id, Inventory inv, @Nullable BlockEntity blockEntity) {
        super(JolCraftMenuTypes.STRONGBOX_MENU.get(), id);

        this.level = inv.player.level();
        this.blockEntity = blockEntity instanceof StrongboxBlockEntity strongbox ? strongbox : null;

        var strongboxContainer = this.blockEntity != null
                ? this.blockEntity
                : new SimpleContainer(STRONGBOX_SIZE);

        for (int row = 0; row < 2; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(strongboxContainer, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 68 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 126));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        int hotbarStart = STRONGBOX_SIZE + 27;
        int hotbarEnd = hotbarStart + 9;

        if (index < STRONGBOX_SIZE) {
            if (!moveItemStackTo(stack, STRONGBOX_SIZE, hotbarEnd, true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, STRONGBOX_SIZE, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (blockEntity == null) {
            return false;
        }

        return stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player,
                blockEntity.getBlockState().getBlock()
        );
    }
}