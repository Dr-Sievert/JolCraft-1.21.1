package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.StrongboxBlockEntity;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StrongboxMenu extends JolCraftMenu {

    private static final int STRONGBOX_SIZE = 18;
    private static final int MENU_HEIGHT_TILES = 4;

    private static final int STRONGBOX_SLOT_X = 8;
    private static final int STRONGBOX_SLOT_Y = 18;

    @Nullable
    public final StrongboxBlockEntity blockEntity;

    private final Level level;

    public StrongboxMenu(
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

    public StrongboxMenu(
            int id,
            Inventory inventory,
            @Nullable BlockEntity blockEntity
    ) {
        super(
                JolCraftMenuTypes.STRONGBOX_MENU.get(),
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

        var strongboxContainer =
                this.blockEntity != null
                        ? this.blockEntity
                        : new SimpleContainer(
                        STRONGBOX_SIZE
                );

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        strongboxContainer,
                        column + row * 9,
                        STRONGBOX_SLOT_X
                                + column * 18,
                        STRONGBOX_SLOT_Y
                                + row * 18
                ));
            }
        }

        appendPlayerInventory(
                inventory
        );
    }

    public @Nullable StrongboxBlockEntity getBlockEntity() {
        return this.blockEntity;
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

        if (index < STRONGBOX_SIZE) {
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
                STRONGBOX_SIZE,
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
        if (this.blockEntity == null) {
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
}