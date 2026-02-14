package net.sievert.jolcraft.world.entity.custom.dwarf.util.trade;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

public class DwarfClientSideMerchant implements DwarfMerchant {
    private final Player source;
    private DwarfMerchantOffers offers = new DwarfMerchantOffers();
    private int xp;

    public DwarfClientSideMerchant(Player source) {
        this.source = source;
    }

    @Override
    public Player getTradingPlayer() {
        return this.source;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
    }

    @Override
    public DwarfMerchantOffers getOffers() {
        return this.offers;
    }

    @Override
    public void overrideOffers(DwarfMerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void notifyTrade(DwarfMerchantOffer offer) {
        offer.increaseUses();
    }

    /**
     * Notifies the merchant of a possible merchant recipe being fulfilled or not. Usually, this is just a sound byte being played depending on whether the suggested {@link ItemStack} is not empty.
     */
    @Override
    public void notifyTradeUpdated(ItemStack stack) {
    }

    @Override
    public boolean isClientSide() {
        return this.source.level().isClientSide;
    }

    @Override
    public boolean stillValid(Player p_383147_) {
        return this.source == p_383147_;
    }

    @Override
    public int getDwarfXp() {
        return this.xp;
    }

    @Override
    public void overrideXp(int xp) {
        this.xp = xp;
    }

    @Override
    public boolean showProgressBar() {
        return true;
    }

    @Override
    public boolean showLevel() {
        return true;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return JolCraftSounds.DWARF_YES.get();
    }
}
