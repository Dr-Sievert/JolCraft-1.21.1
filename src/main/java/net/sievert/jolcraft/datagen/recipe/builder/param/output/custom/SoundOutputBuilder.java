package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SoundOutputBuilder {

    private @Nullable Holder<SoundEvent> sound;
    private WorldAnchor anchor = WorldAnchor.PLAYER;
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
        this.sound = (sound == null)
                ? null
                : BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound);
        return this;
    }

    public @NotNull SoundOutputBuilder anchor(@Nullable WorldAnchor anchor) {
        if (anchor != null) {
            this.anchor = anchor;
        }
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

        return DataResult.success(new SoundOutput(sound, anchor, volume, pitch));
    }

    public @NotNull SoundOutput build() {
        return buildValidated().getOrThrow(IllegalStateException::new);
    }
}