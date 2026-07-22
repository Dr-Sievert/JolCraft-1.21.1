package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.LapidaryBenchBlockEntity;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.gui.slot.JolCraftResultSlot;
import net.sievert.jolcraft.world.gui.slot.JolCraftSlot;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchMenu extends AbstractContainerMenu {

    private static final int DATA_RECIPE_VALID = 0;
    private static final int DATA_ACTION_ID = 1;
    private static final int DATA_COUNT = 2;

    private final Player player;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public LapidaryBenchMenu(int windowId, Inventory playerInventory, LapidaryBenchBlockEntity be) {
        this(
                windowId,
                playerInventory,
                be,
                be.getContainerData(),
                ContainerLevelAccess.create(Objects.requireNonNull(be.getLevel()), be.getBlockPos())
        );

        if (playerInventory.player instanceof ServerPlayer sp) {
            be.refreshCachedState(sp);
        }
    }

    public LapidaryBenchMenu(int windowId, Inventory playerInventory) {
        this(
                windowId,
                playerInventory,
                new SimpleContainer(3),
                new SimpleContainerData(DATA_COUNT),
                ContainerLevelAccess.NULL
        );
    }

    private LapidaryBenchMenu(
            int windowId,
            Inventory playerInventory,
            Container container,
            ContainerData data,
            ContainerLevelAccess access
    ) {
        super(JolCraftMenuTypes.LAPIDARY_BENCH_MENU.get(), windowId);

        this.access = access;
        this.player = playerInventory.player;
        this.data = data;

        checkContainerSize(container, 3);
        checkContainerDataCount(data, DATA_COUNT);

        this.addSlot(new JolCraftSlot(container, LapidaryBenchBlockEntity.SLOT_INPUT, 32, 32) {
            @Override
            public void setChanged() {
                super.setChanged();
                refreshBenchState();
            }
        });

        this.addSlot(new JolCraftSlot(container, LapidaryBenchBlockEntity.SLOT_TOOL, 80, 16) {
            @Override
            public void setChanged() {
                super.setChanged();
                refreshBenchState();
            }
        }.mayPlaceRule(stack ->
                stack.is(JolCraftTags.Items.ARTISAN_HAMMERS) || stack.is(JolCraftTags.Items.CHISELS)
        ));

        this.addSlot(new JolCraftResultSlot(this.player, container, LapidaryBenchBlockEntity.SLOT_OUTPUT, 128, 32));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 68 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 126));
        }

        this.addDataSlots(this.data);
    }

    private void refreshBenchState() {
        if (!(this.player instanceof ServerPlayer sp)) return;

        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof LapidaryBenchBlockEntity be) {
                be.refreshCachedState(sp);
            }
        });
    }

    public boolean isRecipeValid() {
        return this.data.get(DATA_RECIPE_VALID) == 1;
    }

    public boolean isButtonActive() {
        return isRecipeValid();
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (!isButtonActive() || buttonId != this.data.get(DATA_ACTION_ID)) {
            return false;
        }

        this.access.execute((level, pos) -> {
            if (player instanceof ServerPlayer sp) {
                if (level.getBlockEntity(pos) instanceof LapidaryBenchBlockEntity be) {
                    be.handleAction(sp);
                }
            }
        });

        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == LapidaryBenchBlockEntity.SLOT_OUTPUT) {
                if (!this.moveItemStackTo(stackInSlot, 3, 39, true)) return ItemStack.EMPTY;
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index >= 3) {
                if (!this.moveItemStackTo(stackInSlot, LapidaryBenchBlockEntity.SLOT_TOOL, LapidaryBenchBlockEntity.SLOT_TOOL + 1, false)) {
                    if (!this.moveItemStackTo(stackInSlot, LapidaryBenchBlockEntity.SLOT_INPUT, LapidaryBenchBlockEntity.SLOT_INPUT + 1, false)) {
                        if (index < 30) {
                            if (!this.moveItemStackTo(stackInSlot, 30, 39, false)) return ItemStack.EMPTY;
                        } else if (!this.moveItemStackTo(stackInSlot, 3, 30, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (!this.moveItemStackTo(stackInSlot, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return itemstack;
    }

    public ItemStack getToolStack() {
        return this.slots.get(LapidaryBenchBlockEntity.SLOT_TOOL).getItem();
    }

    public boolean hasTool() {
        return !getToolStack().isEmpty();
    }

    public int getActionIdForTool() {
        return this.data.get(DATA_ACTION_ID);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, JolCraftBlocks.LAPIDARY_BENCH.get());
    }
}