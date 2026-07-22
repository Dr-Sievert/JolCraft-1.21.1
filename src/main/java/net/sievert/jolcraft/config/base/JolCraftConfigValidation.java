package net.sievert.jolcraft.config.base;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;

public final class JolCraftConfigValidation {

    private JolCraftConfigValidation() {}

    public static DataResult<?> require(boolean condition, String error) {
        return condition ? DataResult.success(Unit.INSTANCE)
                : DataResult.error(() -> error);
    }

    public static DataResult<?> positive(int value, String name) {
        return value > 0
                ? DataResult.success(Unit.INSTANCE)
                : DataResult.error(() -> name + " must be > 0 (got " + value + ")");
    }

    public static DataResult<?> nonNegative(int value, String name) {
        return value >= 0
                ? DataResult.success(Unit.INSTANCE)
                : DataResult.error(() -> name + " must be >= 0 (got " + value + ")");
    }

    public static DataResult<?> positive(float value, String name) {
        return value > 0
                ? DataResult.success(Unit.INSTANCE)
                : DataResult.error(() -> name + " must be > 0 (got " + value + ")");
    }
}