package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.sounds.SoundEvent;
import net.sievert.jolcraft.world.recipe.param.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SoundOutputBuilder {

    private @Nullable SoundEvent sound;
    private float volume = 1.0F;
    private float pitch = 1.0F;

    private SoundOutputBuilder() {}

    public static @NotNull SoundOutputBuilder create() {
        return new SoundOutputBuilder();
    }

    public @NotNull SoundOutputBuilder sound(@Nullable SoundEvent sound) {
        this.sound = sound;
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

    public @NotNull DataResult<SoundOutput> buildValidated() {
        if (sound == null) {
            return DataResult.error(() -> "sound is required");
        }

        if (!Float.isFinite(volume) || volume < 0.0F) {
            return DataResult.error(() -> "volume must be finite and >= 0");
        }

        if (!Float.isFinite(pitch) || pitch <= 0.0F) {
            return DataResult.error(() -> "pitch must be finite and > 0");
        }

        return SoundOutput.of(sound, volume, pitch).validate();
    }

    public @NotNull SoundOutput build() {
        return buildValidated().getOrThrow(IllegalStateException::new);
    }
}