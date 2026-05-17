package net.sievert.jolcraft.world.recipe.param.output.custom;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public record SoundOutput(
        @NotNull ResourceLocation soundId,
        float volume,
        float pitch
) implements OutputParam, SelfValidating<SoundOutput>, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftDictionary.SOUND,
                    JolCraftDictionary.OUTPUT
            ));

    public static final byte DISC = 3;

    private record VerboseRaw(
            @NotNull ResourceLocation id,
            float volume,
            float pitch
    ) {}

    private static final Codec<VerboseRaw> VERBOSE_RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC
                            .fieldOf(JolCraftParameterIds.ID)
                            .forGetter(VerboseRaw::id),

                    Codec.FLOAT
                            .optionalFieldOf(JolCraftParameterIds.VOLUME, 1.0F)
                            .forGetter(VerboseRaw::volume),

                    Codec.FLOAT
                            .optionalFieldOf(JolCraftParameterIds.PITCH, 1.0F)
                            .forGetter(VerboseRaw::pitch)
            ).apply(instance, VerboseRaw::new));

    private static final Codec<ResourceLocation> SHORTHAND_CODEC =
            ResourceLocation.CODEC;

    private static final Codec<Either<ResourceLocation, VerboseRaw>> RAW_CODEC =
            Codec.either(SHORTHAND_CODEC, VERBOSE_RAW_CODEC);

    public static final Codec<SoundOutput> CODEC =
            ParamCodecContract.create(
                    RAW_CODEC,
                    SoundOutput::fromRaw,
                    SoundOutput::toRaw
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        ResourceLocation.STREAM_CODEC.encode(buf, value.soundId());
                        buf.writeFloat(value.volume());
                        buf.writeFloat(value.pitch());
                    },
                    buf -> new SoundOutput(
                            ResourceLocation.STREAM_CODEC.decode(buf),
                            buf.readFloat(),
                            buf.readFloat()
                    )
            );

    public static final ParamTypeDef<OutputParam> TYPE_DEF = new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static @NotNull DataResult<SoundOutput> fromRaw(
            @NotNull Either<ResourceLocation, VerboseRaw> raw
    ) {
        if (raw.left().isPresent()) {
            return DataResult.success(SoundOutput.of(raw.left().orElseThrow()));
        }

        VerboseRaw verbose = raw.right().orElseThrow();

        return DataResult.success(new SoundOutput(
                verbose.id(),
                verbose.volume(),
                verbose.pitch()
        ));
    }

    private static @NotNull Either<ResourceLocation, VerboseRaw> toRaw(@NotNull SoundOutput value) {
        if (value.isDefaultVolumeAndPitch()) {
            return Either.left(value.soundId());
        }

        return Either.right(new VerboseRaw(
                value.soundId(),
                value.volume(),
                value.pitch()
        ));
    }

    private boolean isDefaultVolumeAndPitch() {
        return Float.compare(volume, 1.0F) == 0 && Float.compare(pitch, 1.0F) == 0;
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return resolve(ctx.level().registryAccess())
                .<List<Output>>map(soundRef -> List.of(
                        new Output.Sounds(
                                List.of(new Output.Sound(soundRef, volume, pitch))
                        )
                ))
                .orElseGet(List::of);
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return List.of(
                RegistryIntrospection.empty(Registries.SOUND_EVENT)
        );
    }

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

    public @NotNull Optional<Holder.Reference<SoundEvent>> resolve(@NotNull RegistryAccess access) {
        return access.lookupOrThrow(Registries.SOUND_EVENT)
                .get(ResourceKey.create(Registries.SOUND_EVENT, soundId));
    }

    public @Nullable SoundEvent resolveValue(@NotNull RegistryAccess access) {
        return resolve(access).map(Holder::value).orElse(null);
    }

    public static @NotNull SoundOutput of(
            @NotNull ResourceLocation soundId,
            float volume,
            float pitch
    ) {
        return new SoundOutput(soundId, volume, pitch);
    }

    public static @NotNull SoundOutput of(@NotNull ResourceLocation soundId) {
        return of(soundId, 1.0F, 1.0F);
    }

    public static @NotNull SoundOutput of(
            @NotNull Holder<SoundEvent> sound,
            float volume,
            float pitch
    ) {
        return new SoundOutput(extractSoundId(sound), volume, pitch);
    }

    public static @NotNull SoundOutput of(@NotNull Holder<SoundEvent> sound) {
        return of(sound, 1.0F, 1.0F);
    }

    public static @NotNull SoundOutput of(
            @NotNull DeferredHolder<SoundEvent, ? extends SoundEvent> sound,
            float volume,
            float pitch
    ) {
        return new SoundOutput(sound.getId(), volume, pitch);
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
        return new SoundOutput(sound.getLocation(), volume, pitch);
    }

    public static @NotNull SoundOutput of(@NotNull SoundEvent sound) {
        return of(sound, 1.0F, 1.0F);
    }

    private static @NotNull ResourceLocation extractSoundId(@NotNull Holder<SoundEvent> sound) {
        return sound.unwrapKey()
                .map(ResourceKey::location)
                .orElseGet(() -> sound.value().getLocation());
    }
}