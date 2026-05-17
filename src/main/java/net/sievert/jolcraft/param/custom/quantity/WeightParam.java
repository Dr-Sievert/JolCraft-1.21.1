package net.sievert.jolcraft.param.custom.quantity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;

public record WeightParam(int value) implements ParamData<WeightParam> {

    public static final WeightParam ONE = new WeightParam(1);

    public WeightParam {
        if (value < 1) {
            throw new IllegalArgumentException(error(value));
        }
    }

    @Override
    public DataResult<WeightParam> validate() {
        return validateWeight(this);
    }

    private static DataResult<WeightParam> validateWeight(WeightParam weight) {
        if (weight == null) {
            return ParamValidations.invalid("'" + JolCraftParameterIds.WEIGHT + "' is required");
        }

        return weight.value >= 1
                ? ParamValidations.ok(weight)
                : ParamValidations.invalid(error(weight.value));
    }

    private static String error(int value) {
        return "'" + JolCraftParameterIds.WEIGHT + "' must be >= 1 (value=" + value + ")";
    }

    public static final Codec<WeightParam> CODEC =
            Codec.INT.flatXmap(
                    value -> value >= 1
                            ? ParamValidations.ok(new WeightParam(value))
                            : ParamValidations.invalid(error(value)),
                    weight -> validateWeight(weight).map(WeightParam::value)
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, WeightParam> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, weight) -> buf.writeVarInt(weight.value()),
                    buf -> new WeightParam(buf.readVarInt())
            ), WeightParam::validateWeight);

    @Override
    public Codec<WeightParam> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WeightParam> streamCodec() {
        return STREAM_CODEC;
    }
}