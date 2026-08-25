package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import net.sievert.jolcraft.world.recipe.custom.mortar.MortarRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class MortarRecipeBuilder
        implements JolCraftEmissionBuilder<RecipeOutput> {

    private @Nullable String id;
    private final List<ItemInput> inputs =
            new ArrayList<>();

    private @Nullable ItemOutput result;
    private @Nullable SoundOutput sound;
    private int grindingWork;
    private int toolDamage = 1;

    private MortarRecipeBuilder() {
    }

    public static MortarRecipeBuilder create() {
        return new MortarRecipeBuilder();
    }

    public MortarRecipeBuilder id(
            @NotNull String id
    ) {
        this.id = id;
        return this;
    }

    public MortarRecipeBuilder input(
            @NotNull ItemInput input
    ) {
        this.inputs.add(input);
        return this;
    }

    public MortarRecipeBuilder result(
            @NotNull ItemOutput result
    ) {
        this.result = result;
        return this;
    }

    public MortarRecipeBuilder sound(
            @NotNull SoundOutput sound
    ) {
        this.sound = sound;
        return this;
    }

    public MortarRecipeBuilder grindingWork(
            int grindingWork
    ) {
        this.grindingWork = grindingWork;
        return this;
    }

    public MortarRecipeBuilder toolDamage(
            int toolDamage
    ) {
        this.toolDamage = toolDamage;
        return this;
    }

    @Override
    public @NotNull DataResult<JolCraftDataEmission<RecipeOutput>>
    buildValidated() {
        if (inputs.isEmpty()) {
            return DataResult.error(() ->
                    "at least one input is required"
            );
        }

        if (inputs.size() > 3) {
            return DataResult.error(() ->
                    "no more than three inputs are allowed"
            );
        }

        if (result == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.RESULT
                            + " is required"
            );
        }

        if (sound == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.SOUND
                            + " is required"
            );
        }

        if (grindingWork < 1
                || grindingWork > MortarRecipe.MAX_GRINDING_WORK) {
            return DataResult.error(() ->
                    "grinding work must be between 1 and "
                            + MortarRecipe.MAX_GRINDING_WORK
            );
        }

        if (toolDamage < 0) {
            return DataResult.error(() ->
                    "tool damage cannot be negative"
            );
        }

        if (id == null || id.isBlank()) {
            return DataResult.error(() ->
                    "mortar recipe id is required"
            );
        }

        MortarRecipe recipe =
                new MortarRecipe(
                        List.copyOf(inputs),
                        result,
                        sound,
                        grindingWork,
                        toolDamage
                );

        DataResult<MortarRecipe> validated =
                MortarRecipe.Serializer.validate(
                        recipe
                );

        if (validated.error().isPresent()) {
            String message =
                    validated.error()
                            .map(DataResult.Error::message)
                            .orElse(
                                    "invalid mortar recipe"
                            );

            return DataResult.error(() ->
                    message
            );
        }

        String resolvedId = id;

        return DataResult.success(
                new JolCraftDataEmission<>(
                        resolvedId,
                        (recipeOutput, path) ->
                                recipeOutput.accept(
                                        ResourceLocation.fromNamespaceAndPath(
                                                JolCraft.MOD_ID,
                                                path
                                        ),
                                        recipe,
                                        null
                                )
                )
        );
    }
}
