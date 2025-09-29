package net.sievert.jolcraft.gui.custom.dwarf;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.custom.merchant.CoinPouchItem;
import net.sievert.jolcraft.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.gui.custom.slot.DwarfMerchantResultSlot;
import net.sievert.jolcraft.entity.util.dwarf.trade.*;
import net.sievert.jolcraft.item.util.CoinPouchHelper;

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
        this.addStandardInventorySlots(playerInventory, 108, 84);
    }

    @Override
    public boolean stillValid(Player player) {
        return trader.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        trader.setTradingPlayer(null);

        if (!trader.isClientSide()) {
            for (int i = 0; i < 2; i++) {
                ItemStack stack = tradeContainer.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {
                    if (!player.isAlive() || (player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected())) {
                        player.drop(stack, false);
                    } else {
                        player.getInventory().placeItemBackInInventory(stack);
                    }
                }
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

        if (slot.hasItem()) {
            ItemStack in = slot.getItem();
            result = in.copy();

            if (index == 2) {
                if (!this.moveItemStackTo(in, 3, 39, true)) return ItemStack.EMPTY;
                slot.onQuickCraft(in, result);
                playTradeSound();
            } else if (index != 0 && index != 1) {
                if (!this.moveItemStackTo(in, 3, 39, false)) return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(in, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (in.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (in.getCount() == result.getCount()) return ItemStack.EMPTY;

            slot.onTake(player, in);
        }

        return result;
    }

    private void playTradeSound() {
        if (!trader.isClientSide()) {
            Entity e = (Entity) trader;
            e.level().playLocalSound(e.getX(), e.getY(), e.getZ(), trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
        }
    }

    public void tryMoveItems(int selectedRecipe) {
        if (selectedRecipe < 0 || selectedRecipe >= getOffers().size()) return;

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
                ItemStack stack = slots.get(i).getItem();
                if (!stack.isEmpty() && stack.getItem() instanceof CoinPouchItem
                        && CoinPouchHelper.getCoins(stack) >= cost.count()) {
                    ItemStack pouchCopy = stack.copy();
                    pouchCopy.setCount(1);
                    tradeContainer.setItem(slot, pouchCopy);
                    stack.shrink(1);
                    slots.get(i).setChanged();
                    return;
                }
            }
        }

        // Fallback: normal stack match
        for (int i = 3; i < 39; i++) {
            ItemStack stack = slots.get(i).getItem();
            if (!stack.isEmpty() && cost.test(stack)) {
                ItemStack current = tradeContainer.getItem(slot);
                if (current.isEmpty() || ItemStack.isSameItemSameComponents(stack, current)) {
                    int max = stack.getMaxStackSize();
                    int toMove = Math.min(max - current.getCount(), stack.getCount());
                    ItemStack merged = stack.copyWithCount(current.getCount() + toMove);
                    stack.shrink(toMove);
                    tradeContainer.setItem(slot, merged);
                    slots.get(i).setChanged();
                    if (merged.getCount() >= max) break;
                }
            }
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
        return trader.getVillagerXp();
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

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return false;
    }
}
