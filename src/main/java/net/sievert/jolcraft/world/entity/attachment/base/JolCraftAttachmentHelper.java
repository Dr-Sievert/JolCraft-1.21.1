package net.sievert.jolcraft.world.entity.attachment.base;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;

public abstract class JolCraftAttachmentHelper<TAttachment> {

    protected abstract @NotNull AttachmentType<TAttachment> type();

    protected final @NotNull TAttachment read(@NotNull Player player) {
        return player.getData(type());
    }

    protected final void write(@NotNull Player player, @NotNull TAttachment value) {
        player.setData(type(), value);
    }

    protected final void clear(@NotNull Player player) {
        player.removeData(type());
    }
}