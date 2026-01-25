package net.sievert.jolcraft.network.proxy.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.AttachmentType;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public interface JolCraftClientAccess {

    <T> T getAttachment(AttachmentType<T> type, Player player);

    boolean isAltDown();

    @Nullable
    Player getLocalPlayer();

    @Nullable
    Component getAltKeyComponent();
}
