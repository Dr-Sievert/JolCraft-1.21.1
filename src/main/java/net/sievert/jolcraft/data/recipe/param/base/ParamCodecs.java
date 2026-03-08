package net.sievert.jolcraft.data.recipe.param.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.NotNull;

/**
 * Small helpers to enforce uniform param codec patterns.
 */
public final class ParamCodecs {

    private ParamCodecs() {}

    /**
     * Wrap a raw codec with the JolCraft validation contract:
     * - Decode: validate via {@link SelfValidating#validate()}
     * - Encode: passthrough via success
     *
     * Usage:
     * public static final Codec<Foo> CODEC = ParamCodecs.validated(RAW_CODEC);
     */
    public static <T extends SelfValidating<T>> @NotNull Codec<T> validated(@NotNull Codec<T> rawCodec) {

        return rawCodec.flatXmap(
                SelfValidating::validate,
                DataResult::success
        );
    }
}