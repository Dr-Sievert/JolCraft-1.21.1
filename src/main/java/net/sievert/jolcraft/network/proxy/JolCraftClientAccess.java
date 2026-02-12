package net.sievert.jolcraft.network.proxy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.network.packet.s2c.*;

import javax.annotation.Nullable;

public interface JolCraftClientAccess {

    @SuppressWarnings("unused")
    <T> T getAttachment(AttachmentType<T> type, Player player);

    boolean isAltDown();

    @Nullable Player getLocalPlayer();

    @Nullable Component getAltKeyComponent();

    void apply(ClientboundDwarfMerchantOffersPacket packet);
    void apply(ClientboundDeliriumCursePacket packet);
    void apply(ClientboundDwarvenLanguagePacket packet);
    void apply(ClientboundAncientDwarvenLanguagePacket packet);
    void apply(ClientboundDwarvenReputationPacket packet);
    void apply(ClientboundDwarvenEndorsementsPacket packet);
    void apply(ClientboundDwarfTomeUnlocksPacket packet);
}
