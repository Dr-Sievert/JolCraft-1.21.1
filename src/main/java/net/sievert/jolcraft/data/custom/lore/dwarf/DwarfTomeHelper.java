package net.sievert.jolcraft.data.custom.lore.dwarf;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.unlock.TomeUnlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundTomeUnlocksPacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import java.util.Set;
import java.util.stream.Collectors;

public class DwarfTomeHelper {

    /**
     * Checks if player is creative OR has the unlock (side-safe).
     */
    public static boolean hasUnlock(Player player, DwarfLoreKey unlockId) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        TomeUnlock<DwarfLoreKey> unlock = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.DWARF_TOME_UNLOCK.get(), player);
        return unlock != null && unlock.hasUnlock(unlockId);
    }

    /**
     * Checks if player has the unlock (bypasses creative, side-safe).
     */
    public static boolean hasUnlockBypassCreative(Player player, DwarfLoreKey unlockId) {
        if (player == null) return false;
        TomeUnlock<DwarfLoreKey> unlock = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.DWARF_TOME_UNLOCK.get(), player);
        return unlock != null && unlock.hasUnlock(unlockId);
    }

    /**
     * Grants an unlock to a player.
     * Only call this on the server. Also syncs the client view.
     */
    public static void grantUnlock(Player player, DwarfLoreKey unlockId) {
        if (player == null) return;
        TomeUnlock<DwarfLoreKey> unlock = player.getData(JolCraftAttachments.DWARF_TOME_UNLOCK.get());
        unlock.addUnlock(unlockId);
        if (player instanceof ServerPlayer serverPlayer) {
            Set<String> unlockKeys = unlock.getUnlocks().stream().map(e -> e.name().toLowerCase()).collect(Collectors.toSet());
            JolCraftNetworking.sendToClient(serverPlayer,
                    new ClientboundTomeUnlocksPacket(unlockKeys));
        }
    }

    /**
     * Gets all unlocks for a player (side-safe).
     */
    public static Set<DwarfLoreKey> getAllUnlocks(Player player) {
        if (player == null) return Set.of();
        TomeUnlock<DwarfLoreKey> unlock = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.DWARF_TOME_UNLOCK.get(), player);
        return unlock != null ? unlock.getUnlocks() : Set.of();
    }

    // --- CLIENT ONLY: For local player convenience ---

    @OnlyIn(Dist.CLIENT)
    public static boolean hasUnlockClient(DwarfLoreKey unlockId) {
        Player player = Minecraft.getInstance().player;
        return hasUnlock(player, unlockId);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasUnlockClientBypassCreative(DwarfLoreKey unlockId) {
        Player player = Minecraft.getInstance().player;
        return hasUnlockBypassCreative(player, unlockId);
    }

    @OnlyIn(Dist.CLIENT)
    public static Set<DwarfLoreKey> getAllUnlocksClient() {
        Player player = Minecraft.getInstance().player;
        return getAllUnlocks(player);
    }
}
