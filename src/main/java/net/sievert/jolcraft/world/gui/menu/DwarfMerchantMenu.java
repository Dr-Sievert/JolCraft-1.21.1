package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfClientSideMerchant;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfItemCost;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchant;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.gui.container.DwarfMerchantContainer;
import net.sievert.jolcraft.world.gui.slot.DwarfMerchantResultSlot;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfMerchantMenu extends AbstractContainerMenu {

    private final DwarfMerchant trader;
    private final DwarfMerchantContainer tradeContainer;

    private int merchantLevel;
    private boolean showProgressBar;
    private boolean showLevel;
    private boolean canRestock;

    public DwarfMerchantMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(containerId, inventory, new DwarfClientSideMerchant(inventory.player));
    }

    public DwarfMerchantMenu(int containerId, Inventory playerInventory, DwarfMerchant trader) {
        super(JolCraftMenuTypes.DWARF_MERCHANT_MENU.get(), containerId);
        this.trader = trader;
        this.tradeContainer = new DwarfMerchantContainer(trader);

        this.addSlot(new Slot(tradeContainer, 0, 136, 37));
        this.addSlot(new Slot(tradeContainer, 1, 162, 37));
        this.addSlot(new DwarfMerchantResultSlot(playerInventory.player, trader, tradeContainer, 2, 220, 37));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 108 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return trader.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.trader.setTradingPlayer(null);

        if (!this.trader.isClientSide()) {
            ItemStack a = this.tradeContainer.removeItemNoUpdate(0);
            ItemStack b = this.tradeContainer.removeItemNoUpdate(1);

            if (!player.isAlive()) {
                if (!a.isEmpty()) player.drop(a, false);
                if (!b.isEmpty()) player.drop(b, false);
            } else {
                player.getInventory().placeItemBackInInventory(a);
                player.getInventory().placeItemBackInInventory(b);
            }
        }
    }

    @Override
    public void slotsChanged(Container container) {
        tradeContainer.updateSellItem();
        super.slotsChanged(container);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack in = slot.getItem();
        result = in.copy();

        if (index == 2) {
            // Result slot -> player inventory
            if (!this.moveItemStackTo(in, 3, 39, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(in, result);
        } else if (index != 0 && index != 1) {
            // Player inventory -> (no special targets; same behavior as before)
            if (!this.moveItemStackTo(in, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Payment slots -> player inventory
            if (!this.moveItemStackTo(in, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (in.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (in.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, in);
        return result;
    }

    public void tryMoveItems(int selectedRecipe) {
        if (selectedRecipe < 0 || selectedRecipe >= getOffers().size()) return;

        // Push current payment stacks back into inventory if possible
        for (int i = 0; i < 2; i++) {
            ItemStack in = tradeContainer.getItem(i);
            if (!in.isEmpty() && this.moveItemStackTo(in, 3, 39, true)) {
                tradeContainer.setItem(i, in);
            }
        }

        if (tradeContainer.getItem(0).isEmpty() && tradeContainer.getItem(1).isEmpty()) {
            DwarfMerchantOffer offer = getOffers().get(selectedRecipe);
            moveFromInventoryToPaymentSlot(0, offer.getItemCostA());
            offer.getItemCostB().ifPresent(cost -> moveFromInventoryToPaymentSlot(1, cost));
        }
    }

    private void moveFromInventoryToPaymentSlot(int slot, DwarfItemCost cost) {
        // Prioritize coin pouch
        if (cost.item().value() == JolCraftItems.GOLD_COIN.get()) {
            for (int i = 3; i < 39; i++) {
                ItemStack stack = this.slots.get(i).getItem();
                if (!stack.isEmpty()
                        && stack.getItem() instanceof CoinPouchItem
                        && stack.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0) >= cost.count()){

                    ItemStack pouchCopy = stack.copy();
                    pouchCopy.setCount(1);

                    tradeContainer.setItem(slot, pouchCopy);
                    stack.shrink(1);

                    this.slots.get(i).setChanged();
                    return;
                }
            }
        }

        // Fallback: normal stack match
        for (int i = 3; i < 39; i++) {
            ItemStack stack = this.slots.get(i).getItem();
            if (stack.isEmpty() || !cost.test(stack)) continue;

            ItemStack current = tradeContainer.getItem(slot);
            if (!current.isEmpty() && !ItemStack.isSameItemSameComponents(stack, current)) continue;

            int max = stack.getMaxStackSize();
            int toMove = Math.min(max - current.getCount(), stack.getCount());
            if (toMove <= 0) break;

            ItemStack merged = stack.copyWithCount(current.getCount() + toMove);
            stack.shrink(toMove);

            tradeContainer.setItem(slot, merged);
            this.slots.get(i).setChanged();

            if (merged.getCount() >= max) break;
        }
    }

    public void setSelectionHint(int index) {
        this.tradeContainer.setSelectionHint(index);
    }

    public void setOffers(DwarfMerchantOffers offers) {
        trader.overrideOffers(offers);
    }

    public DwarfMerchantOffers getOffers() {
        return trader.getOffers();
    }

    public int getTraderXp() {
        return trader.getDwarfXp();
    }

    public int getFutureTraderXp() {
        return tradeContainer.getFutureXp();
    }

    public void setXp(int xp) {
        trader.overrideXp(xp);
    }

    public void setMerchantLevel(int level) {
        this.merchantLevel = level;
    }

    public int getTraderLevel() {
        return this.merchantLevel;
    }

    public void setCanRestock(boolean b) {
        this.canRestock = b;
    }

    public boolean canRestock() {
        return this.canRestock;
    }

    public void setShowProgressBar(boolean show) {
        this.showProgressBar = show;
    }

    public boolean showProgressBar() {
        return this.showProgressBar;
    }

    public void setshowLevel(boolean showLevel) {
        this.showLevel = showLevel;
    }

    public boolean showLevel() {
        return this.showLevel;
    }

    public DwarfMerchant getTrader() {
        return trader;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return false;
    }
}