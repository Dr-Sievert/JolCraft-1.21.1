package net.sievert.jolcraft.network.proxy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.network.packet.S2C.*;

import javax.annotation.Nullable;

public interface JolCraftClientAccess {

    <T> T getAttachment(AttachmentType<T> type, Player player);

    boolean isAltDown();

    @Nullable Player getLocalPlayer();

    @Nullable Component getAltKeyComponent();

    void apply(ClientboundParticlePacket packet);
    void apply(ClientboundPlaySoundPacket packet);
    void apply(ClientboundDwarfMerchantOffersPacket packet);
    void apply(ClientboundDeliriumPacket packet);
    void apply(ClientboundLanguagePacket packet);
    void apply(ClientboundAncientLanguagePacket packet);
    void apply(ClientboundReputationPacket packet);
    void apply(ClientboundEndorsementsPacket packet);
    void apply(ClientboundLoreUnlocksPacket packet);
}
