package net.sievert.jolcraft.param.base;

public interface ParamMatching<T> {

    boolean matches(T value);

    static <T> boolean allMatch(Iterable<? extends ParamMatching<T>> matchers, T value) {
        for (ParamMatching<T> matcher : matchers) {
            if (!matcher.matches(value)) return false;
        }

        return true;
    }

    static <T> boolean anyMatch(Iterable<? extends ParamMatching<T>> matchers, T value) {
        for (ParamMatching<T> matcher : matchers) {
            if (matcher.matches(value)) return true;
        }

        return false;
    }
}
