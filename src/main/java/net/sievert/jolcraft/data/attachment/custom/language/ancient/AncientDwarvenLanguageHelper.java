package net.sievert.jolcraft.data.attachment.custom.language.ancient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundAncientDwarvenLanguagePacket;

public final class AncientDwarvenLanguageHelper {

    private AncientDwarvenLanguageHelper() {}

    /**
     * Checks if a player knows Ancient Dwarvish, or is creative (bypasses language checks).
     */
    public static boolean knowsAncientDwarvish(Player player) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        return knowsAncientDwarvishBypassCreative(player);
    }

    /**
     * Checks if a player knows Ancient Dwarvish, WITHOUT creative-mode bypass.
     */
    public static boolean knowsAncientDwarvishBypassCreative(Player player) {
        if (player == null) return false;
        return player.getData(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get()).hasLanguage();
    }

    /**
     * Sets the "knows Ancient Dwarvish" flag for a player.
     * Server-authoritative: writes to the live attachment and syncs the client view.
     */
    public static void setKnowsAncientDwarvish(Player player, boolean value) {
        if (player == null) return;

        AncientDwarvenLanguage lang = player.getData(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get());
        if (!lang.setHasLanguageIfChanged(value)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer, new ClientboundAncientDwarvenLanguagePacket(value));
        }
    }
}