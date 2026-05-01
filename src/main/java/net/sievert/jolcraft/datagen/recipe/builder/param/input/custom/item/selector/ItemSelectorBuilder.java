package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.selector;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.selector.ItemIngredient;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen builder for {@link ItemSelector}.
 *
 * Semantics:
 * - Selector-level {@link Conditions} gates the entire selector.
 * - Each entry may have its own {@link Conditions}.
 * - OR across entries after gating.
 *
 * Policy:
 * - Never throws
 * - Ignores nulls
 * - Deterministic build
 * - Leaves strict validation to {@link ItemSelector#validate()}
 */
public final class ItemSelectorBuilder implements ParamBuilder<ItemSelector> {

    private Conditions conditions;
    private List<ItemSelector.Entry> entries;

    private ItemSelectorBuilder() {}

    public static ItemSelectorBuilder create() {
        return new ItemSelectorBuilder();
    }

    // ---------------------------------------------------------------------
    // TOP-LEVEL CONDITIONS
    // ---------------------------------------------------------------------

    public ItemSelectorBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public ItemSelectorBuilder conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // ENTRIES
    // ---------------------------------------------------------------------

    public ItemSelectorBuilder entries(List<ItemSelector.Entry> entries) {
        this.entries = entries;
        return this;
    }

    public ItemSelectorBuilder entry(ItemSelector.Entry entry) {
        if (entry == null) return this;

        List<ItemSelector.Entry> list = this.entries;
        if (list == null || list.isEmpty()) {
            this.entries = new ArrayList<>(List.of(entry));
            return this;
        }

        ArrayList<ItemSelector.Entry> next = new ArrayList<>(list.size() + 1);
        for (ItemSelector.Entry e : list) if (e != null) next.add(e);
        next.add(entry);
        this.entries = next;
        return this;
    }

    public ItemSelectorBuilder entry(Conditions entryConditions, ItemIngredient ingredient) {
        return entry(new ItemSelector.Entry(entryConditions, ingredient));
    }

    public ItemSelectorBuilder entry(ConditionsBuilder entryConditions, ItemIngredient ingredient) {
        return entry(entryConditions != null ? entryConditions.build() : null, ingredient);
    }

    public ItemSelectorBuilder entry(ItemIngredient ingredient) {
        return entry(Conditions.EMPTY, ingredient);
    }

    public ItemSelectorBuilder entry(ItemIngredientBuilder builder) {
        return entry(builder != null ? builder.build() : null);
    }

    public ItemSelectorBuilder item(ItemLike item) {
        if (item == null) return this;
        return entry(ItemIngredient.of(item));
    }

    public ItemSelectorBuilder item(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return this;
        return item(stack.getItem());
    }

    /**
     * Shorthand: selector = single ingredient (encodes as shorthand where possible).
     * This overwrites current selector state.
     */
    public ItemSelectorBuilder wrapSingle(ItemIngredient ingredient) {
        this.conditions = Conditions.EMPTY;
        this.entries = List.of(new ItemSelector.Entry(Conditions.EMPTY, ingredient));
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public ItemSelector build() {
        Conditions c = (conditions != null) ? conditions : Conditions.EMPTY;

        List<ItemSelector.Entry> list = this.entries;
        if (list == null || list.isEmpty()) {
            return new ItemSelector(c, List.of());
        }

        ArrayList<ItemSelector.Entry> safe = new ArrayList<>(list.size());
        for (ItemSelector.Entry e : list) if (e != null) safe.add(e);

        return new ItemSelector(c, safe.isEmpty() ? List.of() : safe);
    }
}