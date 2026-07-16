package net.sievert.jolcraft.world.recipe.output;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.function.Supplier;

public final class SoundOutputs {

    private SoundOutputs() {}

    public static SoundOutput sound(
            Holder<SoundEvent> sound
    ) {
        return sound(
                sound,
                SoundSource.BLOCKS
        );
    }

    public static SoundOutput sound(
            SoundEvent sound
    ) {
        return sound(
                holder(sound),
                SoundSource.BLOCKS
        );
    }

    public static SoundOutput sound(
            Supplier<? extends SoundEvent> sound
    ) {
        return sound(sound.get());
    }

    public static SoundOutput sound(
            Holder<SoundEvent> sound,
            SoundSource source
    ) {
        return sound(
                sound,
                source,
                ConstantValue.exactly(1.0F),
                ConstantValue.exactly(1.0F)
        );
    }

    public static SoundOutput sound(
            SoundEvent sound,
            SoundSource source
    ) {
        return sound(
                holder(sound),
                source
        );
    }

    public static SoundOutput sound(
            Supplier<? extends SoundEvent> sound,
            SoundSource source
    ) {
        return sound(
                sound.get(),
                source
        );
    }

    public static SoundOutput sound(
            Holder<SoundEvent> sound,
            SoundSource source,
            NumberProvider volume,
            NumberProvider pitch
    ) {
        return SoundOutput.of(
                sound,
                source,
                volume,
                pitch
        );
    }

    public static SoundOutput sound(
            SoundEvent sound,
            SoundSource source,
            NumberProvider volume,
            NumberProvider pitch
    ) {
        return sound(
                holder(sound),
                source,
                volume,
                pitch
        );
    }

    public static SoundOutput sound(
            Supplier<? extends SoundEvent> sound,
            SoundSource source,
            NumberProvider volume,
            NumberProvider pitch
    ) {
        return sound(
                sound.get(),
                source,
                volume,
                pitch
        );
    }

    private static Holder<SoundEvent> holder(
            SoundEvent sound
    ) {
        return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound);
    }
}