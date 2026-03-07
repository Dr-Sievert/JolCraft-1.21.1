package net.sievert.jolcraft.datagen.recipe.builder.param.input.entity.selector;

import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.selector.EntityIngredient;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.selector.EntitySelector;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen builder for {@link EntitySelector}.
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
 * - Leaves domain validation to {@link EntitySelector#validate()}
 */
public final class EntitySelectorBuilder implements ParamBuilder<EntitySelector> {

    private Conditions conditions;
    private List<EntitySelector.Entry> entries;

    private EntitySelectorBuilder() {}

    public static EntitySelectorBuilder create() {
        return new EntitySelectorBuilder();
    }

    // ---------------------------------------------------------------------
    // TOP-LEVEL CONDITIONS
    // ---------------------------------------------------------------------

    public EntitySelectorBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public EntitySelectorBuilder conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // ENTRIES
    // ---------------------------------------------------------------------

    public EntitySelectorBuilder entries(List<EntitySelector.Entry> entries) {
        this.entries = entries;
        return this;
    }

    public EntitySelectorBuilder entry(EntitySelector.Entry entry) {
        if (entry == null) return this;

        List<EntitySelector.Entry> list = this.entries;
        if (list == null || list.isEmpty()) {
            this.entries = new ArrayList<>(List.of(entry));
            return this;
        }

        ArrayList<EntitySelector.Entry> next = new ArrayList<>(list.size() + 1);
        for (EntitySelector.Entry e : list) if (e != null) next.add(e);
        next.add(entry);
        this.entries = next;
        return this;
    }

    public EntitySelectorBuilder entry(Conditions entryConditions, EntityIngredient ingredient) {
        return entry(new EntitySelector.Entry(entryConditions, ingredient));
    }

    public EntitySelectorBuilder entry(ConditionsBuilder entryConditions, EntityIngredient ingredient) {
        return entry(entryConditions != null ? entryConditions.build() : null, ingredient);
    }

    public EntitySelectorBuilder entry(EntityIngredient ingredient) {
        return entry(Conditions.EMPTY, ingredient);
    }

    public EntitySelectorBuilder entry(EntityIngredientBuilder builder) {
        return entry(builder != null ? builder.build() : null);
    }

    public EntitySelectorBuilder wrapSingle(EntityIngredient ingredient) {
        this.conditions = Conditions.EMPTY;
        this.entries = List.of(new EntitySelector.Entry(Conditions.EMPTY, ingredient));
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public EntitySelector build() {
        Conditions c = (conditions != null) ? conditions : Conditions.EMPTY;

        List<EntitySelector.Entry> list = this.entries;
        if (list == null || list.isEmpty()) {
            return new EntitySelector(c, List.of());
        }

        ArrayList<EntitySelector.Entry> safe = new ArrayList<>(list.size());
        for (EntitySelector.Entry e : list) if (e != null) safe.add(e);

        return new EntitySelector(c, safe.isEmpty() ? List.of() : safe);
    }
}