package net.sievert.jolcraft.param.base;

import com.mojang.serialization.DataResult;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ParamValidations {

    private ParamValidations() {}

    public static <T> DataResult<T> ok(T value) {
        return DataResult.success(value);
    }

    public static <T> DataResult<T> invalid(String message) {
        return DataResult.error(() -> message);
    }

    public static <T> DataResult<T> rule(T owner, boolean ok, Supplier<String> message) {
        return ok ? ok(owner) : invalid(message.get());
    }

    @SafeVarargs
    public static <T> DataResult<T> all(T owner, Supplier<DataResult<T>>... checks) {
        for (Supplier<DataResult<T>> check : checks) {
            DataResult<T> result = check.get();
            if (result.error().isPresent()) {
                return result;
            }
        }

        return ok(owner);
    }

    public static <T> DataResult<T> required(T value, String name) {
        return value != null
                ? ok(value)
                : invalid(name + " is required");
    }

    public static <T> DataResult<T> notNull(T owner, Object value, String name) {
        return value != null
                ? ok(owner)
                : invalid(name + " is required");
    }

    public static <T, C extends Collection<?>> DataResult<T> notEmpty(T owner, C values, String name) {
        return values != null && !values.isEmpty()
                ? ok(owner)
                : invalid(name + " must not be empty");
    }

    public static <T> DataResult<T> exactlyOne(T owner, Object a, String aName, Object b, String bName) {
        boolean hasA = a != null;
        boolean hasB = b != null;

        return hasA != hasB
                ? ok(owner)
                : invalid("requires exactly one of '" + aName + "' or '" + bName + "'");
    }

    public static <T> DataResult<T> atMostOne(T owner, Object a, String aName, Object b, String bName) {
        return a != null && b != null
                ? invalid("requires at most one of '" + aName + "' or '" + bName + "'")
                : ok(owner);
    }

    public static <T> DataResult<T> minMax(T owner, int min, int max, String name) {
        return min <= max
                ? ok(owner)
                : invalid(name + " min must be <= max");
    }

    public static <T> DataResult<T> minMax(T owner, float min, float max, String name) {
        if (!Float.isFinite(min) || !Float.isFinite(max)) {
            return invalid(name + " values must be finite");
        }

        return min <= max
                ? ok(owner)
                : invalid(name + " min must be <= max");
    }

    public static <T> DataResult<T> minMax(T owner, double min, double max, String name) {
        if (!Double.isFinite(min) || !Double.isFinite(max)) {
            return invalid(name + " values must be finite");
        }

        return min <= max
                ? ok(owner)
                : invalid(name + " min must be <= max");
    }

    public static <T> DataResult<T> nonNegative(T owner, int value, String name) {
        return value >= 0
                ? ok(owner)
                : invalid(name + " must be >= 0");
    }

    public static <T> DataResult<T> nonNegative(T owner, float value, String name) {
        return Float.isFinite(value) && value >= 0.0F
                ? ok(owner)
                : invalid(name + " must be finite and >= 0");
    }

    public static <T> DataResult<T> nonNegative(T owner, double value, String name) {
        return Double.isFinite(value) && value >= 0.0D
                ? ok(owner)
                : invalid(name + " must be finite and >= 0");
    }

    public static <T> DataResult<T> positive(T owner, int value, String name) {
        return value > 0
                ? ok(owner)
                : invalid(name + " must be > 0");
    }

    public static <T> DataResult<T> positive(T owner, float value, String name) {
        return Float.isFinite(value) && value > 0.0F
                ? ok(owner)
                : invalid(name + " must be finite and > 0");
    }

    public static <T> DataResult<T> positive(T owner, double value, String name) {
        return Double.isFinite(value) && value > 0.0D
                ? ok(owner)
                : invalid(name + " must be finite and > 0");
    }

    public static <T> DataResult<T> finite(T owner, float value, String name) {
        return Float.isFinite(value)
                ? ok(owner)
                : invalid(name + " must be finite");
    }

    public static <T> DataResult<T> finite(T owner, double value, String name) {
        return Double.isFinite(value)
                ? ok(owner)
                : invalid(name + " must be finite");
    }

    public static <T> List<T> sanitizeList(List<T> input) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }

        return List.copyOf(input.stream()
                .filter(Objects::nonNull)
                .toList());
    }

    public static <T, V extends ParamData<V>> DataResult<T> child(T owner, V value, String name) {
        if (value == null) {
            return invalid(name + " is required");
        }

        return wrap(owner, value.validate(), name);
    }

    public static <T, V extends ParamData<V>> DataResult<T> optionalChild(T owner, V value, String name) {
        return value == null
                ? ok(owner)
                : child(owner, value, name);
    }

    public static <T, V extends ParamData<V>> DataResult<T> children(
            T owner,
            List<V> values,
            String name
    ) {
        return children(owner, values, name, ParamData::validate);
    }

    public static <T, V> DataResult<T> children(
            T owner,
            List<V> values,
            String name,
            Function<V, DataResult<?>> validator
    ) {
        if (values == null) {
            return ok(owner);
        }

        for (int i = 0; i < values.size(); i++) {
            V value = values.get(i);

            if (value == null) {
                return invalid(name + "[" + i + "] is required");
            }

            DataResult<T> result = wrap(owner, validator.apply(value), name + "[" + i + "]");
            if (result.error().isPresent()) {
                return result;
            }
        }

        return ok(owner);
    }

    public static <T, V> DataResult<T> wrap(T owner, DataResult<V> result, String name) {
        return result.error()
                .<DataResult<T>>map(error -> invalid(name + " invalid: " + error.message()))
                .orElseGet(() -> ok(owner));
    }
}