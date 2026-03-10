package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement.ItemRequirements;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemIngredient;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.requirement.ItemRequirementsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.selector.ItemIngredientBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.selector.ItemSelectorBuilder;

public final class ItemInputBuilder implements ParamBuilder<ItemInput> {

    private Conditions conditions;
    private ItemSelector selector;
    private IntRange count;
    private ItemRequirements requirements;

    private ItemInputBuilder() {}

    public static ItemInputBuilder create() {
        return new ItemInputBuilder();
    }

    public static ItemInputBuilder one(ItemLike item) {
        return create().item(item).count(IntRange.ONE);
    }

    public static ItemInputBuilder one(Ingredient ingredient) {
        return create().ingredient(ingredient).count(IntRange.ONE);
    }

    // ---------------------------------------------------------------------
    // CONDITIONS
    // ---------------------------------------------------------------------

    public ItemInputBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public ItemInputBuilder conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // SELECTOR
    // ---------------------------------------------------------------------

    public ItemInputBuilder selector(ItemSelector selector) {
        this.selector = selector;
        return this;
    }

    public ItemInputBuilder selector(ItemSelectorBuilder builder) {
        this.selector = builder != null ? builder.build() : null;
        return this;
    }

    public ItemInputBuilder selector(ItemIngredient ingredient) {
        this.selector = ingredient != null ? ItemSelector.of(ingredient) : null;
        return this;
    }

    public ItemInputBuilder selector(ItemIngredientBuilder builder) {
        return selector(builder != null ? builder.build() : null);
    }

    public ItemInputBuilder item(ItemLike item) {
        this.selector = item != null ? ItemSelector.of(item) : null;
        return this;
    }

    public ItemInputBuilder item(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return this;
        return item(stack.getItem());
    }

    public ItemInputBuilder ingredient(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            this.selector = null;
            return this;
        }

        try {
            this.selector = ItemInput.one(ingredient).selector();
        } catch (IllegalArgumentException ignored) {
            this.selector = null;
        }
        return this;
    }

    // ---------------------------------------------------------------------
    // COUNT
    // ---------------------------------------------------------------------

    public ItemInputBuilder count(IntRange count) {
        this.count = count;
        return this;
    }

    // ---------------------------------------------------------------------
    // REQUIREMENTS
    // ---------------------------------------------------------------------

    public ItemInputBuilder requirements(ItemRequirements requirements) {
        this.requirements = requirements;
        return this;
    }

    public ItemInputBuilder requirements(ItemRequirementsBuilder builder) {
        this.requirements = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public ItemInput build() {
        Conditions c = conditions != null ? conditions : Conditions.EMPTY;
        IntRange n = count != null ? count : IntRange.ONE;
        ItemRequirements r = requirements != null ? requirements : ItemRequirements.EMPTY;

        if (selector == null) {
            throw new IllegalStateException("Missing required field '" + JolCraftParameterIds.SELECTOR + "'");
        }

        return new ItemInput(c, selector, n, r);
    }
}