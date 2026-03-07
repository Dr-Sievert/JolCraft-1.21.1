package net.sievert.jolcraft.world.gui.custom.slot;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.gui.custom.container.DwarfMerchantContainer;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchant;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.item.util.coin.CoinPouchHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfMerchantResultSlot extends Slot {
    private final DwarfMerchantContainer slots;
    private final Player player;
    private int removeCount;
    private final DwarfMerchant merchant;

    public DwarfMerchantResultSlot(Player player, DwarfMerchant merchant, DwarfMerchantContainer slots, int slot, int xPosition, int yPosition) {
        super(slots, slot, xPosition, yPosition);
        this.player = player;
        this.merchant = merchant;
        this.slots = slots;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount = this.removeCount + Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
        this.removeCount = 0;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);

        DwarfMerchantOffer offer = this.slots.getActiveOffer();
        if (offer == null) return;

        ItemStack slotA = this.slots.getItem(0);
        ItemStack slotB = this.slots.getItem(1);

        int aCoinsBefore = (slotA.getItem() instanceof CoinPouchItem)
                ? CoinPouchHelper.getCoins(slotA)
                : 0;

        int bCoinsBefore = (slotB.getItem() instanceof CoinPouchItem)
                ? CoinPouchHelper.getCoins(slotB)
                : 0;

        int aCountBefore = slotA.getCount();
        int bCountBefore = slotB.getCount();

        boolean took = offer.take(slotA, slotB) || offer.take(slotB, slotA);
        if (!took) {
            this.merchant.overrideXp(this.merchant.getDwarfXp() + offer.getXp());
            return;
        }

        int coinsSpent = 0;

        // Slot A
        if (aCoinsBefore > 0) {
            coinsSpent += Math.max(0, aCoinsBefore - CoinPouchHelper.getCoins(slotA));
        } else if (slotA.is(JolCraftTags.Items.COINS)) {
            coinsSpent += Math.max(0, aCountBefore - slotA.getCount());
        }

        // Slot B
        if (bCoinsBefore > 0) {
            coinsSpent += Math.max(0, bCoinsBefore - CoinPouchHelper.getCoins(slotB));
        } else if (slotB.is(JolCraftTags.Items.COINS)) {
            coinsSpent += Math.max(0, bCountBefore - slotB.getCount());
        }

        if (coinsSpent > 0 && player instanceof ServerPlayer) {
            player.awardStat(JolCraftStats.COINS_SPENT.get(), coinsSpent);
        }

        this.merchant.notifyTrade(offer);
        this.slots.setItem(0, slotA);
        this.slots.setItem(1, slotB);

        this.merchant.overrideXp(this.merchant.getDwarfXp() + offer.getXp());
    }
}