package net.sievert.jolcraft.datagen.base.builder;

import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.NotNull;

public interface JolCraftValidatedBuilder<T> {

    @NotNull
    DataResult<T> buildValidated();
}