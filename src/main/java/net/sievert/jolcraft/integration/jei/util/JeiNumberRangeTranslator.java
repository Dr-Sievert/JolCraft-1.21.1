package net.sievert.jolcraft.integration.jei.util;

import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.mixin.UniformGeneratorAccessor;
import org.jetbrains.annotations.NotNull;

public final class JeiNumberRangeTranslator {

    private JeiNumberRangeTranslator() {
    }

    public static @NotNull NumberRange translate(
            @NotNull NumberProvider provider
    ) {
        if (provider instanceof ConstantValue(float value)) {
            int count =
                    requireInteger(
                            value,
                            "constant number provider"
                    );

            return new NumberRange(
                    count,
                    count
            );
        }

        if (provider instanceof UniformGenerator uniform) {
            UniformGeneratorAccessor accessor =
                    (UniformGeneratorAccessor) (Object) uniform;

            int min =
                    readConstant(
                            accessor.jolcraft$getMin(),
                            "uniform minimum"
                    );

            int max =
                    readConstant(
                            accessor.jolcraft$getMax(),
                            "uniform maximum"
                    );

            return new NumberRange(
                    min,
                    max
            );
        }

        throw new IllegalArgumentException(
                "Unsupported JEI number provider: "
                        + provider.getClass()
                        .getName()
        );
    }

    private static int readConstant(
            @NotNull NumberProvider provider,
            @NotNull String description
    ) {
        if (!(provider instanceof ConstantValue(float value))) {
            throw new IllegalArgumentException(
                    "JEI translation requires a constant "
                            + description
                            + ", found "
                            + provider.getClass()
                            .getName()
            );
        }

        return requireInteger(
                value,
                description
        );
    }

    private static int requireInteger(
            float value,
            @NotNull String description
    ) {
        if (!Float.isFinite(
                value
        )) {
            throw new IllegalArgumentException(
                    "Expected a finite "
                            + description
                            + ", found "
                            + value
            );
        }

        if (value != Math.floor(
                value
        )) {
            throw new IllegalArgumentException(
                    "Expected an integer "
                            + description
                            + ", found "
                            + value
            );
        }

        return (int) value;
    }

    public record NumberRange(
            int min,
            int max
    ) {

        public NumberRange {
            if (min < 0) {
                throw new IllegalArgumentException(
                        "min must be at least 0"
                );
            }

            if (max < min) {
                throw new IllegalArgumentException(
                        "max must be at least min"
                );
            }
        }

        public boolean fixed() {
            return min == max;
        }
    }
}