package net.sievert.jolcraft.util.attachment;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.lang.DwarvenLanguage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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
        DwarvenLanguage lang = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_LANGUAGE.get(), player);
        return lang != null && lang.knowsLanguage();
    }

    /**
     * Checks if a player knows Dwarvish, WITHOUT creative-mode bypass.
     * Uses the JolCraftProxy for context safety.
     */
    public static boolean knowsDwarvishBypassCreative(Player player) {
        if (player == null) return false;
        DwarvenLanguage lang = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_LANGUAGE.get(), player);
        return lang != null && lang.knowsLanguage();
    }

    /**
     * Sets the "knows Dwarvish" flag for a player.
     * Always goes through JolCraftProxy.
     */
    public static void setKnowsDwarvish(Player player, boolean value) {
        if (player == null) return;
        DwarvenLanguage lang = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_LANGUAGE.get(), player);
        if (lang != null) {
            lang.setKnowsLanguage(value);
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
