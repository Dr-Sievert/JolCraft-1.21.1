package net.sievert.jolcraft.network.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;

/**
 * JolCraft proxy interface for side-specific features and safe, side-aware read-only attachment access.
 *
 * For all gameplay/world logic that needs to mutate attachments, always use player.getData(...) directly.
 * For *read-only* access (e.g., UI, tooltips, client-cached reputation), use JolCraftProxy.get(level).getAttachment(...).
 * This ensures the correct value is returned for each side (server = real, client = last-synced/cached).
 */
public interface JolCraftProxy {

    JolCraftProxy CLIENT = new JolCraftClientProxy();
    JolCraftProxy SERVER = new JolCraftServerProxy();

    /**
     * Returns the correct proxy for the given level (logical side).
     */
    static JolCraftProxy get(Level level) {
        return level.isClientSide() ? CLIENT : SERVER;
    }

    /**
     * Read-only attachment accessor. Always safe for client/server use.
     */
    <T> T getAttachment(AttachmentType<T> type, Player player);
}
