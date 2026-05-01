package net.sievert.jolcraft.datagen.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface JolCraftMainDataProvider<TTarget> extends JolCraftDataProvider<TTarget> {

    @NotNull
    JolCraftDataDomain domain();

    @Override
    @NotNull
    String id();

    @NotNull
    default JolCraftDataTracking createTracking() {
        return new JolCraftDataTracking();
    }

    default void generate(
            @NotNull TTarget target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        JolCraftDataTracking tracking = createTracking();
        generateSelfAndChildren(target, packOutput, lookupProvider, existingFileHelper, tracking);
    }
}