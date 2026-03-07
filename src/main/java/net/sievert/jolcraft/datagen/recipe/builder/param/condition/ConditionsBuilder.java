package net.sievert.jolcraft.datagen.recipe.builder.param.condition;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionTypes;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.ValidatedBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen-only builder for {@link Conditions}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Null entries are ignored (fail-closed).
 * - {@link ConditionTypes.InvalidCondition} is ignored (avoid silent "always false" gates).
 * - Canonicalizes empty => {@link Conditions#EMPTY}.
 * - Order preserved (even though AND semantics, keep stable output).
 */
public final class ConditionsBuilder implements ParamBuilder<Conditions> {

    private final ArrayList<Condition> list = new ArrayList<>();

    private ConditionsBuilder() {}

    public static ConditionsBuilder create() {
        return new ConditionsBuilder();
    }

    // ---------------------------------------------------------------------
    // ADDERS
    // ---------------------------------------------------------------------

    public ConditionsBuilder condition(Condition c) {
        if (c == null) return this;
        if (c instanceof ConditionTypes.InvalidCondition) return this;
        list.add(c);
        return this;
    }

    public ConditionsBuilder condition(ValidatedBuilder<? extends Condition> builder) {
        if (builder == null) return this;

        DataResult<? extends Condition> built = builder.buildValidated();
        Condition c = built.result().orElse(null);

        return condition(c);
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public Conditions build() {
        if (list.isEmpty()) {
            return Conditions.EMPTY;
        }
        return new Conditions(List.copyOf(list));
    }
}