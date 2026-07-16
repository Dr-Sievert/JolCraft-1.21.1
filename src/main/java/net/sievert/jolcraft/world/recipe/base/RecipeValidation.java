package net.sievert.jolcraft.world.recipe.base;

import com.mojang.serialization.DataResult;
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

        public @NotNull Validator<R> check(
                @NotNull DataResult<?> result
        ) {
            return check(result, null);
        }

        public @NotNull Validator<R> check(
                @NotNull DataResult<?> result,
                @Nullable String path
        ) {
            if (hasError()) {
                return this;
            }

            result.error().ifPresent(error -> {
                if (path == null || path.isBlank()) {
                    state = DataResult.error(error::message);
                } else {
                    state = DataResult.error(
                            () -> path + " invalid: " + error.message()
                    );
                }
            });

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