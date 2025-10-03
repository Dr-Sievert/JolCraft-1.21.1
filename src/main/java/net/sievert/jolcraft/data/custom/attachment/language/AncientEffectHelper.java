package net.sievert.jolcraft.data.custom.attachment.language;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class AncientEffectHelper {
    public static final ResourceLocation SGA_FONT = ResourceLocation.withDefaultNamespace("alt");

    /**
     * Returns a Component: readable if the player has Ancient Memory (effect or permanent), otherwise SGA runes.
     * SERVER/client-proxy version.
     */
    public static Component getAncientText(Player player, Component readable) {
        if (hasAncientMemory(player)) {
            return readable;
        } else {
            return readable.copy().withStyle(style -> style.withFont(SGA_FONT));
        }
    }

    /**
     * Returns a List<Component>: readable if the player has Ancient Memory (effect or permanent),
     * otherwise every line SGA-wrapped. CLIENT-side.
     */
    @OnlyIn(Dist.CLIENT)
    public static List<Component> getAncientText(Player player, List<Component> readableLines) {
        if (hasAncientMemoryClient()) {
            return readableLines;
        } else {
            return readableLines.stream()
                    .map(line -> (Component) line.copy().withStyle(style -> style.withFont(SGA_FONT)))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    /**
     * Checks if player has Ancient Memory effect or permanent knowledge (using helper).
     * This works on both sides (safe through proxy).
     */
    public static boolean hasAncientMemory(Player player) {
        return player != null && (
                player.hasEffect(JolCraftEffects.ANCIENT_MEMORY)
                        || AncientDwarvenLanguageHelper.knowsAncientDwarvish(player)
        );
    }

    /**
     * Same as above, but creative does NOT bypass.
     */
    public static boolean hasAncientMemoryBypassCreative(Player player) {
        return player != null && (
                player.hasEffect(JolCraftEffects.ANCIENT_MEMORY)
                        || AncientDwarvenLanguageHelper.knowsAncientDwarvishBypassCreative(player)
        );
    }

    /**
     * CLIENT-side version: check local effect + client helper.
     * Used for tooltips, GUIs, render, etc.
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean hasAncientMemoryClient() {
        Player player = Minecraft.getInstance().player;
        return hasAncientMemory(player);
    }
}
