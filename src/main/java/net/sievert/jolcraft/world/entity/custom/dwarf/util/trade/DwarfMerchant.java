package net.sievert.jolcraft.world.entity.custom.dwarf.util.trade;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;

public interface DwarfMerchant {
    void setTradingPlayer(@Nullable Player tradingPlayer);

    @Nullable
    Player getTradingPlayer();

    DwarfMerchantOffers getOffers();

    void overrideOffers(DwarfMerchantOffers offers);

    void notifyTrade(DwarfMerchantOffer offer);

    /**
     * Notifies the merchant of a possible merchant recipe being fulfilled or not.
     * Usually, this is just a sound byte being played depending on whether the
     * suggested {@link ItemStack} is not empty.
     */
    void notifyTradeUpdated(ItemStack stack);

    int getVillagerXp();

    void overrideXp(int xp);

    boolean showProgressBar();

    boolean showLevel();

    SoundEvent getNotifyTradeSound();

    default boolean canRestock() {
        return false;
    }

    default void openTradingScreen(Player player, Component displayName, int level) {
        this.setTradingPlayer(player);
        OptionalInt optionalInt = player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, accessingPlayer) -> new DwarfMerchantMenu(containerId, inventory, this),
                        displayName
                )
        );

        if (optionalInt.isEmpty()) {
            this.setTradingPlayer(null);
            return;
        }

        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            DwarfMerchantOffers merchantOffers = this.getOffers();
            if (!merchantOffers.isEmpty()) {
                SendDwarfMerchantOffers(
                        serverPlayer,
                        optionalInt.getAsInt(),
                        merchantOffers,
                        level,
                        this.getVillagerXp(),
                        this.showProgressBar(),
                        this.showLevel(),
                        this.canRestock()
                );
            }
        }
    }

    static void SendDwarfMerchantOffers(
            ServerPlayer player,
            int containerId,
            DwarfMerchantOffers offers,
            int dwarfLevel,
            int dwarfXp,
            boolean showProgress,
            boolean showLevel,
            boolean canRestock
    ) {
        ClientboundDwarfMerchantOffersPacket packet = new ClientboundDwarfMerchantOffersPacket(
                containerId,
                offers,
                dwarfLevel,
                dwarfXp,
                showProgress,
                showLevel,
                canRestock
        );
        JolCraftNetworking.sendToClient(player, packet);
    }

    boolean isClientSide();

    boolean stillValid(Player player);

}
