package net.sievert.jolcraft.network.proxy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.network.packet.s2c.*;

import javax.annotation.Nullable;

public interface JolCraftClientAccess {

    boolean isAltDown();

    @Nullable Player getLocalPlayer();

    @Nullable Component getAltKeyComponent();

    void apply(ClientboundDwarfMerchantOffersPacket packet);
    void apply(ClientboundDeliriumCursePacket packet);
}
