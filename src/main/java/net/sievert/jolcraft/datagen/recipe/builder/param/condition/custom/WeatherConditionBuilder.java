package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.WeatherCondition;
import net.sievert.jolcraft.datagen.recipe.builder.base.ValidatedBuilder;

/**
 * Datagen-only builder for {@link WeatherCondition}.
 *
 * Policy:
 * - No throwing, no logging.
 * - rain/thunder are tri-state via presence:
 *   - unset   => not required
 *   - set(true/false) => required and must match that value
 * - Inconsistent calls are allowed and simply overwrite (builder is not single-assignment here).
 * - invert(boolean) is always allowed.
 *
 * Validation:
 * - Delegates to {@link WeatherCondition#validate()}:
 *   requires at least one of rain/thunder to be present (required).
 */
public final class WeatherConditionBuilder implements ValidatedBuilder<Condition> {

    private Boolean rain;
    private Boolean thunder;

    private boolean invert;

    private WeatherConditionBuilder() {}

    public static WeatherConditionBuilder create() {
        return new WeatherConditionBuilder();
    }

    // ---------------------------------------------------------------------
    // SETTERS (presence-based)
    // ---------------------------------------------------------------------

    /** Require raining == value. */
    public WeatherConditionBuilder rain(boolean value) {
        this.rain = value;
        return this;
    }

    /** Clear rain requirement. */
    public WeatherConditionBuilder noRain() {
        this.rain = null;
        return this;
    }

    /** Require thundering == value. */
    public WeatherConditionBuilder thunder(boolean value) {
        this.thunder = value;
        return this;
    }

    /** Clear thunder requirement. */
    public WeatherConditionBuilder noThunder() {
        this.thunder = null;
        return this;
    }

    public WeatherConditionBuilder invert(boolean invert) {
        this.invert = invert;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public DataResult<Condition> buildValidated() {
        boolean requireRaining = rain != null;
        boolean raining = rain != null && rain;

        boolean requireThundering = thunder != null;
        boolean thundering = thunder != null && thunder;

        WeatherCondition built = new WeatherCondition(
                requireRaining,
                raining,
                requireThundering,
                thundering,
                invert
        );

        return built.validate();
    }
}