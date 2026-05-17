package net.sievert.jolcraft.param.custom.quantity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;

public record IntRange(int min, int max) implements ParamData<IntRange> {

    public static final IntRange ZERO = fixed(0);
    public static final IntRange ONE = fixed(1);

    public static IntRange fixed(int value) {
        return new IntRange(value, value);
    }

    public boolean isZero() {
        return min == 0 && max == 0;
    }

    public boolean isOne() {
        return min == 1 && max == 1;
    }

    public boolean isFixed() {
        return min == max;
    }

    public boolean isPositiveRange() {
        return min >= 1 && max >= min;
    }

    public int roll(RandomSource random) {
        return isFixed() ? min : min + random.nextInt((max - min) + 1);
    }

    public static DataResult<IntRange> validateRange(IntRange value) {
        if (value == null) {
            return ParamValidations.invalid("IntRange is required");
        }

        return ParamValidations.all(value,
                () -> ParamValidations.nonNegative(value, value.min, "IntRange min"),
                () -> ParamValidations.nonNegative(value, value.max, "IntRange max"),
                () -> ParamValidations.minMax(value, value.min, value.max, "IntRange")
        );
    }

    public static DataResult<IntRange> validatePositiveRange(IntRange value) {
        if (value == null) {
            return ParamValidations.invalid("IntRange is required");
        }

        return validateRange(value).flatMap(range ->
                range.isPositiveRange()
                        ? ParamValidations.ok(range)
                        : ParamValidations.invalid("IntRange values must be >= 1")
        );
    }

    @Override
    public DataResult<IntRange> validate() {
        return validateRange(this);
    }

    private static final MapCodec<IntRange> OBJECT_CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.INT.fieldOf(JolCraftParameterIds.MIN_COUNT).forGetter(IntRange::min),
                    Codec.INT.fieldOf(JolCraftParameterIds.MAX_COUNT).forGetter(IntRange::max)
            ).apply(inst, IntRange::new));

    private static final Codec<IntRange> RAW_CODEC = ParamCodecs.either(
            Codec.INT,
            OBJECT_CODEC.codec(),
            either -> ParamValidations.ok(either.map(IntRange::fixed, range -> range)),
            range -> ParamValidations.ok(range.isFixed()
                    ? Either.left(range.min)
                    : Either.right(range))
    );

    public static final Codec<IntRange> CODEC =
            ParamCodecs.validated(RAW_CODEC, IntRange::validateRange);

    public static final Codec<IntRange> POSITIVE_CODEC =
            ParamCodecs.validated(RAW_CODEC, IntRange::validatePositiveRange);

    public static final StreamCodec<RegistryFriendlyByteBuf, IntRange> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, range) -> {
                        buf.writeVarInt(range.min());
                        buf.writeVarInt(range.max());
                    },
                    buf -> new IntRange(buf.readVarInt(), buf.readVarInt())
            ), IntRange::validateRange);

    @Override
    public Codec<IntRange> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, IntRange> streamCodec() {
        return STREAM_CODEC;
    }
}