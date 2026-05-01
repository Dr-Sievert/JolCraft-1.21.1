package net.sievert.jolcraft.world.recipe.param.quantity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;

/**
 * Stable, data-driven float range.
 *
 * Schema:
 * - either a float (fixed)
 * - or an object { "min": X, "max": Y }
 *
 * Invariants:
 * - finite values
 * - min <= max
 */
public record FloatRange(float min, float max) implements SelfValidating<FloatRange> {

    public static final FloatRange ZERO = fixed(0f);
    public static final FloatRange ONE = fixed(1f);

    // ---------------------------------------------------------------------
    // FACTORY
    // ---------------------------------------------------------------------

    public static FloatRange fixed(float value) {
        return new FloatRange(value, value);
    }

    public boolean isFixed() {
        return min == max;
    }

    public float roll(RandomSource random) {
        float min = this.min;
        float max = this.max;

        if (max < min) {
            float t = min;
            min = max;
            max = t;
        }

        if (min == max) {
            return min;
        }

        return min + random.nextFloat() * (max - min);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    public static DataResult<FloatRange> validateRange(FloatRange value) {
        if (value == null) {
            return DataResult.error(() -> "FloatRange is null");
        }

        if (!Float.isFinite(value.min) || !Float.isFinite(value.max)) {
            return DataResult.error(() -> "FloatRange values must be finite");
        }

        if (value.min > value.max) {
            return DataResult.error(() ->
                    "FloatRange min must be <= max (min=" + value.min + ", max=" + value.max + ")");
        }

        return DataResult.success(value);
    }

    @Override
    public DataResult<FloatRange> validate() {
        return validateRange(this);
    }

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final MapCodec<FloatRange> OBJECT_CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.FLOAT.fieldOf(JolCraftParameterIds.MIN).forGetter(FloatRange::min),
                    Codec.FLOAT.fieldOf(JolCraftParameterIds.MAX).forGetter(FloatRange::max)
            ).apply(inst, FloatRange::new));

    private static final Codec<FloatRange> RAW_CODEC =
            Codec.either(Codec.FLOAT, OBJECT_CODEC.codec()).xmap(
                    e -> e.map(FloatRange::fixed, r -> r),
                    r -> r.isFixed()
                            ? Either.left(r.min)
                            : Either.right(r)
            );

    public static final Codec<FloatRange> CODEC =
            RAW_CODEC.flatXmap(FloatRange::validateRange, DataResult::success);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, FloatRange> STREAM_CODEC =
            StreamCodec.of(
                    (buf, range) -> {
                        buf.writeFloat(range.min());
                        buf.writeFloat(range.max());
                    },
                    buf -> {
                        float min = buf.readFloat();
                        float max = buf.readFloat();

                        FloatRange raw = new FloatRange(min, max);
                        return validateRange(raw).getOrThrow(IllegalArgumentException::new);
                    }
            );
}