package net.sievert.jolcraft.datagen.base;

import org.jetbrains.annotations.NotNull;

public interface JolCraftSubDataProvider<TTarget> extends JolCraftDataProvider<TTarget> {

    @Override
    @NotNull
    JolCraftDataProvider<TTarget> parent();
}