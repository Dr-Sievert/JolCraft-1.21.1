package net.sievert.jolcraft.datagen.recipe.builder.param.input.base;

import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.input.base.InputParam;
import net.sievert.jolcraft.world.recipe.param.input.base.Inputs;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a typed {@link Inputs} OR-group for a specific subject type S.
 *
 * Notes:
 * - Inputs are typed by subject (S), unlike Outputs which are heterogeneous.
 * - This builder is the canonical "input block" builder used by recipes/datagen.
 *
 * Datagen policy:
 * - Never throws
 * - Ignores nulls
 * - Leaves domain validation to Inputs.validate()
 */
public final class InputsBuilder<S> implements ParamBuilder<Inputs<S>> {

    private Conditions conditions;
    private List<Inputs.Entry<S>> entries;

    private InputsBuilder() {}

    public static <S> InputsBuilder<S> create() {
        return new InputsBuilder<>();
    }

    // ---------------------------------------------------------------------
    // TOP-LEVEL CONDITIONS
    // ---------------------------------------------------------------------

    public InputsBuilder<S> conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public InputsBuilder<S> conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // ENTRIES
    // ---------------------------------------------------------------------

    public InputsBuilder<S> entries(List<Inputs.Entry<S>> entries) {
        this.entries = entries;
        return this;
    }

    public InputsBuilder<S> entry(Inputs.Entry<S> entry) {
        if (entry == null) return this;

        List<Inputs.Entry<S>> list = this.entries;
        if (list == null || list.isEmpty()) {
            this.entries = new ArrayList<>(List.of(entry));
            return this;
        }

        ArrayList<Inputs.Entry<S>> next = new ArrayList<>(list.size() + 1);
        for (Inputs.Entry<S> e : list) if (e != null) next.add(e);
        next.add(entry);

        this.entries = next;
        return this;
    }

    public InputsBuilder<S> entry(Conditions entryConditions, InputParam<?, S> param) {
        return entry(new Inputs.Entry<>(entryConditions, param));
    }

    public InputsBuilder<S> entry(ConditionsBuilder entryConditions, InputParam<?, S> param) {
        return entry(entryConditions != null ? entryConditions.build() : null, param);
    }

    public InputsBuilder<S> wrapSingle(InputParam<?, S> leaf) {
        this.conditions = Conditions.EMPTY;
        this.entries = List.of(new Inputs.Entry<>(Conditions.EMPTY, leaf));
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public Inputs<S> build() {
        Conditions top = (conditions != null) ? conditions : Conditions.EMPTY;

        List<Inputs.Entry<S>> list = this.entries;
        if (list == null || list.isEmpty()) {
            return new Inputs<>(top, List.of());
        }

        ArrayList<Inputs.Entry<S>> next = new ArrayList<>(list.size());
        for (Inputs.Entry<S> e : list) if (e != null) next.add(e);

        return new Inputs<>(top, next.isEmpty() ? List.of() : next);
    }
}