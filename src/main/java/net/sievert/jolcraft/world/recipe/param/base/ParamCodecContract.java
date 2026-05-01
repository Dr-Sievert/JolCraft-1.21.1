package net.sievert.jolcraft.world.recipe.param.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.function.Function;

public final class ParamCodecContract {

    private ParamCodecContract() {}

    public static <R, T extends SelfValidating<T>> Codec<T> create(
            Codec<R> rawCodec,
            Function<R, DataResult<T>> fromRaw,
            Function<T, R> toRaw
    ) {
        return rawCodec.flatXmap(
                raw -> fromRaw.apply(raw).flatMap(SelfValidating::validate),
                value -> DataResult.success(toRaw.apply(value))
        );
    }

}