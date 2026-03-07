package net.sievert.jolcraft.data.recipe.param.output.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@MethodsReturnNonnullByDefault
public record SoundOutput(
        @Nullable Holder<SoundEvent> sound,
        @Nullable WorldAnchor anchor,
        float volume,
        float pitch
) implements OutputParam, SelfValidating<SoundOutput>, RegistryIntrospectable {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.SOUND, JolCraftDictionary.OUTPUT));

    public static final SoundOutput EMPTY =
            new SoundOutput(null, null, 0.0F, 1.0F);

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<Holder<SoundEvent>> SOUND_CODEC =
            RegistryFixedCodec.create(Registries.SOUND_EVENT);

    private static final Codec<WorldAnchor> ANCHOR_CODEC =
            StringRepresentable.fromEnum(WorldAnchor::values);

    private static final Codec<SoundOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    SOUND_CODEC.fieldOf(JolCraftParameterIds.SOUND)
                            .forGetter(v -> v.sound),

                    ANCHOR_CODEC.optionalFieldOf(JolCraftParameterIds.POSITION)
                            .forGetter(v -> Optional.ofNullable(v.anchor)),

                    Codec.FLOAT.optionalFieldOf(JolCraftParameterIds.VOLUME, 1.0F)
                            .forGetter(SoundOutput::volume),

                    Codec.FLOAT.optionalFieldOf(JolCraftParameterIds.PITCH, 1.0F)
                            .forGetter(SoundOutput::pitch)
            ).apply(instance, (s, aOpt, vol, pit) ->
                    new SoundOutput(s, aOpt.orElse(null), vol, pit)
            ));

    public static final Codec<SoundOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<SoundEvent>> SOUND_STREAM =
            ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT);

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        Holder<SoundEvent> s = value.sound;
                        buf.writeBoolean(s != null);
                        if (s != null) {
                            SOUND_STREAM.encode(buf, s);
                        }

                        WorldAnchor.encodeOptional(buf, value.anchor);
                        buf.writeFloat(value.volume);
                        buf.writeFloat(value.pitch);
                    },
                    buf -> {
                        Holder<SoundEvent> s = null;
                        if (buf.readBoolean()) {
                            s = SOUND_STREAM.decode(buf);
                        }

                        WorldAnchor anchor = WorldAnchor.decodeOptional(buf);

                        float volume = buf.readFloat();
                        float pitch = buf.readFloat();

                        return new SoundOutput(s, anchor, volume, pitch);
                    }
            );

    // ---------------------------------------------------------------------
    // OUTPUT PARAM
    // ---------------------------------------------------------------------

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {

        if (sound == null) return List.of();

        if (!Float.isFinite(volume) || volume < 0.0F) return List.of();
        if (!Float.isFinite(pitch) || pitch <= 0.0F) return List.of();

        return List.of(
                new Output.Sounds(
                        List.of(new Output.Sound(sound, anchor, volume, pitch))
                )
        );
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull RegistryIntrospection introspection() {
        Holder<SoundEvent> h = sound;
        return (h != null)
                ? RegistryIntrospection.single(Registries.SOUND_EVENT, h)
                : RegistryIntrospection.mixed(Registries.SOUND_EVENT, 0, false);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<SoundOutput> validate() {
        if (sound == null) {
            return SelfValidating.invalid("Missing required field: '" + JolCraftParameterIds.SOUND + "'");
        }

        if (!Float.isFinite(volume) || volume < 0.0F) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.VOLUME + "' must be finite and >= 0");
        }

        if (!Float.isFinite(pitch) || pitch <= 0.0F) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.PITCH + "' must be finite and > 0");
        }

        return SelfValidating.ok(this);
    }

    // ---------------------------------------------------------------------
    // CREATION
    // ---------------------------------------------------------------------

    public static SoundOutput of(@Nullable SoundEvent sound, float volume, float pitch) {
        if (sound == null) return EMPTY;

        Holder<SoundEvent> holder = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound);

        return new SoundOutput(holder, null, volume, pitch);
    }

    public static SoundOutput of(@Nullable SoundEvent sound) {
        return of(sound, 1.0F, 1.0F);
    }
}