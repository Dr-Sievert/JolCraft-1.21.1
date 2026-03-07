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
import net.sievert.jolcraft.data.recipe.param.SelfValidating;

/**
 * Stable, data-driven integer range.
 *
 * Schema:
 * - either an int (fixed)
 * - or an object { "min_count": X, "max_count": Y }
 *
 * Invariants:
 * - values >= 1
 * - min <= max
 *
 * Validation is performed via Codec (not constructor).
 *
 * - no throws (in JolCraft code; buffer IO may throw)
 * - roll(...) is total; caller must provide non-null RandomSource.
 */
public record IntRange(int min, int max) implements SelfValidating<IntRange> {

    public static final IntRange ZERO = fixed(0);
    public static final IntRange ONE = fixed(1);

    public boolean isOne() {
        return min == 1 && max == 1;
    }

    // ---------------------------------------------------------------------
    // FACTORY
    // ---------------------------------------------------------------------

    public static IntRange fixed(int value) {
        return new IntRange(value, value);
    }

    public boolean isFixed() {
        return min == max;
    }

    public int roll(RandomSource random) {
        int min = this.min;
        int max = this.max;

        if (max < min) {
            int t = min;
            min = max;
            max = t;
        }

        if (min < 1) min = 1;
        if (max < 1) max = 1;

        int bound = (max - min) + 1;
        if (bound <= 1) {
            return min;
        }

        return min + random.nextInt(bound);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    /** Static validation helper (for callers and codecs). */
    public static DataResult<IntRange> validateRange(IntRange value) {
        if (value == null) {
            return DataResult.error(() -> "IntRange is null");
        }

        if (value.min < 1 || value.max < 1) {
            return DataResult.error(() ->
                    "IntRange values must be >= 1 (min=" + value.min + ", max=" + value.max + ")");
        }

        if (value.min > value.max) {
            return DataResult.error(() ->
                    "IntRange min must be <= max (min=" + value.min + ", max=" + value.max + ")");
        }

        return DataResult.success(value);
    }

    /** Uniform param validation entry point. */
    @Override
    public DataResult<IntRange> validate() {
        return validateRange(this);
    }

    // ---------------------------------------------------------------------
    // CODEC (int OR {min_count,max_count})
    // ---------------------------------------------------------------------

    private static final MapCodec<IntRange> OBJECT_CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.INT.fieldOf(JolCraftParameterIds.MIN_COUNT).forGetter(IntRange::min),
                    Codec.INT.fieldOf(JolCraftParameterIds.MAX_COUNT).forGetter(IntRange::max)
            ).apply(inst, IntRange::new));

    private static final Codec<IntRange> RAW_CODEC =
            Codec.either(Codec.INT, OBJECT_CODEC.codec()).xmap(
                    e -> e.map(IntRange::fixed, r -> r),
                    r -> r.isFixed()
                            ? Either.left(r.min)
                            : Either.right(r)
            );

    /**
     * Validated codec (enforces invariants on decode/encode).
     */
    public static final Codec<IntRange> CODEC =
            RAW_CODEC.flatXmap(IntRange::validateRange, DataResult::success);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    /**
     * Stream is always 2 varints: min, max.
     * Decode validates and degrades deterministically to {@link #ONE} on invalid.
     * (Avoids throws and keeps runtime total.)
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, IntRange> STREAM_CODEC =
            StreamCodec.of(
                    (buf, range) -> {
                        buf.writeVarInt(range.min());
                        buf.writeVarInt(range.max());
                    },
                    (buf) -> {
                        int min = buf.readVarInt();
                        int max = buf.readVarInt();

                        IntRange raw = new IntRange(min, max);
                        return validateRange(raw).result().orElse(ONE);
                    }
            );
}