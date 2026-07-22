package net.sievert.jolcraft.datagen.base.builder;

import org.jetbrains.annotations.NotNull;

public interface JolCraftOrderedBuilder {

    /**
     * Stable sequencing bucket.
     * Builders sharing this key share the same 1..N counter.
     */
    @NotNull
    String orderKey();

    /**
     * 0 = unassigned
     * >0 = assigned
     */
    int order();

    void setOrder(int order);
}