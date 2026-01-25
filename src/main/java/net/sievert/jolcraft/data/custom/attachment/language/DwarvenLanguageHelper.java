package net.sievert.jolcraft.data.custom.attachment.language;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundLanguagePacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

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
        DwarvenLanguage lang = JolCraftProxy.access().getAttachment(JolCraftAttachments.DWARVEN_LANGUAGE.get(), player);
        return lang != null && lang.knowsLanguage();
    }

    /**
     * Sets the "knows Dwarvish" flag for a player.
     * Server-authoritative: writes to the live attachment and syncs the client view.
     */
    public static void setKnowsDwarvish(Player player, boolean value) {
        if (player == null) return;
        DwarvenLanguage lang = player.getData(JolCraftAttachments.DWARVEN_LANGUAGE.get());
        lang.setKnowsLanguage(value);
        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer, new ClientboundLanguagePacket(value));
        }
    }
}