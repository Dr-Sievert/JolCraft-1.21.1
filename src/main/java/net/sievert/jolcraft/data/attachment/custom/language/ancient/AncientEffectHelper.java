package net.sievert.jolcraft.data.attachment.custom.language.ancient;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.id.font.JolCraftFontIds;
import net.sievert.jolcraft.world.effect.JolCraftEffects;

public final class AncientEffectHelper {

    private AncientEffectHelper() {}

    /**
     * Returns readable text if the player has Ancient Memory (effect or permanent),
     * otherwise applies SGA rune font.
     * Safe on both logical sides.
     */
    public static Component getAncientText(Player player, Component readable) {
        if (hasAncientMemory(player)) return readable;
        return readable.copy().withStyle(style -> style.withFont(JolCraftFontIds.SGA));
    }

    /**
     * Checks if the player has Ancient Memory effect or permanent knowledge.
     * Creative mode DOES bypass.
     */
    public static boolean hasAncientMemory(Player player) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        return hasAncientMemoryBypassCreative(player);
    }

    /**
     * Same as above, but creative mode does NOT bypass.
     */
    public static boolean hasAncientMemoryBypassCreative(Player player) {
        if (player == null) return false;
        if (player.hasEffect(JolCraftEffects.ANCIENT_MEMORY)) return true;
        return player.getData(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get()).hasLanguage();
    }
}
