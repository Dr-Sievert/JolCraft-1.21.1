package net.sievert.jolcraft.network.proxy;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

public class JolCraftServerProxy implements JolCraftProxy {
    @Override
    public <T> T getAttachment(AttachmentType<T> type, Player player) {
        return player.getData(type);
    }
}
