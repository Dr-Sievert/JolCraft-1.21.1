package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SoundOutputBuilder {

    private @Nullable Holder<SoundEvent> sound;
    private @Nullable WorldAnchor anchor;
    private float volume = 1.0F;
    private float pitch = 1.0F;

    private SoundOutputBuilder() {}

    public static @NotNull SoundOutputBuilder create() {
        return new SoundOutputBuilder();
    }

    public @NotNull SoundOutputBuilder sound(@Nullable Holder<SoundEvent> sound) {
        this.sound = sound;
        return this;
    }

    public @NotNull SoundOutputBuilder sound(@Nullable SoundEvent sound) {
        this.sound = sound == null ? null : BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound);
        return this;
    }

    public @NotNull SoundOutputBuilder anchor(@Nullable WorldAnchor anchor) {
        this.anchor = anchor;
        return this;
    }

    public @NotNull SoundOutputBuilder volume(float volume) {
        this.volume = volume;
        return this;
    }

    public @NotNull SoundOutputBuilder pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public @NotNull SoundOutput build() {
        if (sound == null) {
            return SoundOutput.EMPTY;
        }

        float safeVolume = (!Float.isFinite(volume) || volume < 0.0F) ? 1.0F : volume;
        float safePitch = (!Float.isFinite(pitch) || pitch <= 0.0F) ? 1.0F : pitch;

        return new SoundOutput(sound, anchor, safeVolume, safePitch);
    }
}