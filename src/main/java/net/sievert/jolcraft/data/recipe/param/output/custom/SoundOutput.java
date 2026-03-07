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

import java.util.List;

@MethodsReturnNonnullByDefault
public record SoundOutput(
        Holder<SoundEvent> sound,
        WorldAnchor anchor,
        float volume,
        float pitch
) implements OutputParam, SelfValidating<SoundOutput>, RegistryIntrospectable {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.SOUND, JolCraftDictionary.OUTPUT));

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
                            .forGetter(SoundOutput::sound),

                    ANCHOR_CODEC.optionalFieldOf(JolCraftParameterIds.POSITION, WorldAnchor.PLAYER)
                            .forGetter(SoundOutput::anchor),

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

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<SoundEvent>> SOUND_STREAM =
            ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT);

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        SOUND_STREAM.encode(buf, value.sound);
                        WorldAnchor.STREAM_CODEC.encode(buf, value.anchor);
                        buf.writeFloat(value.volume);
                        buf.writeFloat(value.pitch);
                    },
                    buf -> new SoundOutput(
                            SOUND_STREAM.decode(buf),
                            WorldAnchor.STREAM_CODEC.decode(buf),
                            buf.readFloat(),
                            buf.readFloat()
                    )
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
        return RegistryIntrospection.single(Registries.SOUND_EVENT, sound);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<SoundOutput> validate() {

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

    public static @NotNull SoundOutput of(@NotNull SoundEvent sound, float volume, float pitch) {
        Holder<SoundEvent> holder = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound);
        return new SoundOutput(holder, WorldAnchor.PLAYER, volume, pitch);
    }

    public static @NotNull SoundOutput of(@NotNull SoundEvent sound) {
        return of(sound, 1.0F, 1.0F);
    }
}