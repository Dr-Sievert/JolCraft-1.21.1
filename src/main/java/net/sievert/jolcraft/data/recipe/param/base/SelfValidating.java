package net.sievert.jolcraft.data.recipe.param.base;

import com.mojang.serialization.DataResult;

public interface SelfValidating<T> extends Param {

    @Override
    DataResult<T> validate();

    static <T> DataResult<T> invalid(String message) {
        return DataResult.error(() -> message);
    }

    static <T> DataResult<T> ok(T value) {
        return DataResult.success(value);
    }
}