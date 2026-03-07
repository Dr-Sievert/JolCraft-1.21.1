package net.sievert.jolcraft.data.recipe.param;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

/**
 * Small helpers to enforce uniform param codec patterns.
 */
public final class ParamCodecs {

    private ParamCodecs() {}

    /**
     * Wrap a RAW codec with the JolCraft validation contract:
     * - Decode: validate (flatXmap)
     * - Encode: passthrough (success)
     *
     * Usage:
     * public static final Codec<Foo> CODEC = ParamCodecs.validated(RAW_CODEC);
     */
    public static <T extends SelfValidating<T>> Codec<T> validated(Codec<T> rawCodec) {
        return rawCodec.flatXmap(SelfValidating::validate, DataResult::success);
    }
}