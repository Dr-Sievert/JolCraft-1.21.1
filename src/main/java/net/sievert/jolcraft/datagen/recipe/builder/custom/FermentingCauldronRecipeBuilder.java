package net.sievert.jolcraft.datagen.recipe.builder.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.output.custom.EffectOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmission;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeFileNameBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Datagen-only fluent builder for {@link FermentingCauldronRecipe}.
 *
 * Name examples:
 * - add_wheat_to_water_cauldron
 * - brew_wheat_in_water_cauldron
 * - add_wheat_with_speed_effect_to_yeast_cauldron
 * - brew_wheat_with_regeneration_effect_in_yeast_cauldron
 * - extract_beer_bottle_from_yeast_cauldron
 * - extract_beer_bottle_with_speed_effect_from_yeast_cauldron
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"UnusedReturnValue"})
public final class FermentingCauldronRecipeBuilder implements RecipeBuilder {

    private final List<String> errors = new ArrayList<>();

    private ItemInput ingredient = ItemInput.EMPTY;
    private ItemSelector lastIngredient = ItemSelector.EMPTY;
    private ItemOutput extract = ItemOutput.EMPTY;
    private EffectOutput effect = EffectOutput.EMPTY;

    private int brewTicks = 1;
    private int bubbleTicks = 1;
    private int brewColor = 0;
    private boolean finalizeBrew = false;

    private FermentingCauldronRecipeBuilder() {}

    public static @NotNull FermentingCauldronRecipeBuilder create() {
        return new FermentingCauldronRecipeBuilder();
    }

    public @NotNull FermentingCauldronRecipeBuilder ingredient(@Nullable ItemInput in) {
        if (in == null) {
            errors.add("ingredient is null");
            this.ingredient = ItemInput.EMPTY;
            return this;
        }
        this.ingredient = in;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder ingredient(@Nullable ItemLike item, int count) {
        if (item == null) {
            errors.add("ingredient item is null");
            this.ingredient = ItemInput.EMPTY;
            return this;
        }
        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count(IntRange.fixed(Math.max(1, count)))
                .build();
        return ingredient(built);
    }

    public @NotNull FermentingCauldronRecipeBuilder ingredient(@Nullable ItemLike item) {
        return ingredient(item, 1);
    }

    public @NotNull FermentingCauldronRecipeBuilder lastIngredient(@Nullable ItemSelector sel) {
        if (sel == null) {
            errors.add("lastIngredient is null (treated as EMPTY)");
            this.lastIngredient = ItemSelector.EMPTY;
            return this;
        }
        this.lastIngredient = sel;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder noLastIngredient() {
        this.lastIngredient = ItemSelector.EMPTY;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder extract(@Nullable ItemOutput out) {
        if (out == null) {
            errors.add("extract is null (treated as EMPTY)");
            this.extract = ItemOutput.EMPTY;
            return this;
        }
        this.extract = out;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder extract(@Nullable ItemLike item, int min, int max) {
        if (item == null) {
            errors.add("extract item is null");
            this.extract = ItemOutput.EMPTY;
            return this;
        }

        ItemOutput built = ItemOutputBuilder.create()
                .result(item.asItem(), min, max)
                .build();

        return extract(built);
    }

    public @NotNull FermentingCauldronRecipeBuilder extract(@Nullable ItemLike item, int fixedCount) {
        return extract(item, fixedCount, fixedCount);
    }

    public @NotNull FermentingCauldronRecipeBuilder noExtract() {
        this.extract = ItemOutput.EMPTY;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder effect(@Nullable EffectOutput eff) {
        if (eff == null) {
            errors.add("effect is null (treated as EMPTY)");
            this.effect = EffectOutput.EMPTY;
            return this;
        }
        this.effect = eff;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder noEffect() {
        this.effect = EffectOutput.EMPTY;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder brewTicks(int ticks) {
        if (ticks < 1) {
            errors.add("brewTicks must be >= 1");
            this.brewTicks = 1;
            return this;
        }
        this.brewTicks = ticks;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder bubbleTicks(int ticks) {
        if (ticks < 1) {
            errors.add("bubbleTicks must be >= 1");
            this.bubbleTicks = 1;
            return this;
        }
        this.bubbleTicks = ticks;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder brewColor(int color) {
        this.brewColor = color;
        return this;
    }

    public @NotNull FermentingCauldronRecipeBuilder finalizeBrew(boolean finalize) {
        this.finalizeBrew = finalize;
        return this;
    }

    @Override
    public @NotNull DataResult<RecipeEmission> buildValidated() {

        String ingredientTok = tokenFromIngredientFailClosed(ingredient);

        boolean hasLast = lastIngredient != null && lastIngredient != ItemSelector.EMPTY;
        String lastTok = hasLast ? tokenFromLastIngredientFailClosed(lastIngredient) : null;

        String cauldronTok = hasLast
                ? JolCraftStrings.underscored(lastTok, JolCraftDictionary.CAULDRON)
                : JolCraftStrings.underscored(JolCraftDictionary.WATER, JolCraftDictionary.CAULDRON);

        boolean hasExtract = extract != null && extract != ItemOutput.EMPTY;
        String extractTok = hasExtract ? tokenFromExtractFailClosed(extract) : null;

        boolean hasEffect = effect != null && effect != EffectOutput.EMPTY;
        String effectTok = hasEffect ? tokenFromEffectFailClosed(effect) : null;

        if (hasExtract && !hasLast) {
            errors.add("extract requires lastIngredient (cannot extract from water cauldron)");
        }

        RecipeFileNameBuilder nb = RecipeFileNameBuilder.create();

        if (hasExtract) {
            nb.word(JolCraftDictionary.EXTRACT)
                    .word(extractTok);

            if (hasEffect) {
                nb.word(JolCraftDictionary.WITH)
                        .word(effectTok)
                        .word(JolCraftDictionary.EFFECT);
            }

            nb.word(JolCraftDictionary.FROM)
                    .word(cauldronTok);

        } else if (finalizeBrew) {
            nb.word(JolCraftDictionary.BREW)
                    .word(ingredientTok);

            if (hasEffect) {
                nb.word(JolCraftDictionary.WITH)
                        .word(effectTok)
                        .word(JolCraftDictionary.EFFECT);
            }

            nb.word(JolCraftDictionary.IN)
                    .word(cauldronTok);

        } else {
            nb.word(JolCraftDictionary.ADD)
                    .word(ingredientTok);

            if (hasEffect) {
                nb.word(JolCraftDictionary.WITH)
                        .word(effectTok)
                        .word(JolCraftDictionary.EFFECT);
            }

            nb.word(JolCraftDictionary.TO)
                    .word(cauldronTok);
        }

        DataResult<String> nameBuilt = nb.build();

        if (!errors.isEmpty()) {
            String partial = nameBuilt.result().orElse("");
            String msg = "recipeName: " + String.join("; ", errors) +
                    (nameBuilt.error().isPresent() ? ("; " + nameBuilt.error().get().message()) : "");
            nameBuilt = DataResult.error(() -> msg, partial);
        }

        FermentingCauldronRecipe r = new FermentingCauldronRecipe(
                (ingredient == null) ? ItemInput.EMPTY : ingredient,
                (lastIngredient == null) ? ItemSelector.EMPTY : lastIngredient,
                (extract == null) ? ItemOutput.EMPTY : extract,
                (effect == null) ? EffectOutput.EMPTY : effect,
                Math.max(1, brewTicks),
                Math.max(1, bubbleTicks),
                brewColor,
                finalizeBrew
        );

        DataResult<FermentingCauldronRecipe> validated =
                FermentingCauldronRecipe.validateRecipe(r);

        DataResult<FermentingCauldronRecipe> recipeResult =
                (!errors.isEmpty() && validated.error().isEmpty())
                        ? DataResult.error(() -> "builder: " + String.join("; ", errors), r)
                        : validated;

        return nameBuilt.flatMap(name ->
                recipeResult.flatMap(validRecipe ->
                        RecipeEmission.of(
                                JolCraftRecipeIds.FERMENTING_CAULDRON,
                                name,
                                (RecipeOutput outAccept, ResourceKey<Recipe<?>> id) ->
                                        outAccept.accept(id, validRecipe, null)
                        )
                )
        );
    }

    private static @Nullable String pathOfItem(@Nullable Holder<Item> h) {
        if (h == null) return null;
        ResourceLocation id = h.unwrapKey().map(ResourceKey::location).orElse(null);
        return (id != null) ? id.getPath() : null;
    }

    private static @Nullable String pathOfEffect(@Nullable Holder<MobEffect> h) {
        if (h == null) return null;
        ResourceLocation id = h.unwrapKey().map(ResourceKey::location).orElse(null);
        return (id != null) ? id.getPath() : null;
    }

    private String tokenFromIngredientFailClosed(ItemInput in) {
        if (in == ItemInput.EMPTY) {
            errors.add("ingredient is missing");
            return JolCraftDictionary.UNKNOWN;
        }

        Optional<Holder<Item>> h = in.singleConcrete(Registries.ITEM);
        if (h.isEmpty()) {
            errors.add("ingredient must be a specific item (for naming)");
            return JolCraftDictionary.UNKNOWN;
        }

        String path = pathOfItem(h.get());
        if (path == null) {
            errors.add("ingredient has no registry key (for naming)");
            return JolCraftDictionary.UNKNOWN;
        }

        return path;
    }

    private String tokenFromLastIngredientFailClosed(ItemSelector sel) {
        if (sel == ItemSelector.EMPTY) {
            errors.add("last_ingredient is missing");
            return JolCraftDictionary.UNKNOWN;
        }

        Optional<Holder<Item>> concrete = sel.singleConcrete(Registries.ITEM);
        if (concrete.isPresent()) {
            String path = pathOfItem(concrete.get());
            if (path == null) {
                errors.add("last_ingredient has no registry key (for naming)");
                return JolCraftDictionary.UNKNOWN;
            }
            return path;
        }

        Optional<TagKey<Item>> tag = sel.singleTag(Registries.ITEM);
        if (tag.isPresent()) {
            return pathOfTag(tag.get());
        }

        errors.add("last_ingredient must be exactly one concrete item or single tag for naming");
        return JolCraftDictionary.UNKNOWN;
    }

    private static @Nullable String pathOfTag(@Nullable TagKey<Item> tag) {
        if (tag == null) return null;
        return tag.location().getPath();
    }

    private String tokenFromExtractFailClosed(ItemOutput out) {
        if (out == ItemOutput.EMPTY) {
            errors.add("extract is missing");
            return JolCraftDictionary.UNKNOWN;
        }

        Optional<Holder<Item>> h = out.singleConcrete(Registries.ITEM);
        if (h.isEmpty()) {
            errors.add("extract must be a direct item producer (for naming)");
            return JolCraftDictionary.UNKNOWN;
        }

        String path = pathOfItem(h.get());
        if (path == null) {
            errors.add("extract has no registry key (for naming)");
            return JolCraftDictionary.UNKNOWN;
        }

        return path;
    }

    private String tokenFromEffectFailClosed(EffectOutput eff) {
        if (eff == EffectOutput.EMPTY) {
            errors.add("effect is missing");
            return JolCraftDictionary.EFFECT;
        }

        Holder<MobEffect> h = eff.id();
        if (h == null) {
            errors.add("effect has null id (for naming)");
            return JolCraftDictionary.EFFECT;
        }

        String path = pathOfEffect(h);
        if (path == null) {
            errors.add("effect has no registry key (for naming)");
            return JolCraftDictionary.EFFECT;
        }

        return path.toLowerCase(Locale.ROOT);
    }
}