package net.sievert.jolcraft.datagen.recipe.builder.param.quantity;

import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

/**
 * Datagen-only builder for {@link IntRange}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Assemble-only; validation is performed by ParamBuilder.buildValidated() via IntRange.validate().
 * - Defaults to {@link IntRange#ONE} (safe sentinel) if unset.
 *
 * Notes:
 * - IntRange invariants (per IntRange.validate): min/max must be >= 1 and min <= max.
 */
public final class IntRangeBuilder implements ParamBuilder<IntRange> {

    private Integer min;
    private Integer max;

    private IntRangeBuilder() {}

    public static IntRangeBuilder create() {
        return new IntRangeBuilder();
    }

    // ---------------------------------------------------------------------
    // STATIC CONVENIENCE
    // ---------------------------------------------------------------------

    public static IntRange one() {
        return IntRange.ONE;
    }

    public static IntRange fixed(int value) {
        return IntRange.fixed(value);
    }

    public static IntRange between(int min, int max) {
        return new IntRange(min, max);
    }

    // ---------------------------------------------------------------------
    // SETTERS
    // ---------------------------------------------------------------------

    public IntRangeBuilder min(int min) {
        this.min = min;
        return this;
    }

    public IntRangeBuilder max(int max) {
        this.max = max;
        return this;
    }

    /** Convenience: sets both min and max to the same value. */
    public IntRangeBuilder fixedValue(int value) {
        this.min = value;
        this.max = value;
        return this;
    }

    /** Convenience: sets both min and max explicitly. */
    public IntRangeBuilder range(int min, int max) {
        this.min = min;
        this.max = max;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public IntRange build() {
        int mi = (min == null) ? 1 : min;
        int ma = (max == null) ? mi : max;
        return new IntRange(mi, ma);
    }
}