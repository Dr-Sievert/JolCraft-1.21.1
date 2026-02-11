package net.sievert.jolcraft.network.proxy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.network.packet.s2c.*;

import javax.annotation.Nullable;

public final class JolCraftServerProxy implements JolCraftClientAccess {

    @Override
    public <T> T getAttachment(AttachmentType<T> type, Player player) {
        return player.getData(type);
    }

    @Override
    public boolean isAltDown() {
        return false;
    }

    @Override
    public @Nullable Player getLocalPlayer() {
        return null;
    }

    @Override
    public @Nullable Component getAltKeyComponent() {
        return null;
    }

    @Override
    public void apply(ClientboundDwarfMerchantOffersPacket packet) {}

    @Override
    public void apply(ClientboundDeliriumCursePacket packet) {}

    @Override
    public void apply(ClientboundDwarvenLanguagePacket packet) {}

    @Override
    public void apply(ClientboundAncientDwarvenLanguagePacket packet) {}

    @Override
    public void apply(ClientboundReputationPacket packet) {}

    @Override
    public void apply(ClientboundEndorsementsPacket packet) {}

    @Override
    public void apply(ClientboundLoreUnlocksPacket packet) {}
}
