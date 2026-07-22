package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class FermentingCauldronRecipeBuilder
        implements JolCraftEmissionBuilder<RecipeOutput> {

    private static final String LAST_INGREDIENT_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LAST,
                    JolCraftDictionary.INGREDIENT
            );

    private @Nullable String id;

    private @Nullable ItemInput ingredient;
    private Optional<ItemInput> lastIngredient =
            Optional.empty();

    private Optional<ItemOutput> extract =
            Optional.empty();

    private Optional<EffectOutput> effect =
            Optional.empty();

    private int brewTicks =
            FermentingCauldronRecipe.DEFAULT_BREW_TICKS;

    private int bubbleTicks =
            FermentingCauldronRecipe.DEFAULT_BUBBLE_TICKS;

    private int brewColor =
            FermentingCauldronRecipe.DEFAULT_BREW_COLOR;

    private boolean finalizeBrew =
            FermentingCauldronRecipe.DEFAULT_FINALIZE_BREW;

    private FermentingCauldronRecipeBuilder() {}

    public static FermentingCauldronRecipeBuilder create() {
        return new FermentingCauldronRecipeBuilder();
    }

    public FermentingCauldronRecipeBuilder id(
            @NotNull String id
    ) {
        this.id = id;
        return this;
    }

    public FermentingCauldronRecipeBuilder ingredient(
            @NotNull ItemInput ingredient
    ) {
        this.ingredient = ingredient;
        return this;
    }

    public FermentingCauldronRecipeBuilder lastIngredient(
            @NotNull ItemInput lastIngredient
    ) {
        this.lastIngredient =
                Optional.of(lastIngredient);

        return this;
    }

    public FermentingCauldronRecipeBuilder noLastIngredient() {
        this.lastIngredient =
                Optional.empty();

        return this;
    }

    public FermentingCauldronRecipeBuilder extract(
            @NotNull ItemOutput extract
    ) {
        this.extract =
                Optional.of(extract);

        return this;
    }

    public FermentingCauldronRecipeBuilder noExtract() {
        this.extract =
                Optional.empty();

        return this;
    }

    public FermentingCauldronRecipeBuilder effect(
            @NotNull EffectOutput effect
    ) {
        this.effect =
                Optional.of(effect);

        return this;
    }

    public FermentingCauldronRecipeBuilder noEffect() {
        this.effect =
                Optional.empty();

        return this;
    }

    public FermentingCauldronRecipeBuilder brewTicks(
            int brewTicks
    ) {
        this.brewTicks = brewTicks;
        return this;
    }

    public FermentingCauldronRecipeBuilder bubbleTicks(
            int bubbleTicks
    ) {
        this.bubbleTicks = bubbleTicks;
        return this;
    }

    public FermentingCauldronRecipeBuilder brewColor(
            int brewColor
    ) {
        this.brewColor = brewColor;
        return this;
    }

    public FermentingCauldronRecipeBuilder noBrewColor() {
        this.brewColor =
                FermentingCauldronRecipe.DEFAULT_BREW_COLOR;

        return this;
    }

    public FermentingCauldronRecipeBuilder finalizeBrew(
            boolean finalizeBrew
    ) {
        this.finalizeBrew = finalizeBrew;
        return this;
    }

    @Override
    public @NotNull DataResult<JolCraftDataEmission<RecipeOutput>>
    buildValidated() {
        if (id == null || id.isBlank()) {
            return DataResult.error(() ->
                    "fermenting cauldron recipe id is required"
            );
        }

        if (ingredient == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.INGREDIENT
                            + " is required"
            );
        }

        FermentingCauldronRecipe recipe =
                new FermentingCauldronRecipe(
                        ingredient,
                        lastIngredient,
                        extract,
                        effect,
                        brewTicks,
                        bubbleTicks,
                        brewColor,
                        finalizeBrew
                );

        DataResult<FermentingCauldronRecipe> validated =
                FermentingCauldronRecipe.Serializer.validate(
                        recipe
                );

        if (validated.error().isPresent()) {
            String message = validated.error()
                    .map(DataResult.Error::message)
                    .orElse(
                            "invalid fermenting cauldron recipe"
                    );

            return DataResult.error(() -> message);
        }

        String resolvedId = id;

        return DataResult.success(
                new JolCraftDataEmission<>(
                        resolvedId,
                        (recipeOutput, path) ->
                                recipeOutput.accept(
                                        ResourceLocation
                                                .fromNamespaceAndPath(
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