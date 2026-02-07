package net.sievert.jolcraft.world.gui.custom.slot;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.gui.custom.container.DwarfMerchantContainer;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchant;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantOffer;

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

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
     */
    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount = this.removeCount + Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    /**
     * Typically increases an internal count, then calls {@code onCrafting(item)}.
     *
     * @param stack the output.
     */
    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    /**
     * @param stack the output.
     */
    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
        this.removeCount = 0;
    }

    @Override
    public void onTake(Player p_150631_, ItemStack p_150632_) {
        this.checkTakeAchievements(p_150632_);
        DwarfMerchantOffer merchantoffer = this.slots.getActiveOffer();
        if (merchantoffer != null) {
            ItemStack itemstack = this.slots.getItem(0);
            ItemStack itemstack1 = this.slots.getItem(1);
            if (merchantoffer.take(itemstack, itemstack1) || merchantoffer.take(itemstack1, itemstack)) {
                this.merchant.notifyTrade(merchantoffer);
                p_150631_.awardStat(Stats.TRADED_WITH_VILLAGER);
                this.slots.setItem(0, itemstack);
                this.slots.setItem(1, itemstack1);
            }

            this.merchant.overrideXp(this.merchant.getVillagerXp() + merchantoffer.getXp());
        }
    }


}
