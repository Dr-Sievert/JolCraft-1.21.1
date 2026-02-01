package net.sievert.jolcraft.world.sound.util;

import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

public final class PlaySound {

    private PlaySound() {}

    public static void curse(Player player) {
        JolCraftSoundHelper.player(player, JolCraftSounds.CURSE.get(), 0.8F, 1.0F);
    }

    public static void curse(Player player, float volume, float pitch) {
        JolCraftSoundHelper.player(player, JolCraftSounds.CURSE.get(), volume, pitch);
    }
}