package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.base.builder.JolCraftEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.world.recipe.base.input.ItemInputAction;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class HandInteractionRecipeBuilder
        implements JolCraftEmissionBuilder<RecipeOutput> {

    private @Nullable String id;

    private @Nullable ItemInput ingredientA;
    private ItemInputAction actionA =
            ItemInputAction.CATALYST;

    private @Nullable ItemInput ingredientB;
    private ItemInputAction actionB =
            ItemInputAction.CATALYST;

    private final List<
            net.sievert.jolcraft.world.recipe.base.output.RecipeOutput
            > outputs = new ArrayList<>();

    private @Nullable SoundOutput successSound;
    private @Nullable SoundOutput failSound;

    private boolean requireSneaking;

    private HandInteractionRecipeBuilder() {}

    public static HandInteractionRecipeBuilder create() {
        return new HandInteractionRecipeBuilder();
    }

    public HandInteractionRecipeBuilder id(
            @NotNull String id
    ) {
        this.id = id;
        return this;
    }

    public HandInteractionRecipeBuilder ingredientA(
            @NotNull ItemInput ingredientA
    ) {
        this.ingredientA = ingredientA;
        return this;
    }

    public HandInteractionRecipeBuilder actionA(
            @NotNull ItemInputAction actionA
    ) {
        this.actionA = actionA;
        return this;
    }

    public HandInteractionRecipeBuilder ingredientB(
            @NotNull ItemInput ingredientB
    ) {
        this.ingredientB = ingredientB;
        return this;
    }

    public HandInteractionRecipeBuilder actionB(
            @NotNull ItemInputAction actionB
    ) {
        this.actionB = actionB;
        return this;
    }

    public HandInteractionRecipeBuilder output(
            @NotNull net.sievert.jolcraft.world.recipe.base.output.RecipeOutput output
    ) {
        this.outputs.add(output);
        return this;
    }

    public HandInteractionRecipeBuilder outputs(
            @NotNull List<
                    ? extends net.sievert.jolcraft.world.recipe.base.output.RecipeOutput
                    > outputs
    ) {
        this.outputs.addAll(outputs);
        return this;
    }

    public HandInteractionRecipeBuilder successSound(
            @NotNull SoundOutput successSound
    ) {
        this.successSound = successSound;
        return this;
    }

    public HandInteractionRecipeBuilder failSound(
            @NotNull SoundOutput failSound
    ) {
        this.failSound = failSound;
        return this;
    }

    public HandInteractionRecipeBuilder requireSneaking(
            boolean requireSneaking
    ) {
        this.requireSneaking = requireSneaking;
        return this;
    }

    @Override
    public @NotNull DataResult<
            JolCraftDataEmission<RecipeOutput>
            > buildValidated() {
        if (id == null || id.isBlank()) {
            return DataResult.error(() ->
                    "hand interaction recipe id is required"
            );
        }

        if (ingredientA == null) {
            return DataResult.error(() ->
                    HandInteractionRecipe.INGREDIENT_A_KEY
                            + " is required"
            );
        }

        if (actionA == null) {
            return DataResult.error(() ->
                    HandInteractionRecipe.ACTION_A_KEY
                            + " is required"
            );
        }

        if (ingredientB == null) {
            return DataResult.error(() ->
                    HandInteractionRecipe.INGREDIENT_B_KEY
                            + " is required"
            );
        }

        if (actionB == null) {
            return DataResult.error(() ->
                    HandInteractionRecipe.ACTION_B_KEY
                            + " is required"
            );
        }

        if (outputs.isEmpty()) {
            return DataResult.error(() ->
                    HandInteractionRecipe.RESULTS_KEY
                            + " must contain at least one output"
            );
        }

        if (successSound == null) {
            return DataResult.error(() ->
                    HandInteractionRecipe.SUCCESS_SOUND_KEY
                            + " is required"
            );
        }

        if (failSound == null) {
            return DataResult.error(() ->
                    HandInteractionRecipe.FAIL_SOUND_KEY
                            + " is required"
            );
        }

        HandInteractionRecipe recipe =
                new HandInteractionRecipe(
                        ingredientA,
                        actionA,
                        ingredientB,
                        actionB,
                        List.copyOf(outputs),
                        successSound,
                        failSound,
                        requireSneaking
                );

        DataResult<HandInteractionRecipe> validated =
                HandInteractionRecipe.Serializer.validate(
                        recipe
                );

        if (validated.error().isPresent()) {
            String message = validated.error()
                    .map(DataResult.Error::message)
                    .orElse(
                            "invalid hand interaction recipe"
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