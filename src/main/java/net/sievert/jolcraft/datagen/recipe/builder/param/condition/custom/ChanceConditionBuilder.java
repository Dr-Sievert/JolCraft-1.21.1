package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.ChanceCondition;
import net.sievert.jolcraft.datagen.recipe.builder.base.ValidatedBuilder;

/**
 * Datagen-only builder for {@link ChanceCondition}.
 *
 * Policy:
 * - No throwing, no logging.
 * - No clamping or mutation of values (builder is an assembler only).
 * - Validation is delegated to {@link ChanceCondition#validate()}.
 * - invert(boolean) is always allowed (inherited).
 *
 * Invariants (enforced by param validation):
 * - chance must be in [0.0, 1.0]
 * - chance must be finite and not NaN
 */
public final class ChanceConditionBuilder extends AbstractConditionBuilder<ChanceConditionBuilder> implements ValidatedBuilder<Condition> {

    private double chance;

    private ChanceConditionBuilder() {}

    public static ChanceConditionBuilder create() {
        return new ChanceConditionBuilder();
    }

    // ---------------------------------------------------------------------
    // SETTERS
    // ---------------------------------------------------------------------

    public ChanceConditionBuilder chance(double chance) {
        this.chance = chance;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public DataResult<Condition> buildValidated() {
        ChanceCondition built = new ChanceCondition(chance, invert());
        return built.validate();
    }
}