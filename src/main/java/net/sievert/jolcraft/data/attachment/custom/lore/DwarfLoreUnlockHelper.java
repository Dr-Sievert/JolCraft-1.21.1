package net.sievert.jolcraft.data.attachment.custom.lore;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundLoreUnlocksPacket;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class DwarfLoreUnlockHelper {

    private DwarfLoreUnlockHelper() {}

    /**
     * Read-only helper.
     * Safe on both logical sides – reads directly from the attachment.
     */
    public static boolean hasUnlock(Player player, DwarfLoreKey key) {
        if (player == null || key == null) return false;
        return player.getData(JolCraftAttachments.DWARF_LORE_UNLOCK.get()).hasUnlock(key);
    }

    /**
     * Returns a snapshot of all unlocked dwarf lore keys for the player.
     * Read-only: the returned set is unmodifiable and reflects the attachment state.
     * Safe on both logical sides.
     */
    public static Set<DwarfLoreKey> getAllUnlocks(Player player) {
        if (player == null) return Set.of();
        return player.getData(JolCraftAttachments.DWARF_LORE_UNLOCK.get()).getUnlocks();
    }


    /**
     * Server-authoritative mutation.
     * Adds a single unlock and syncs a full snapshot to the client if changed.
     */
    public static void addUnlock(Player player, DwarfLoreKey key) {
        if (!(player instanceof ServerPlayer serverPlayer) || key == null) return;

        DwarfLoreUnlock unlock = serverPlayer.getData(JolCraftAttachments.DWARF_LORE_UNLOCK.get());
        if (!unlock.addUnlockIfAbsent(key)) return;

        // Packet transports a snapshot only; attachment remains the single source of truth
        JolCraftNetworking.sendToClient(
                serverPlayer,
                new ClientboundLoreUnlocksPacket(
                        unlock.getUnlocks().stream()
                                .map(k -> k.name().toLowerCase(Locale.ROOT))
                                .collect(Collectors.toUnmodifiableSet())
                )
        );
    }

    /**
     * Server-authoritative snapshot replace.
     * Used for bulk sync (login, reload, migration).
     */
    public static void setUnlocks(Player player, Set<DwarfLoreKey> snapshot) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        DwarfLoreUnlock unlock = serverPlayer.getData(JolCraftAttachments.DWARF_LORE_UNLOCK.get());
        if (!unlock.setUnlocksIfChanged(snapshot)) return;

        JolCraftNetworking.sendToClient(
                serverPlayer,
                new ClientboundLoreUnlocksPacket(
                        unlock.getUnlocks().stream()
                                .map(k -> k.name().toLowerCase(Locale.ROOT))
                                .collect(Collectors.toUnmodifiableSet())
                )
        );
    }
}