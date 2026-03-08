package net.sievert.jolcraft.data.recipe.param.quantity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import org.jetbrains.annotations.NotNull;

/**
 * Atomic weight parameter.
 *
 * - Serialized as a plain integer.
 * - Optional at usage sites (default = 1).
 * - value >= 0 (0 disables entry).
 *
 * This class does not define semantics.
 * Containers decide how weight is interpreted.
 */
public record WeightParam(int value) implements SelfValidating<WeightParam> {

    public static final WeightParam ONE = new WeightParam(1);

    // ---------------------------------------------------------------------
    // CANONICAL (clamp storage to >= 0)
    // ---------------------------------------------------------------------

    public WeightParam {
        if (value < 0) value = 0;
    }

    // ---------------------------------------------------------------------
    // CODEC (validated)
    // ---------------------------------------------------------------------

    private static final Codec<WeightParam> RAW_CODEC =
            Codec.INT.xmap(WeightParam::new, WeightParam::value);

    public static final Codec<WeightParam> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    /**
     * Stream is always one varint: value.
     * Decode degrades deterministically to {@link #ONE} on invalid (<0).
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, WeightParam> STREAM_CODEC =
            StreamCodec.of(
                    (buf, w) -> buf.writeVarInt(w.safe()),
                    buf -> {
                        int v = buf.readVarInt();
                        if (v < 0) return ONE;
                        return new WeightParam(v);
                    }
            );

    // ---------------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------------

    public int safe() {
        return Math.max(0, value);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<WeightParam> validate() {
        if (value < 0) {
            return DataResult.error(() ->
                    JolCraftParameterIds.WEIGHT + " must be >= 0");
        }
        return DataResult.success(this);
    }

    public static @NotNull DataResult<WeightParam> validate(WeightParam w) {
        if (w == null) {
            return DataResult.error(() ->
                    JolCraftParameterIds.WEIGHT + " cannot be null");
        }
        return w.validate();
    }
}