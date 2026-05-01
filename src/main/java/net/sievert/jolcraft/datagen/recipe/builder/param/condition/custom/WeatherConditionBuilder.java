package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.world.recipe.param.condition.Condition;
import net.sievert.jolcraft.world.recipe.param.condition.custom.WeatherCondition;
import net.sievert.jolcraft.datagen.base.builder.JolCraftValidatedBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Datagen-only builder for {@link WeatherCondition}.
 *
 * Usage:
 *   WeatherConditionBuilder.create().rain()
 *   WeatherConditionBuilder.create().noRain()
 *   WeatherConditionBuilder.create().thunder()
 *   WeatherConditionBuilder.create().noThunder()
 *   WeatherConditionBuilder.create().clear()
 *   WeatherConditionBuilder.create().noClear()
 *
 * Policy:
 * - No exceptions or logging.
 * - Later calls overwrite earlier ones.
 * - Validation delegated to WeatherCondition.
 */
public final class WeatherConditionBuilder implements JolCraftValidatedBuilder<Condition> {

    private Boolean rain;
    private Boolean thunder;
    private Boolean clear;

    private WeatherConditionBuilder() {}

    public static @NotNull WeatherConditionBuilder create() {
        return new WeatherConditionBuilder();
    }

    // ---------------------------------------------------------------------
    // RAIN
    // ---------------------------------------------------------------------

    public @NotNull WeatherConditionBuilder rain() {
        this.rain = true;
        return this;
    }

    public @NotNull WeatherConditionBuilder noRain() {
        this.rain = false;
        return this;
    }

    // ---------------------------------------------------------------------
    // THUNDER
    // ---------------------------------------------------------------------

    public @NotNull WeatherConditionBuilder thunder() {
        this.thunder = true;
        return this;
    }

    public @NotNull WeatherConditionBuilder noThunder() {
        this.thunder = false;
        return this;
    }

    // ---------------------------------------------------------------------
    // CLEAR
    // ---------------------------------------------------------------------

    /**
     * Requires clear weather.
     */
    public @NotNull WeatherConditionBuilder clear() {
        this.clear = true;
        return this;
    }

    /**
     * Requires weather to not be clear.
     */
    public @NotNull WeatherConditionBuilder noClear() {
        this.clear = false;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<Condition> buildValidated() {
        boolean requireRain = rain != null;
        boolean rainValue = rain != null && rain;

        boolean requireThunder = thunder != null;
        boolean thunderValue = thunder != null && thunder;

        boolean requireClear = clear != null;
        boolean clearValue = clear != null && clear;

        WeatherCondition built = new WeatherCondition(
                requireRain,
                rainValue,
                requireThunder,
                thunderValue,
                requireClear,
                clearValue
        );

        return built.validate();
    }
}