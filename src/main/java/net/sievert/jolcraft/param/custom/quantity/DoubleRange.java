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

public record DoubleRange(double min, double max) implements ParamData<DoubleRange> {

    public static final DoubleRange ZERO = fixed(0.0D);
    public static final DoubleRange ONE = fixed(1.0D);

    public static DoubleRange fixed(double value) {
        return new DoubleRange(value, value);
    }

    public boolean isFixed() {
        return min == max;
    }

    public double roll(RandomSource random) {
        return isFixed() ? min : min + random.nextDouble() * (max - min);
    }

    public static DataResult<DoubleRange> validateRange(DoubleRange value) {
        if (value == null) {
            return ParamValidations.invalid("DoubleRange is required");
        }

        return ParamValidations.minMax(value, value.min, value.max, "DoubleRange");
    }

    @Override
    public DataResult<DoubleRange> validate() {
        return validateRange(this);
    }

    private static final MapCodec<DoubleRange> OBJECT_CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.DOUBLE.fieldOf(JolCraftParameterIds.MIN).forGetter(DoubleRange::min),
                    Codec.DOUBLE.fieldOf(JolCraftParameterIds.MAX).forGetter(DoubleRange::max)
            ).apply(inst, DoubleRange::new));

    private static final Codec<DoubleRange> RAW_CODEC = ParamCodecs.either(
            Codec.DOUBLE,
            OBJECT_CODEC.codec(),
            either -> ParamValidations.ok(either.map(DoubleRange::fixed, range -> range)),
            range -> ParamValidations.ok(range.isFixed()
                    ? Either.left(range.min)
                    : Either.right(range))
    );

    public static final Codec<DoubleRange> CODEC =
            ParamCodecs.validated(RAW_CODEC, DoubleRange::validateRange);

    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleRange> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, range) -> {
                        buf.writeDouble(range.min());
                        buf.writeDouble(range.max());
                    },
                    buf -> new DoubleRange(buf.readDouble(), buf.readDouble())
            ), DoubleRange::validateRange);

    @Override
    public Codec<DoubleRange> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DoubleRange> streamCodec() {
        return STREAM_CODEC;
    }
}