package net.sievert.jolcraft.data.util.attachment;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.lang.DwarvenLanguage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundLanguagePacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

/**
 * Helper for Dwarven Language capability, using the JolCraftProxy system for server/client safety.
 */
public class DwarvenLanguageHelper {

    /**
     * Checks if a player knows Dwarvish, or is creative (bypasses language checks).
     * Always uses the JolCraftProxy for correct context.
     */
    public static boolean knowsDwarvish(Player player) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        DwarvenLanguage lang = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.DWARVEN_LANGUAGE.get(), player);
        return lang != null && lang.knowsLanguage();
    }

    /**
     * Checks if a player knows Dwarvish, WITHOUT creative-mode bypass.
     * Uses the JolCraftProxy for context safety.
     */
    public static boolean knowsDwarvishBypassCreative(Player player) {
        if (player == null) return false;
        DwarvenLanguage lang = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.DWARVEN_LANGUAGE.get(), player);
        return lang != null && lang.knowsLanguage();
    }

    /**
     * Sets the "knows Dwarvish" flag for a player.
     * Only ever use this on the SERVER. Also syncs the client view.
     */
    public static void setKnowsDwarvish(Player player, boolean value) {
        if (player == null) return;
        DwarvenLanguage lang = player.getData(JolCraftAttachments.DWARVEN_LANGUAGE.get());
        lang.setKnowsLanguage(value);
        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer,
                    new ClientboundLanguagePacket(value));
        }
    }

    /**
     * CLIENT-ONLY: Checks if the local player knows Dwarvish, or is creative.
     * Always routed through JolCraftProxy for correct side handling.
     * Use in tooltips, GUIs, and client-only renders.
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean knowsDwarvishClient() {
        Player player = Minecraft.getInstance().player;
        return knowsDwarvish(player);
    }
}
