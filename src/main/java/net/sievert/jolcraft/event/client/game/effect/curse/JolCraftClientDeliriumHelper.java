package net.sievert.jolcraft.event.client.game.effect.curse;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.sievert.jolcraft.network.data.client.ClientDeliriumData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public final class JolCraftClientDeliriumHelper {

    private JolCraftClientDeliriumHelper() {}

    private static final float VOLUME = 0.10F;
    private static final float PITCH = 0.65F;

    public static void onSound(PlaySoundEvent event) {
        if (ClientDeliriumData.isInactive()) return;

        SoundInstance sound = event.getSound();
        if (sound == null || sound.isRelative()) return;

        if (isCave(sound)) return;

        event.setSound(new Scaled(sound));
    }

    private static boolean isCave(SoundInstance sound) {
        ResourceLocation cave = caveLocation();
        return cave != null && cave.equals(sound.getLocation());
    }

    @Nullable
    private static ResourceLocation caveLocation() {
        return SoundEvents.AMBIENT_CAVE.unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);
    }

    // ---------------------------------------------------------
    // Wrapper
    // ---------------------------------------------------------

    private record Scaled(SoundInstance delegate) implements SoundInstance {

        @Override public @NotNull ResourceLocation getLocation() { return delegate.getLocation(); }
        @Override public @Nullable WeighedSoundEvents resolve(@NotNull SoundManager manager) { return delegate.resolve(manager); }
        @Override public @NotNull Sound getSound() { return delegate.getSound(); }
        @Override public @NotNull SoundSource getSource() { return delegate.getSource(); }
        @Override public boolean isLooping() { return delegate.isLooping(); }
        @Override public boolean isRelative() { return delegate.isRelative(); }
        @Override public int getDelay() { return delegate.getDelay(); }

        @Override public float getVolume() { return delegate.getVolume() * VOLUME; }
        @Override public float getPitch() { return delegate.getPitch() * PITCH; }

        @Override public double getX() { return delegate.getX(); }
        @Override public double getY() { return delegate.getY(); }
        @Override public double getZ() { return delegate.getZ(); }
        @Override public @NotNull Attenuation getAttenuation() { return delegate.getAttenuation(); }
    }
}