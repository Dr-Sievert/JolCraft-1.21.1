package net.sievert.jolcraft.world.gui.custom.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.LapidaryBenchBlockEntity;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.gui.custom.slot.JolCraftResultSlot;
import net.sievert.jolcraft.world.gui.custom.slot.JolCraftSlot;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchMenu extends AbstractContainerMenu {

    private static final int DATA_RECIPE_VALID = 0;
    private static final int DATA_COUNT = 1;

    private final Player player;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public LapidaryBenchMenu(int windowId, Inventory playerInventory, LapidaryBenchBlockEntity be) {
        this(windowId, playerInventory, be, ContainerLevelAccess.create(Objects.requireNonNull(be.getLevel()), be.getBlockPos()));
    }

    public LapidaryBenchMenu(int windowId, Inventory playerInventory) {
        this(windowId, playerInventory, new SimpleContainer(3), ContainerLevelAccess.NULL);
    }

    private LapidaryBenchMenu(int windowId, Inventory playerInventory, Container container, ContainerLevelAccess access) {
        super(JolCraftMenuTypes.LAPIDARY_BENCH_MENU.get(), windowId);
        this.access = access;
        this.player = playerInventory.player;

        checkContainerSize(container, 3);

        this.addSlot(new JolCraftSlot(container, LapidaryBenchBlockEntity.SLOT_INPUT, 32, 32));

        this.addSlot(new JolCraftSlot(container, LapidaryBenchBlockEntity.SLOT_TOOL, 80, 16).mayPlaceRule(stack ->
                stack.is(JolCraftTags.Items.ARTISAN_HAMMERS) || stack.is(JolCraftTags.Items.CHISELS)
        ));

        this.addSlot(new JolCraftResultSlot(this.player, container, LapidaryBenchBlockEntity.SLOT_OUTPUT, 128, 32));

        this.addStandardInventorySlots(playerInventory, 8, 68);

        this.data = new SimpleContainerData(DATA_COUNT);
        this.addDataSlots(this.data);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!(player instanceof ServerPlayer sp)) return;

        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof LapidaryBenchBlockEntity be) {
                this.data.set(DATA_RECIPE_VALID, be.isRecipeValid(sp) ? 1 : 0);
            } else {
                this.data.set(DATA_RECIPE_VALID, 0);
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
            }
            else if (index >= 3) {
                if (!this.moveItemStackTo(stackInSlot, LapidaryBenchBlockEntity.SLOT_TOOL, LapidaryBenchBlockEntity.SLOT_TOOL + 1, false)) {
                    if (!this.moveItemStackTo(stackInSlot, LapidaryBenchBlockEntity.SLOT_INPUT, LapidaryBenchBlockEntity.SLOT_INPUT + 1, false)) {
                        if (index < 30) {
                            if (!this.moveItemStackTo(stackInSlot, 30, 39, false)) return ItemStack.EMPTY;
                        } else if (!this.moveItemStackTo(stackInSlot, 3, 30, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            else if (!this.moveItemStackTo(stackInSlot, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();

            if (stackInSlot.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
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
        ItemStack tool = getToolStack();
        if (tool.is(JolCraftTags.Items.ARTISAN_HAMMERS)) return 0;
        if (tool.is(JolCraftTags.Items.CHISELS)) return 1;
        return -1;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, JolCraftBlocks.LAPIDARY_BENCH.get());
    }
}