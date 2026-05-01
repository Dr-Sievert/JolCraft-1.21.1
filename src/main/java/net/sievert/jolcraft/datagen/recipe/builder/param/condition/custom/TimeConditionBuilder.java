package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.world.recipe.param.condition.Condition;
import net.sievert.jolcraft.world.recipe.param.condition.custom.TimeCondition;
import net.sievert.jolcraft.datagen.base.builder.JolCraftValidatedBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Datagen-only builder for {@link TimeCondition}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Single-assignment mode selection: first mode call wins.
 * - Modes:
 *   - range(min,max)
 *   - day()
 *   - night()
 * - invert(boolean) is always allowed (inherited).
 *
 * Validation:
 * - Delegates to {@link TimeCondition#validate()}.
 */
public final class TimeConditionBuilder extends AbstractConditionBuilder<TimeConditionBuilder> implements JolCraftValidatedBuilder<Condition> {

    private enum Kind { RANGE, DAY, NIGHT }

    private Kind kind;

    private Integer min;
    private Integer max;

    private TimeConditionBuilder() {}

    public static TimeConditionBuilder create() {
        return new TimeConditionBuilder();
    }

    // ---------------------------------------------------------------------
    // MODE SELECTION
    // ---------------------------------------------------------------------

    public TimeConditionBuilder range(int min, int max) {
        if (this.kind != null) return this;
        this.kind = Kind.RANGE;
        this.min = min;
        this.max = max;
        return this;
    }

    public TimeConditionBuilder day() {
        if (this.kind != null) return this;
        this.kind = Kind.DAY;
        return this;
    }

    public TimeConditionBuilder night() {
        if (this.kind != null) return this;
        this.kind = Kind.NIGHT;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<Condition> buildValidated() {
        TimeCondition built;

        if (kind == Kind.DAY) {
            built = new TimeCondition(
                    TimeCondition.Mode.DAY,
                    0,
                    0,
                    invert()
            );
        } else if (kind == Kind.NIGHT) {
            built = new TimeCondition(
                    TimeCondition.Mode.NIGHT,
                    0,
                    0,
                    invert()
            );
        } else {
            int a = (min != null) ? min : -1;
            int b = (max != null) ? max : -1;

            built = new TimeCondition(
                    TimeCondition.Mode.RANGE,
                    a,
                    b,
                    invert()
            );
        }

        return built.validate().map(v -> v);
    }
}