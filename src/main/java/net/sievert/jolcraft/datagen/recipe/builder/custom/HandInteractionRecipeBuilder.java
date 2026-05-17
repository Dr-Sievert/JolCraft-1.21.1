package net.sievert.jolcraft.datagen.recipe.builder.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.world.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.world.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.datagen.base.output.JolCraftFileNameBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.ItemInputBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("UnusedReturnValue")
public final class HandInteractionRecipeBuilder implements RecipeBuilder {

    private final List<String> errors = new ArrayList<>();

    private @Nullable ItemInput ingredientA;
    private ItemIngredientAction actionA = ItemIngredientAction.CATALYST;

    private @Nullable ItemInput ingredientB;
    private ItemIngredientAction actionB = ItemIngredientAction.CATALYST;

    private @Nullable Outputs output;

    private @Nullable SoundOutput successSound;
    private @Nullable SoundOutput failSound;

    private boolean requireSneaking = false;

    private HandInteractionRecipeBuilder() {}

    public static @NotNull HandInteractionRecipeBuilder create() {
        return new HandInteractionRecipeBuilder();
    }

    public @NotNull HandInteractionRecipeBuilder ingredientA(@Nullable ItemInput in) {
        if (in == null) {
            errors.add("ingredient_a is null");
            this.ingredientA = null;
            return this;
        }

        this.ingredientA = in;
        return this;
    }

    public @NotNull HandInteractionRecipeBuilder ingredientA(@Nullable ItemLike item, int count) {
        if (item == null) {
            errors.add("ingredient_a item is null");
            this.ingredientA = null;
            return this;
        }

        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count(IntRange.fixed(Math.max(1, count)))
                .build();

        return ingredientA(built);
    }

    public @NotNull HandInteractionRecipeBuilder ingredientA(@Nullable ItemLike item) {
        return ingredientA(item, 1);
    }

    public @NotNull HandInteractionRecipeBuilder actionA(@Nullable ItemIngredientAction action) {
        if (action == null) {
            errors.add("action_a is null");
            this.actionA = ItemIngredientAction.CATALYST;
            return this;
        }

        this.actionA = action;
        return this;
    }

    public @NotNull HandInteractionRecipeBuilder ingredientB(@Nullable ItemInput in) {
        if (in == null) {
            errors.add("ingredient_b is null");
            this.ingredientB = null;
            return this;
        }

        this.ingredientB = in;
        return this;
    }

    public @NotNull HandInteractionRecipeBuilder ingredientB(@Nullable ItemLike item, int count) {
        if (item == null) {
            errors.add("ingredient_b item is null");
            this.ingredientB = null;
            return this;
        }

        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count(IntRange.fixed(Math.max(1, count)))
                .build();

        return ingredientB(built);
    }

    public @NotNull HandInteractionRecipeBuilder ingredientB(@Nullable ItemLike item) {
        return ingredientB(item, 1);
    }

    public @NotNull HandInteractionRecipeBuilder actionB(@Nullable ItemIngredientAction action) {
        if (action == null) {
            errors.add("action_b is null");
            this.actionB = ItemIngredientAction.CATALYST;
            return this;
        }

        this.actionB = action;
        return this;
    }

    public @NotNull HandInteractionRecipeBuilder output(@Nullable Outputs out) {
        if (out == null) {
            errors.add("output is null");
            this.output = null;
            return this;
        }

        if (!out.hasAnyEntries()) {
            errors.add("output must contain at least one output entry");
            this.output = this.output == null ? out : this.output.merge(out);
            return this;
        }

        this.output = this.output == null ? out : this.output.merge(out);
        return this;
    }

    public @NotNull HandInteractionRecipeBuilder successSound(@Nullable SoundOutput sound) {
        if (sound == null) {
            errors.add("success_sound is null");
            this.successSound = null;
            return this;
        }

        this.successSound = sound;
        return this;
    }

    public @NotNull HandInteractionRecipeBuilder failSound(@Nullable SoundOutput sound) {
        if (sound == null) {
            errors.add("fail_sound is null");
            this.failSound = null;
            return this;
        }

        this.failSound = sound;
        return this;
    }

    public @NotNull HandInteractionRecipeBuilder requireSneaking(boolean require) {
        this.requireSneaking = require;
        return this;
    }

    public @NotNull DataResult<String> recipeNameValidated() {
        String aTok = tokenFromIngredientFailClosed(
                ingredientA,
                JolCraftStrings.underscored(JolCraftDictionary.INGREDIENT, "a")
        );
        String bTok = tokenFromIngredientFailClosed(
                ingredientB,
                JolCraftStrings.underscored(JolCraftDictionary.INGREDIENT, "b")
        );

        String aAct = actionToken(actionA);
        String bAct = actionToken(actionB);

        JolCraftFileNameBuilder builder = JolCraftFileNameBuilder.create()
                .token(aAct)
                .token(aTok)
                .token(JolCraftDictionary.AND)
                .token(bAct)
                .token(bTok);

        if (requireSneaking) {
            builder.token(JolCraftDictionary.SNEAK);
        }

        DataResult<String> built = builder.build();

        if (errors.isEmpty()) {
            return built;
        }

        String partial = built.result().orElse("");
        String msg = "recipeName: " + String.join("; ", errors)
                + (built.error().isPresent() ? "; " + built.error().get().message() : "");
        return DataResult.error(() -> msg, partial);
    }

    private @NotNull String actionToken(@Nullable ItemIngredientAction action) {
        if (action == null) {
            errors.add("action is null (for naming)");
            return JolCraftDictionary.UNKNOWN;
        }

        String id = action.type().getId();
        if (id == null || id.isBlank()) {
            errors.add("action token is null/blank (for naming)");
            return JolCraftDictionary.UNKNOWN;
        }

        return id;
    }

    private @NotNull String tokenFromIngredientFailClosed(@Nullable ItemInput in, @NotNull String label) {
        if (in == null) {
            errors.add(label + " is missing");
            return JolCraftDictionary.UNKNOWN;
        }

        Optional<Holder<Item>> concrete = in.singleConcrete(Registries.ITEM);
        if (concrete.isPresent()) {
            ResourceLocation id = concrete.get()
                    .unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);

            if (id == null) {
                errors.add(label + " has no registry key (for naming)");
                return JolCraftDictionary.UNKNOWN;
            }

            return id.getPath();
        }

        Optional<TagKey<Item>> tag = in.singleTag(Registries.ITEM);
        if (tag.isPresent()) {
            return tag.get().location().getPath();
        }

        errors.add(label + " must be a specific item or single tag (for naming)");
        return JolCraftDictionary.UNKNOWN;
    }

    @Override
    public @NotNull DataResult<JolCraftDataEmission<RecipeOutput>> buildValidated() {
        if (ingredientA == null) {
            errors.add("ingredient_a is required");
        }

        if (ingredientB == null) {
            errors.add("ingredient_b is required");
        }

        if (output == null) {
            errors.add("output is required");
        }

        if (successSound == null) {
            errors.add("success_sound is required");
        }

        if (failSound == null) {
            errors.add("fail_sound is required");
        }

        DataResult<String> nameResult = recipeNameValidated();

        if (!errors.isEmpty()) {
            return nameResult.flatMap(name ->
                    DataResult.error(() -> "builder: " + String.join("; ", errors))
            );
        }

        if (ingredientA == null
                || ingredientB == null
                || output == null
                || successSound == null
                || failSound == null) {
            return nameResult.flatMap(name ->
                    DataResult.error(() -> "builder: missing required fields")
            );
        }

        HandInteractionRecipe recipe = new HandInteractionRecipe(
                ingredientA,
                actionA,
                ingredientB,
                actionB,
                output,
                successSound,
                failSound,
                requireSneaking
        );

        return nameResult.flatMap(name ->
                HandInteractionRecipe.validateRecipe(recipe).map(validRecipe ->
                        new JolCraftDataEmission<>(
                                name,
                                (outAccept, path) -> outAccept.accept(JolCraft.location(path), validRecipe, null)
                        )
                )
        );
    }
}