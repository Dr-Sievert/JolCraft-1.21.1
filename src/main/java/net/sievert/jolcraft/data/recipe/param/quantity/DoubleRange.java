package net.sievert.jolcraft.data.recipe.param.quantity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;

/**
 * Stable, data-driven double range.
 *
 * Schema:
 * - either a double (fixed)
 * - or an object { "min": X, "max": Y }
 *
 * Invariants:
 * - finite values
 * - min <= max
 */
public record DoubleRange(double min, double max) implements SelfValidating<DoubleRange> {

    public static final DoubleRange ZERO = fixed(0.0);
    public static final DoubleRange ONE = fixed(1.0);

    // ---------------------------------------------------------------------
    // FACTORY
    // ---------------------------------------------------------------------

    public static DoubleRange fixed(double value) {
        return new DoubleRange(value, value);
    }

    public boolean isFixed() {
        return min == max;
    }

    public double roll(RandomSource random) {
        double min = this.min;
        double max = this.max;

        if (max < min) {
            double t = min;
            min = max;
            max = t;
        }

        if (min == max) {
            return min;
        }

        return min + random.nextDouble() * (max - min);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    public static DataResult<DoubleRange> validateRange(DoubleRange value) {
        if (value == null) {
            return DataResult.error(() -> "DoubleRange is null");
        }

        if (!Double.isFinite(value.min) || !Double.isFinite(value.max)) {
            return DataResult.error(() -> "DoubleRange values must be finite");
        }

        if (value.min > value.max) {
            return DataResult.error(() ->
                    "DoubleRange min must be <= max (min=" + value.min + ", max=" + value.max + ")");
        }

        return DataResult.success(value);
    }

    @Override
    public DataResult<DoubleRange> validate() {
        return validateRange(this);
    }

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final MapCodec<DoubleRange> OBJECT_CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.DOUBLE.fieldOf(JolCraftParameterIds.MIN).forGetter(DoubleRange::min),
                    Codec.DOUBLE.fieldOf(JolCraftParameterIds.MAX).forGetter(DoubleRange::max)
            ).apply(inst, DoubleRange::new));

    private static final Codec<DoubleRange> RAW_CODEC =
            Codec.either(Codec.DOUBLE, OBJECT_CODEC.codec()).xmap(
                    e -> e.map(DoubleRange::fixed, r -> r),
                    r -> r.isFixed()
                            ? Either.left(r.min)
                            : Either.right(r)
            );

    public static final Codec<DoubleRange> CODEC =
            RAW_CODEC.flatXmap(DoubleRange::validateRange, DataResult::success);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleRange> STREAM_CODEC =
            StreamCodec.of(
                    (buf, range) -> {
                        buf.writeDouble(range.min());
                        buf.writeDouble(range.max());
                    },
                    buf -> {
                        double min = buf.readDouble();
                        double max = buf.readDouble();

                        DoubleRange raw = new DoubleRange(min, max);
                        return validateRange(raw).getOrThrow(IllegalArgumentException::new);
                    }
            );
}