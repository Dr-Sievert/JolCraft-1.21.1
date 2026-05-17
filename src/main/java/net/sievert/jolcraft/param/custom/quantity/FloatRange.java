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

public record FloatRange(float min, float max) implements ParamData<FloatRange> {

    public static final FloatRange ZERO = fixed(0.0F);
    public static final FloatRange ONE = fixed(1.0F);

    public static FloatRange fixed(float value) {
        return new FloatRange(value, value);
    }

    public boolean isFixed() {
        return min == max;
    }

    public float roll(RandomSource random) {
        return isFixed() ? min : min + random.nextFloat() * (max - min);
    }

    public static DataResult<FloatRange> validateRange(FloatRange value) {
        if (value == null) {
            return ParamValidations.invalid("FloatRange is required");
        }

        return ParamValidations.minMax(value, value.min, value.max, "FloatRange");
    }

    @Override
    public DataResult<FloatRange> validate() {
        return validateRange(this);
    }

    private static final MapCodec<FloatRange> OBJECT_CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.FLOAT.fieldOf(JolCraftParameterIds.MIN).forGetter(FloatRange::min),
                    Codec.FLOAT.fieldOf(JolCraftParameterIds.MAX).forGetter(FloatRange::max)
            ).apply(inst, FloatRange::new));

    private static final Codec<FloatRange> RAW_CODEC = ParamCodecs.either(
            Codec.FLOAT,
            OBJECT_CODEC.codec(),
            either -> ParamValidations.ok(either.map(FloatRange::fixed, range -> range)),
            range -> ParamValidations.ok(range.isFixed()
                    ? Either.left(range.min)
                    : Either.right(range))
    );

    public static final Codec<FloatRange> CODEC =
            ParamCodecs.validated(RAW_CODEC, FloatRange::validateRange);

    public static final StreamCodec<RegistryFriendlyByteBuf, FloatRange> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, range) -> {
                        buf.writeFloat(range.min());
                        buf.writeFloat(range.max());
                    },
                    buf -> new FloatRange(buf.readFloat(), buf.readFloat())
            ), FloatRange::validateRange);

    @Override
    public Codec<FloatRange> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FloatRange> streamCodec() {
        return STREAM_CODEC;
    }
}