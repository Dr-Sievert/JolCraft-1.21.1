package net.sievert.jolcraft.datagen.base.builder;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import org.jetbrains.annotations.NotNull;

public interface JolCraftEmissionBuilder<TTarget>
        extends JolCraftValidatedBuilder<JolCraftDataEmission<TTarget>> {

    @Override
    @NotNull
    DataResult<JolCraftDataEmission<TTarget>> buildValidated();
}