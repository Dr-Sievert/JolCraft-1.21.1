package net.sievert.jolcraft.network.proxy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.network.proxy.client.JolCraftClientAccess;

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
}
