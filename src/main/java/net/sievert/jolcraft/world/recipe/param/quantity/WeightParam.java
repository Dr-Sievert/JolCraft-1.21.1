package net.sievert.jolcraft.world.recipe.param.quantity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import org.jetbrains.annotations.NotNull;

/**
 * Atomic weight parameter.
 *
 * - Serialized as a plain integer.
 * - Valid values are whole numbers >= 1.
 * - Optional at usage sites; omission means default weight = 1.
 *
 * This class defines the atomic validity of a weight.
 * Containers decide whether the field itself is optional.
 */
public record WeightParam(int value) implements SelfValidating<WeightParam> {

    public static final WeightParam ONE = new WeightParam(1);

    public WeightParam {
        if (value < 1) {
            throw new IllegalArgumentException("'" + JolCraftParameterIds.WEIGHT + "' must be >= 1");
        }
    }

    private static final Codec<WeightParam> RAW_CODEC =
            Codec.INT.flatXmap(
                    value -> value < 1
                            ? DataResult.error(() ->
                            "'" + JolCraftParameterIds.WEIGHT + "' must be a whole number greater than 0")
                            : DataResult.success(new WeightParam(value)),
                    weight -> DataResult.success(weight.value())
            );

    public static final Codec<WeightParam> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, WeightParam> STREAM_CODEC =
            StreamCodec.of(
                    (buf, weight) -> buf.writeVarInt(weight.value()),
                    buf -> new WeightParam(buf.readVarInt())
            );

    @Override
    public @NotNull DataResult<WeightParam> validate() {
        if (value < 1) {
            return DataResult.error(() ->
                    "'" + JolCraftParameterIds.WEIGHT + "' must be a whole number greater than 0");
        }
        return DataResult.success(this);
    }

    public static @NotNull DataResult<WeightParam> validate(WeightParam weight) {
        if (weight == null) {
            return DataResult.error(() ->
                    "'" + JolCraftParameterIds.WEIGHT + "' cannot be null");
        }
        return weight.validate();
    }
}