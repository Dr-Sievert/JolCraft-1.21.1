package net.sievert.jolcraft.data.custom.attachment.lore;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundLoreUnlocksPacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import java.util.Set;
import java.util.stream.Collectors;

public final class DwarfLoreUnlockHelper {

    private DwarfLoreUnlockHelper() {}

    /**
     * Checks if player is creative OR has the unlock (side-safe).
     */
    public static boolean hasUnlock(Player player, DwarfLoreKey unlockId) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        return hasUnlockBypassCreative(player, unlockId);
    }

    /**
     * Checks if player has the unlock (bypasses creative, side-safe).
     */
    public static boolean hasUnlockBypassCreative(Player player, DwarfLoreKey unlockId) {
        if (player == null) return false;
        LoreUnlock<DwarfLoreKey> unlock = JolCraftProxy.access().getAttachment(JolCraftAttachments.DWARF_LORE_UNLOCK.get(), player);
        return unlock != null && unlock.hasUnlock(unlockId);
    }

    /**
     * Gets all unlocks for a player (side-safe).
     */
    public static Set<DwarfLoreKey> getAllUnlocks(Player player) {
        if (player == null) return Set.of();
        LoreUnlock<DwarfLoreKey> unlock = JolCraftProxy.access().getAttachment(JolCraftAttachments.DWARF_LORE_UNLOCK.get(), player);
        return unlock != null ? unlock.getUnlocks() : Set.of();
    }

    /**
     * Grants an unlock to a player.
     * Only call this on the server. Also syncs the client view.
     */
    public static void grantUnlock(Player player, DwarfLoreKey unlockId) {
        if (player == null) return;
        LoreUnlock<DwarfLoreKey> unlock = player.getData(JolCraftAttachments.DWARF_LORE_UNLOCK.get());
        unlock.addUnlock(unlockId);
        if (player instanceof ServerPlayer serverPlayer) {
            Set<String> unlockKeys = unlock.getUnlocks()
                    .stream()
                    .map(e -> e.name().toLowerCase())
                    .collect(Collectors.toSet());
            JolCraftNetworking.sendToClient(serverPlayer, new ClientboundLoreUnlocksPacket(unlockKeys));
        }
    }
}