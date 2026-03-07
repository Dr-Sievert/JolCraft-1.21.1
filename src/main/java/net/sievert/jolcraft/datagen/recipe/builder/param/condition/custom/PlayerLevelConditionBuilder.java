package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.PlayerLevelCondition;
import net.sievert.jolcraft.datagen.recipe.builder.base.ValidatedBuilder;

import java.util.Optional;

/**
 * Datagen-only builder for {@link PlayerLevelCondition}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Assemble-only; no clamping or healing of values.
 * - maxLevel is optional; if unset, it is absent (not forced to 0).
 * - invert(boolean) is always allowed (inherited).
 *
 * Validation:
 * - Delegates to {@link PlayerLevelCondition#validate()} (enforces invariants on min/max).
 */
public final class PlayerLevelConditionBuilder
        extends AbstractConditionBuilder<PlayerLevelConditionBuilder>
        implements ValidatedBuilder<Condition> {

    private Integer minLevel;
    private Integer maxLevel;
    private boolean maxSet;

    private PlayerLevelConditionBuilder() {}

    public static PlayerLevelConditionBuilder create() {
        return new PlayerLevelConditionBuilder();
    }

    // ---------------------------------------------------------------------
    // SETTERS
    // ---------------------------------------------------------------------

    public PlayerLevelConditionBuilder minLevel(int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    /**
     * Sets an explicit maxLevel (present).
     * Use this only when you want an upper bound in the JSON.
     */
    public PlayerLevelConditionBuilder maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        this.maxSet = true;
        return this;
    }

    /**
     * Clears the maxLevel (absent).
     * Useful if a builder instance is reused in a loop.
     */
    public PlayerLevelConditionBuilder noMaxLevel() {
        this.maxLevel = null;
        this.maxSet = false;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public DataResult<Condition> buildValidated() {
        int min = (minLevel != null) ? minLevel : 0;

        Optional<Integer> max =
                (maxSet && maxLevel != null) ? Optional.of(maxLevel) : Optional.empty();

        PlayerLevelCondition built = new PlayerLevelCondition(min, max, invert());
        return built.validate().map(v -> v);
    }
}