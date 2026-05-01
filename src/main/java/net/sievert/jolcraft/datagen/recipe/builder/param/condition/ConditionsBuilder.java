package net.sievert.jolcraft.datagen.recipe.builder.param.condition;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.world.recipe.param.condition.Condition;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.datagen.base.builder.JolCraftValidatedBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen-only builder for {@link Conditions}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Null entries are ignored (fail-closed).
 * - No sentinel filtering: condition dispatch is strict and sentinels no longer exist.
 * - Canonicalizes empty => {@link Conditions#EMPTY}.
 * - Order preserved for deterministic output.
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

    public ConditionsBuilder condition(Condition condition) {
        if (condition == null) return this;
        list.add(condition);
        return this;
    }

    public ConditionsBuilder condition(JolCraftValidatedBuilder<? extends Condition> builder) {
        if (builder == null) return this;

        DataResult<? extends Condition> built = builder.buildValidated();
        return condition(built.result().orElse(null));
    }

    public ConditionsBuilder conditions(List<? extends Condition> conditions) {
        if (conditions == null || conditions.isEmpty()) return this;

        for (Condition condition : conditions) {
            condition(condition);
        }
        return this;
    }

    @SafeVarargs
    public final ConditionsBuilder conditions(JolCraftValidatedBuilder<? extends Condition>... builders) {
        if (builders == null) return this;

        for (JolCraftValidatedBuilder<? extends Condition> builder : builders) {
            condition(builder);
        }
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public Conditions build() {
        return list.isEmpty()
                ? Conditions.EMPTY
                : new Conditions(List.copyOf(list));
    }
}