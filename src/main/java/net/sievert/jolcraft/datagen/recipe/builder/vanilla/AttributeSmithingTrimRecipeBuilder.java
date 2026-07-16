package net.sievert.jolcraft.datagen.recipe.builder.vanilla;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.custom.vanilla.AttributeSmithingTrimRecipe;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Atomic datagen builder for {@link AttributeSmithingTrimRecipe}.
 *
 * Policy-free:
 * - Does not enforce unlock requirements.
 * - Does not generate ids.
 * - Does not decide folder conventions beyond vanilla recipe-book folder structure.
 *
 * If no criteria are supplied, the recipe is still written, but no advancement is emitted.
 * Unlock policy is handled by the outer plan layer.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class AttributeSmithingTrimRecipeBuilder {

    private final RecipeCategory category;
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;

    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private AttributeSmithingTrimRecipeBuilder(
            RecipeCategory category,
            Ingredient template,
            Ingredient base,
            Ingredient addition
    ) {
        this.category = category;
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    public static @NotNull AttributeSmithingTrimRecipeBuilder smithingTrim(
            Ingredient template,
            Ingredient base,
            Ingredient addition,
            RecipeCategory category
    ) {
        return new AttributeSmithingTrimRecipeBuilder(category, template, base, addition);
    }

    public @NotNull AttributeSmithingTrimRecipeBuilder unlocks(String key, Criterion<?> criterion) {
        if (key.isBlank()) {
            return this;
        }
        this.criteria.put(key, criterion);
        return this;
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        AttributeSmithingTrimRecipe recipe = new AttributeSmithingTrimRecipe(
                this.template,
                this.base,
                this.addition
        );

        if (this.criteria.isEmpty()) {
            output.accept(id, recipe, null);
            return;
        }

        Advancement.Builder advancement = output.advancement()
                .addCriterion(
                        JolCraftStrings.underscored(
                                JolCraftDictionary.HAS,
                                JolCraftDictionary.THE,
                                JolCraftDictionary.RECIPE
                        ),
                        RecipeUnlockedTrigger.unlocked(id)
                )
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancement::addCriterion);

        output.accept(
                id,
                recipe,
                advancement.build(
                        id.withPrefix(
                                JolCraftStrings.slashed(
                                        JolCraftStrings.plural(JolCraftDictionary.RECIPE),
                                        this.category.getFolderName()
                                ) + "/"
                        )
                )
        );
    }
}