package net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.vanilla.ComponentPreservingShapelessRecipe;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Atomic datagen builder for {@link ComponentPreservingShapelessRecipe}.
 *
 * Policy-free:
 * - Does not enforce unlock requirements.
 * - Does not generate ids.
 * - Does not decide folder conventions beyond vanilla recipe-book folder structure.
 *
 * - No throws (fail-closed): invalid inputs => save() no-op.
 *
 * If no criteria are supplied, the recipe is still written, but no advancement is emitted.
 * Unlock policy is handled by the outer plan layer.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ComponentPreservingShapelessRecipeBuilder {

    private static final DataComponentPatch EMPTY_PATCH = DataComponentPatch.builder().build();

    private final CraftingBookCategory category;
    private final Ingredient base;

    private final ArrayList<Ingredient> ingredients = new ArrayList<>();
    private final ArrayList<DataComponentType<?>> keep = new ArrayList<>();
    private final ArrayList<DataComponentType<?>> remove = new ArrayList<>();
    private final ArrayList<DataComponentType<?>> baseRequire = new ArrayList<>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private String group = "";
    private ItemStack result = ItemStack.EMPTY;
    private boolean removeAll = false;
    private DataComponentPatch set = EMPTY_PATCH;

    private ComponentPreservingShapelessRecipeBuilder(
            CraftingBookCategory category,
            Ingredient base
    ) {
        this.category = category;
        this.base = base;
    }

    public static @NotNull ComponentPreservingShapelessRecipeBuilder create(
            CraftingBookCategory category,
            Ingredient base
    ) {
        return new ComponentPreservingShapelessRecipeBuilder(category, base);
    }

    // ---------------------------------------------------------------------
    // Fluent API
    // ---------------------------------------------------------------------

    public @NotNull ComponentPreservingShapelessRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    /** Output override (count forced to 1). Null/empty clears. */
    public @NotNull ComponentPreservingShapelessRecipeBuilder result(ItemStack stack) {
        if (stack.isEmpty()) {
            this.result = ItemStack.EMPTY;
            return this;
        }

        this.result = stack.copyWithCount(1);
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder ingredient(Ingredient ing) {
        if (!ing.isEmpty()) {
            this.ingredients.add(ing);
        }
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder ingredients(List<Ingredient> list) {
        if (list.isEmpty()) return this;

        for (Ingredient ing : list) {
            if (ing != null && !ing.isEmpty()) {
                this.ingredients.add(ing);
            }
        }
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder keep(DataComponentType<?> type) {
        this.keep.add(type);
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder keepAll(List<DataComponentType<?>> types) {
        if (types.isEmpty()) return this;

        for (DataComponentType<?> t : types) {
            if (t != null) {
                this.keep.add(t);
            }
        }
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder remove(DataComponentType<?> type) {
        this.remove.add(type);
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder removeAll(List<DataComponentType<?>> types) {
        if (types.isEmpty()) return this;

        for (DataComponentType<?> t : types) {
            if (t != null) {
                this.remove.add(t);
            }
        }
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder clearCopiedComponents() {
        this.removeAll = true;
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder removeAll(boolean removeAll) {
        this.removeAll = removeAll;
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder requireBaseHas(DataComponentType<?> type) {
        this.baseRequire.add(type);
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder requireBaseHasAll(List<DataComponentType<?>> types) {
        if (types.isEmpty()) return this;

        for (DataComponentType<?> t : types) {
            if (t != null) {
                this.baseRequire.add(t);
            }
        }
        return this;
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder set(DataComponentPatch set) {
        this.set = set;
        return this;
    }

    /** Backwards-compat convenience alias. */
    public @NotNull ComponentPreservingShapelessRecipeBuilder patch(DataComponentPatch patch) {
        return set(patch);
    }

    public @NotNull ComponentPreservingShapelessRecipeBuilder unlocks(String key, Criterion<?> criterion) {
        if (key.isBlank()) {
            return this;
        }

        this.criteria.put(key, criterion);
        return this;
    }

    // ---------------------------------------------------------------------
    // Save
    // ---------------------------------------------------------------------

    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        if (!isEnabled()) return;

        ComponentPreservingShapelessRecipe recipe = buildRecipe();

        if (ComponentPreservingShapelessRecipe.Serializer.validate(recipe).error().isPresent()) return;

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
                        id.location().withPrefix(
                                JolCraftStrings.slashed(
                                        JolCraftStrings.plural(JolCraftDictionary.RECIPE),
                                        this.category.getSerializedName()
                                ) + "/"
                        )
                )
        );
    }

    // ---------------------------------------------------------------------
    // Internal
    // ---------------------------------------------------------------------

    private boolean isEnabled() {
        return !this.base.isEmpty() && this.result != null && !this.result.isEmpty();
    }

    private @NotNull ComponentPreservingShapelessRecipe buildRecipe() {
        return new ComponentPreservingShapelessRecipe(
                this.group,
                this.category,
                this.base,
                List.copyOf(this.ingredients),
                this.result,
                List.copyOf(this.keep),
                List.copyOf(this.remove),
                List.copyOf(this.baseRequire),
                this.removeAll,
                this.set
        );
    }
}