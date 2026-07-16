package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import net.sievert.jolcraft.world.recipe.input.ItemInput;
import net.sievert.jolcraft.world.recipe.output.ItemOutput;
import net.sievert.jolcraft.world.recipe.output.SoundOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LapidaryBenchRecipeBuilder implements JolCraftEmissionBuilder<RecipeOutput> {

    private @Nullable String id;

    private @Nullable ItemInput input;
    private @Nullable ItemInput tool;
    private @Nullable ItemOutput result;
    private @Nullable SoundOutput sound;

    private NumberProvider xp =
            ConstantValue.exactly(0.0F);

    private NumberProvider toolDamage =
            ConstantValue.exactly(1.0F);

    private LapidaryBenchRecipeBuilder() {}

    public static LapidaryBenchRecipeBuilder create() {
        return new LapidaryBenchRecipeBuilder();
    }

    public LapidaryBenchRecipeBuilder id(
            @NotNull String id
    ) {
        this.id = id;
        return this;
    }

    public LapidaryBenchRecipeBuilder input(
            @NotNull ItemInput input
    ) {
        this.input = input;
        return this;
    }

    public LapidaryBenchRecipeBuilder tool(
            @NotNull ItemInput tool
    ) {
        this.tool = tool;
        return this;
    }

    public LapidaryBenchRecipeBuilder result(
            @NotNull ItemOutput result
    ) {
        this.result = result;
        return this;
    }

    public LapidaryBenchRecipeBuilder sound(
            @NotNull SoundOutput sound
    ) {
        this.sound = sound;
        return this;
    }

    public LapidaryBenchRecipeBuilder xp(
            @NotNull NumberProvider xp
    ) {
        this.xp = xp;
        return this;
    }

    public LapidaryBenchRecipeBuilder toolDamage(
            @NotNull NumberProvider toolDamage
    ) {
        this.toolDamage = toolDamage;
        return this;
    }

    @Override
    public @NotNull DataResult<JolCraftDataEmission<RecipeOutput>>
    buildValidated() {
        if (input == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.INPUT + " is required"
            );
        }

        if (tool == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.TOOL + " is required"
            );
        }

        if (result == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.RESULT + " is required"
            );
        }

        if (sound == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.SOUND + " is required"
            );
        }

        if (id == null || id.isBlank()) {
            return DataResult.error(() ->
                    "lapidary bench recipe id is required"
            );
        }

        LapidaryBenchRecipe recipe =
                new LapidaryBenchRecipe(
                        input,
                        tool,
                        result,
                        sound,
                        xp,
                        toolDamage
                );

        DataResult<LapidaryBenchRecipe> validated =
                LapidaryBenchRecipe.Serializer.validate(recipe);

        if (validated.error().isPresent()) {
            String message = validated.error()
                    .map(DataResult.Error::message)
                    .orElse("invalid lapidary bench recipe");

            return DataResult.error(() -> message);
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