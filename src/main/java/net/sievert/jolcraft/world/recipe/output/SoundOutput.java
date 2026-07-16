package net.sievert.jolcraft.world.recipe.output;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.sievert.jolcraft.world.recipe.output.hook.JolCraftRecipeHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public record SoundOutput(
        Holder<SoundEvent> sound,
        SoundSource source,
        NumberProvider volume,
        NumberProvider pitch,
        List<ResourceLocation> hooks
) implements RecipeOutput {

    private static final String SOUND_KEY = "sound";
    private static final String SOURCE_KEY = "source";
    private static final String VOLUME_KEY = "volume";
    private static final String PITCH_KEY = "pitch";
    private static final String HOOKS_KEY = "hooks";

    public static final Codec<SoundSource> SOUND_SOURCE_CODEC =
            Codec.STRING.comapFlatMap(
                    name -> Arrays.stream(
                                    SoundSource.values()
                            )
                            .filter(value ->
                                    value.getName().equals(name)
                            )
                            .findFirst()
                            .map(DataResult::success)
                            .orElseGet(() ->
                                    DataResult.error(() ->
                                            "Unknown sound source: "
                                                    + name
                                    )
                            ),
                    SoundSource::getName
            );

    public static final MapCodec<SoundOutput> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            SoundEvent.CODEC
                                    .fieldOf(SOUND_KEY)
                                    .forGetter(SoundOutput::sound),

                            SOUND_SOURCE_CODEC
                                    .optionalFieldOf(
                                            SOURCE_KEY,
                                            SoundSource.BLOCKS
                                    )
                                    .forGetter(SoundOutput::source),

                            NumberProviders.CODEC
                                    .optionalFieldOf(
                                            VOLUME_KEY,
                                            ConstantValue.exactly(1.0F)
                                    )
                                    .forGetter(SoundOutput::volume),

                            NumberProviders.CODEC
                                    .optionalFieldOf(
                                            PITCH_KEY,
                                            ConstantValue.exactly(1.0F)
                                    )
                                    .forGetter(SoundOutput::pitch),

                            ResourceLocation.CODEC
                                    .listOf()
                                    .optionalFieldOf(
                                            HOOKS_KEY,
                                            List.of()
                                    )
                                    .forGetter(SoundOutput::hooks)
                    ).apply(
                            instance,
                            SoundOutput::new
                    )
            );

    public SoundOutput {
        Objects.requireNonNull(
                sound,
                SOUND_KEY
        );

        Objects.requireNonNull(
                source,
                SOURCE_KEY
        );

        Objects.requireNonNull(
                volume,
                VOLUME_KEY
        );

        Objects.requireNonNull(
                pitch,
                PITCH_KEY
        );

        hooks = hooks == null
                ? List.of()
                : List.copyOf(hooks);
    }

    @Override
    public RecipeOutputType getType() {
        return JolCraftRecipeOutputTypes.SOUND.get();
    }

    @Override
    public void validate(
            @NotNull ValidationContext context
    ) {
        RecipeOutput.super.validate(context);

        volume.validate(
                context.forChild("." + VOLUME_KEY)
        );

        pitch.validate(
                context.forChild("." + PITCH_KEY)
        );
    }

    public void generate(
            @NotNull LootContext context,
            @NotNull RecipeInput recipeInput,
            @NotNull Consumer<GeneratedSound> output
    ) {
        GeneratedSound generated =
                new GeneratedSound(
                        sound,
                        source,
                        volume.getFloat(context),
                        pitch.getFloat(context)
                );

        if (applyHooks(
                context,
                generated,
                recipeInput
        )) {
            output.accept(generated);
        }
    }

    public SoundOutput applyHook(
            @NotNull ResourceLocation hook
    ) {
        Objects.requireNonNull(
                hook,
                "hook"
        );

        List<ResourceLocation> updated =
                new ArrayList<>(hooks);

        updated.add(hook);

        return new SoundOutput(
                sound,
                source,
                volume,
                pitch,
                updated
        );
    }

    public SoundOutput applyHooks(
            @NotNull ResourceLocation... hooks
    ) {
        Objects.requireNonNull(
                hooks,
                HOOKS_KEY
        );

        List<ResourceLocation> updated =
                new ArrayList<>(this.hooks);

        for (ResourceLocation hook : hooks) {
            updated.add(
                    Objects.requireNonNull(
                            hook,
                            "hook"
                    )
            );
        }

        return new SoundOutput(
                sound,
                source,
                volume,
                pitch,
                updated
        );
    }

    public static SoundOutput of(
            @NotNull Holder<SoundEvent> sound
    ) {
        return new SoundOutput(
                sound,
                SoundSource.BLOCKS,
                ConstantValue.exactly(1.0F),
                ConstantValue.exactly(1.0F),
                List.of()
        );
    }

    public static SoundOutput of(
            @NotNull Holder<SoundEvent> sound,
            @NotNull SoundSource source,
            @NotNull NumberProvider volume,
            @NotNull NumberProvider pitch
    ) {
        return new SoundOutput(
                sound,
                source,
                volume,
                pitch,
                List.of()
        );
    }

    private boolean applyHooks(
            LootContext context,
            GeneratedSound generated,
            RecipeInput recipeInput
    ) {
        for (ResourceLocation hook : hooks) {
            if (!JolCraftRecipeHooks.apply(
                    hook,
                    context,
                    generated,
                    recipeInput
            )) {
                return false;
            }
        }

        return true;
    }

    public record GeneratedSound(
            Holder<SoundEvent> sound,
            SoundSource source,
            float volume,
            float pitch
    ) {}
}