package net.sievert.jolcraft.world.recipe.base;

import com.mojang.serialization.DataResult;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class RecipeValidation {

    private RecipeValidation() {}

    public static <T> @NotNull DataResult<T> requireRecipe(
            @Nullable T recipe
    ) {
        return recipe == null
                ? DataResult.error(() -> "recipe is null")
                : DataResult.success(recipe);
    }

    public static <T> @NotNull DataResult<T> require(
            @Nullable T value,
            @NotNull String name
    ) {
        return value == null
                ? DataResult.error(() -> name + " is required")
                : DataResult.success(value);
    }

    public static <R> @NotNull Validator<R> validate(
            @Nullable R recipe
    ) {
        return new Validator<>(recipe);
    }

    public static @NotNull DataResult<Void> validateOutput(
            @Nullable RecipeOutput output,
            @NotNull LootContextParamSet params
    ) {
        if (output == null) {
            return DataResult.error(() ->
                    "recipe output is required"
            );
        }

        ProblemReporter.Collector problems =
                new ProblemReporter.Collector();

        ValidationContext context =
                new ValidationContext(
                        problems,
                        params
                );

        output.validate(
                context
        );

        return problems.getReport()
                .<DataResult<Void>>map(report ->
                        DataResult.error(() -> report)
                )
                .orElseGet(() ->
                        DataResult.success(null)
                );
    }

    public static final class Validator<R> {

        private DataResult<R> state;

        private Validator(@Nullable R recipe) {
            state = requireRecipe(recipe);
        }

        public @NotNull Validator<R> require(
                @Nullable Object value,
                @NotNull String name
        ) {
            if (hasError()) {
                return this;
            }

            if (value == null) {
                state = DataResult.error(
                        () -> name + " is required"
                );
            }

            return this;
        }

        public @NotNull Validator<R> rule(
                boolean valid,
                @NotNull Supplier<String> errorMessage
        ) {
            if (!hasError() && !valid) {
                state = DataResult.error(errorMessage);
            }

            return this;
        }

        public @NotNull DataResult<R> done() {
            return state;
        }

        private boolean hasError() {
            return state.error().isPresent();
        }
    }
}