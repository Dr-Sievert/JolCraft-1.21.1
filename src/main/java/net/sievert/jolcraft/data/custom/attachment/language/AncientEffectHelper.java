package net.sievert.jolcraft.data.custom.attachment.language;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.effect.JolCraftEffects;

public final class AncientEffectHelper {

    private AncientEffectHelper() {}

    public static final ResourceLocation SGA_FONT = ResourceLocation.withDefaultNamespace("alt");

    /**
     * Returns readable text if the player has Ancient Memory (effect or permanent),
     * otherwise applies SGA rune font.
     * Safe on both logical sides.
     */
    public static Component getAncientText(Player player, Component readable) {
        if (player == null) return readable.copy().withStyle(style -> style.withFont(SGA_FONT));
        if (hasAncientMemory(player)) return readable;
        return readable.copy().withStyle(style -> style.withFont(SGA_FONT));
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
        return player.hasEffect(JolCraftEffects.ANCIENT_MEMORY) || AncientDwarvenLanguageHelper.knowsAncientDwarvishBypassCreative(player);
    }
}