package net.sievert.jolcraft.data.recipe.param.quantity;

public interface HasCount {
    IntRange count();

    default boolean hasValidCountRange() {
        IntRange c = count();
        if (c == null) return false;
        int min = c.min();
        int max = c.max();
        return min >= 1 && max >= 1 && min <= max;
    }
}