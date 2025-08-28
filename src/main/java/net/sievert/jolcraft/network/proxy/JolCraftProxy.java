package net.sievert.jolcraft.network.proxy;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

/**
 * Generic JolCraft proxy interface for side-safe attachment access.
 */
public interface JolCraftProxy {
    /**
     * Generic, context-safe attachment accessor.
     * Always use this instead of direct attachment access in cross-side logic.
     */
    <T> T getAttachment(AttachmentType<T> type, Player player);
}
