package net.sievert.jolcraft.world.recipe.custom.base;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public final class RecipeValidation {

    private RecipeValidation() {}

    // ---------------------------------------------------------------------
    // Required / null checks
    // ---------------------------------------------------------------------

    public static <T> @NotNull DataResult<T> requireRecipe(@Nullable T recipe) {
        return recipe == null
                ? DataResult.error(() -> "recipe is null")
                : DataResult.success(recipe);
    }

    public static <T> @NotNull DataResult<T> require(@Nullable T v, @NotNull String name) {
        return v == null
                ? DataResult.error(() -> name + " is required")
                : DataResult.success(v);
    }

    // ---------------------------------------------------------------------
    // Validatable wrapping
    // ---------------------------------------------------------------------

    public static <T extends SelfValidating<T>> @NotNull DataResult<T> requireValid(
            @Nullable T v,
            @NotNull String name
    ) {
        if (v == null) return DataResult.error(() -> name + " is required");

        DataResult<T> vr = v.validate();
        var err = vr.error();
        return err.<DataResult<T>>map(e ->
                        DataResult.error(() -> name + " invalid: " + e.message()))
                .orElseGet(() -> DataResult.success(v));
    }

    // ---------------------------------------------------------------------
    // Fluent validator
    // ---------------------------------------------------------------------

    public static <R> @NotNull Validator<R> validate(@Nullable R recipe) {
        return new Validator<>(recipe);
    }

    public static final class Validator<R> {

        private DataResult<R> state;

        private Validator(@Nullable R recipe) {
            this.state = requireRecipe(recipe);
        }

        public @NotNull Validator<R> require(@Nullable Object v, @NotNull String name) {
            if (state.error().isPresent()) return this;
            var err = RecipeValidation.require(v, name).error();
            if (err.isPresent()) {
                var e = err.orElseThrow();
                state = DataResult.error(e::message);
            }
            return this;
        }

        public <T extends SelfValidating<T>> @NotNull Validator<R> requireValid(
                @Nullable T v,
                @NotNull String name
        ) {
            if (state.error().isPresent()) return this;
            var err = RecipeValidation.requireValid(v, name).error();
            if (err.isPresent()) {
                var e = err.orElseThrow();
                state = DataResult.error(e::message);
            }
            return this;
        }

        /** Checks an already-built DataResult and adopts its error message. */
        public @NotNull Validator<R> check(@NotNull DataResult<?> r) {
            return check(r, null);
        }

        /** Checks an already-built DataResult and prefixes its error message with "path invalid: ...". */
        public @NotNull Validator<R> check(@NotNull DataResult<?> r, @Nullable String path) {
            if (state.error().isPresent()) return this;
            var err = r.error();
            if (err.isPresent()) {
                var e = err.orElseThrow();
                if (path == null || path.isEmpty()) {
                    state = DataResult.error(e::message);
                } else {
                    state = DataResult.error(() -> path + " invalid: " + e.message());
                }
            }
            return this;
        }

        public @NotNull Validator<R> rule(boolean ok, @NotNull Supplier<String> msg) {
            if (state.error().isPresent()) return this;
            if (!ok) state = DataResult.error(msg);
            return this;
        }

        public @NotNull DataResult<R> done() {
            return state;
        }
    }
}