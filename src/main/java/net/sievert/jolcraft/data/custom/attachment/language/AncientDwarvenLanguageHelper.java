package net.sievert.jolcraft.data.custom.attachment.language;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundAncientLanguagePacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

/**
 * Helper for Ancient Dwarven Language attachment, using the JolCraftProxy system for server/client safety.
 */
public class AncientDwarvenLanguageHelper {

    /**
     * Checks if a player knows Ancient Dwarvish, or is creative (bypasses language checks).
     * Always uses the JolCraftProxy for correct context.
     */
    public static boolean knowsAncientDwarvish(Player player) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        AncientDwarvenLanguage lang = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get(), player);
        return lang != null && lang.knowsLanguage();
    }

    /**
     * Checks if a player knows Ancient Dwarvish, WITHOUT creative-mode bypass.
     * Uses the JolCraftProxy for context safety.
     */
    public static boolean knowsAncientDwarvishBypassCreative(Player player) {
        if (player == null) return false;
        AncientDwarvenLanguage lang = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get(), player);
        return lang != null && lang.knowsLanguage();
    }

    /**
     * Sets the "knows Ancient Dwarvish" flag for a player.
     * Only call this on the server. Also syncs the client view.
     */
    public static void setKnowsAncientDwarvish(Player player, boolean value) {
        if (player == null) return;
        AncientDwarvenLanguage lang = player.getData(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get());
        lang.setKnowsLanguage(value);
        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer,
                    new ClientboundAncientLanguagePacket(value));
        }
    }
}
