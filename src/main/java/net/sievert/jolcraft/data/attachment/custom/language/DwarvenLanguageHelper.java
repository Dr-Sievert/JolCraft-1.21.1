package net.sievert.jolcraft.data.attachment.custom.language;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundLanguagePacket;

public final class DwarvenLanguageHelper {

    private DwarvenLanguageHelper() {}

    /**
     * Checks if a player knows Dwarvish, or is creative (bypasses language checks).
     */
    public static boolean knowsDwarvish(Player player) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        return knowsDwarvishBypassCreative(player);
    }

    /**
     * Checks if a player knows Dwarvish, WITHOUT creative-mode bypass.
     */
    public static boolean knowsDwarvishBypassCreative(Player player) {
        if (player == null) return false;
        return player.getData(JolCraftAttachments.DWARVEN_LANGUAGE.get()).hasLanguage();
    }

    /**
     * Sets the "knows Dwarvish" flag for a player.
     * Server-authoritative: writes to the live attachment and syncs the client view.
     */
    public static void setKnowsDwarvish(Player player, boolean value) {
        if (player == null) return;

        DwarvenLanguage lang = player.getData(JolCraftAttachments.DWARVEN_LANGUAGE.get());
        if (!lang.setHasLanguageIfChanged(value)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer, new ClientboundLanguagePacket(value));
        }
    }
}