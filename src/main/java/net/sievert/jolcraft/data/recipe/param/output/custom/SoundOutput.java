package net.sievert.jolcraft.data.recipe.param.output.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SoundOutput(
        @NotNull Holder<SoundEvent> sound,
        float volume,
        float pitch
) implements OutputParam, SelfValidating<SoundOutput>, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftDictionary.SOUND,
                    JolCraftDictionary.OUTPUT
            ));

    public static final byte DISC = 3;

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<Holder<SoundEvent>> SOUND_CODEC = SoundEvent.CODEC;

    private static final Codec<SoundOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    SOUND_CODEC.fieldOf(JolCraftParameterIds.SOUND)
                            .forGetter(SoundOutput::sound),

                    Codec.FLOAT.optionalFieldOf(JolCraftParameterIds.VOLUME, 1.0F)
                            .forGetter(SoundOutput::volume),

                    Codec.FLOAT.optionalFieldOf(JolCraftParameterIds.PITCH, 1.0F)
                            .forGetter(SoundOutput::pitch)
            ).apply(instance, SoundOutput::new));

    public static final Codec<SoundOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<SoundEvent>> SOUND_STREAM_CODEC =
            SoundEvent.STREAM_CODEC;

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        SOUND_STREAM_CODEC.encode(buf, value.sound);
                        buf.writeFloat(value.volume);
                        buf.writeFloat(value.pitch);
                    },
                    buf -> new SoundOutput(
                            SOUND_STREAM_CODEC.decode(buf),
                            buf.readFloat(),
                            buf.readFloat()
                    )
            );

    // ---------------------------------------------------------------------
    // OUTPUT PARAM
    // ---------------------------------------------------------------------

    public static final ParamTypeDef<OutputParam> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        if (!Float.isFinite(volume) || volume < 0.0F) return List.of();
        if (!Float.isFinite(pitch) || pitch <= 0.0F) return List.of();

        return List.of(
                new Output.Sounds(
                        List.of(new Output.Sound(sound, volume, pitch))
                )
        );
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return List.of();
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<SoundOutput> validate() {

        if (!Float.isFinite(volume) || volume < 0.0F) {
            return SelfValidating.invalid(
                    "'" + JolCraftParameterIds.VOLUME + "' must be finite and >= 0"
            );
        }

        if (!Float.isFinite(pitch) || pitch <= 0.0F) {
            return SelfValidating.invalid(
                    "'" + JolCraftParameterIds.PITCH + "' must be finite and > 0"
            );
        }

        return SelfValidating.ok(this);
    }

    // ---------------------------------------------------------------------
    // CREATION
    // ---------------------------------------------------------------------

    public static @NotNull SoundOutput of(
            @NotNull Holder<SoundEvent> sound,
            float volume,
            float pitch
    ) {
        return new SoundOutput(sound, volume, pitch);
    }

    public static @NotNull SoundOutput of(
            @NotNull Holder<SoundEvent> sound
    ) {
        return of(sound, 1.0F, 1.0F);
    }

    public static @NotNull SoundOutput of(
            @NotNull DeferredHolder<SoundEvent, ? extends SoundEvent> sound,
            float volume,
            float pitch
    ) {
        return new SoundOutput(sound, volume, pitch);
    }

    public static @NotNull SoundOutput of(
            @NotNull DeferredHolder<SoundEvent, ? extends SoundEvent> sound
    ) {
        return of(sound, 1.0F, 1.0F);
    }

    public static @NotNull SoundOutput of(
            @NotNull SoundEvent sound,
            float volume,
            float pitch
    ) {
        return new SoundOutput(Holder.direct(sound), volume, pitch);
    }

    public static @NotNull SoundOutput of(
            @NotNull SoundEvent sound
    ) {
        return of(sound, 1.0F, 1.0F);
    }
}