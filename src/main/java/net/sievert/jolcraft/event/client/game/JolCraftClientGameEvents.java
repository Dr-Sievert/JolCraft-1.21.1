package net.sievert.jolcraft.event.client.game;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.network.data.client.ClientDeliriumData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class JolCraftClientGameEvents {

    private JolCraftClientGameEvents() {}

    private static final float DELIRIUM_VOLUME = 0.10F;
    private static final float DELIRIUM_PITCH = 0.65F;

    @SubscribeEvent
    public static void onDeliriumMuffle(PlayLevelSoundEvent event) {
        if (!ClientDeliriumData.isActive()) return;

        var soundHolder = event.getSound();
        if (soundHolder == null) return;

        var soundKeyOpt = soundHolder.unwrapKey();
        if (soundKeyOpt.isEmpty()) return;

        ResourceLocation soundLocation = soundKeyOpt.get().location();
        ResourceLocation caveLocation = caveSoundLocation();
        if (caveLocation != null && caveLocation.equals(soundLocation)) return;

        event.setNewVolume(event.getOriginalVolume() * DELIRIUM_VOLUME);
        event.setNewPitch(event.getOriginalPitch() * DELIRIUM_PITCH);
    }

    @SubscribeEvent
    public static void onDeliriumMuffleEngine(PlaySoundEvent event) {
        if (!ClientDeliriumData.isActive()) return;

        SoundInstance sound = event.getSound();
        if (sound == null) return;

        if (sound.isRelative()) return;

        ResourceLocation caveLocation = caveSoundLocation();
        if (caveLocation != null && caveLocation.equals(sound.getLocation())) return;

        event.setSound(new ScaledSoundInstance(sound, DELIRIUM_VOLUME, DELIRIUM_PITCH));
    }

    @Nullable
    private static ResourceLocation caveSoundLocation() {
        return SoundEvents.AMBIENT_CAVE.unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);
    }

    private static final class ScaledSoundInstance implements SoundInstance {

        private final SoundInstance delegate;
        private final float volumeMult;
        private final float pitchMult;

        private ScaledSoundInstance(SoundInstance delegate, float volumeMult, float pitchMult) {
            this.delegate = delegate;
            this.volumeMult = volumeMult;
            this.pitchMult = pitchMult;
        }

        @Override
        public @NotNull ResourceLocation getLocation() {
            return delegate.getLocation();
        }

        @Override
        public @Nullable WeighedSoundEvents resolve(@NotNull SoundManager manager) {
            return delegate.resolve(manager);
        }

        @Override
        public @NotNull Sound getSound() {
            return delegate.getSound();
        }

        @Override
        public @NotNull SoundSource getSource() {
            return delegate.getSource();
        }

        @Override
        public boolean isLooping() {
            return delegate.isLooping();
        }

        @Override
        public boolean isRelative() {
            return delegate.isRelative();
        }

        @Override
        public int getDelay() {
            return delegate.getDelay();
        }

        @Override
        public float getVolume() {
            return delegate.getVolume() * volumeMult;
        }

        @Override
        public float getPitch() {
            return delegate.getPitch() * pitchMult;
        }

        @Override
        public double getX() {
            return delegate.getX();
        }

        @Override
        public double getY() {
            return delegate.getY();
        }

        @Override
        public double getZ() {
            return delegate.getZ();
        }

        @Override
        public @NotNull Attenuation getAttenuation() {
            return delegate.getAttenuation();
        }
    }
}