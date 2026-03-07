package net.sievert.jolcraft.datagen.recipe.builder.param.input.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement.ItemRequirements;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemIngredient;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.item.requirement.ItemRequirementsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.item.selector.ItemIngredientBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.item.selector.ItemSelectorBuilder;

/**
 * Datagen builder for {@link ItemInput}.
 *
 * Policy:
 * - Never throws
 * - Deterministic build
 * - Leaves strict validation to {@link ItemInput#validate()}
 */
public final class ItemInputBuilder implements ParamBuilder<ItemInput> {

    private Conditions conditions;
    private ItemSelector selector;
    private IntRange count;
    private ItemRequirements requirements;

    private ItemInputBuilder() {}

    public static ItemInputBuilder create() {
        return new ItemInputBuilder();
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

    /**
     * Convenience: wrap a single ingredient into a selector.
     * (shorthand-friendly)
     */
    public ItemInputBuilder selector(ItemIngredient ingredient) {
        this.selector = ingredient != null ? ItemSelector.of(ingredient) : null;
        return this;
    }

    public ItemInputBuilder selector(ItemIngredientBuilder builder) {
        return selector(builder != null ? builder.build() : null);
    }

    /**
     * Convenience: single concrete item selector.
     */
    public ItemInputBuilder item(ItemLike item) {
        this.selector = item != null ? ItemSelector.of(item) : null;
        return this;
    }

    /**
     * Convenience: use stack's item as selector (count ignored here; use {@link #count(IntRange)}).
     */
    public ItemInputBuilder item(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return this;
        return item(stack.getItem());
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
        Conditions c = (conditions != null) ? conditions : Conditions.EMPTY;
        ItemSelector s = (selector != null) ? selector : ItemSelector.EMPTY;
        IntRange n = (count != null) ? count : IntRange.ONE;
        ItemRequirements r = (requirements != null) ? requirements : ItemRequirements.EMPTY;

        return new ItemInput(c, s, n, r);
    }
}