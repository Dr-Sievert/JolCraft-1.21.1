package net.sievert.jolcraft.datagen.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface JolCraftDataProvider<TTarget> {

    @NotNull
    String id();

    @NotNull
    default String name() {
        return JolCraft.MOD_NAME + " " +
                JolCraftStrings.toTitleCase(
                        JolCraftStrings.underscored(id(), JolCraftParameterIds.PROVIDER)
                );
    }

    @Nullable
    default JolCraftDataProvider<?> parent() {
        return null;
    }

    @NotNull
    default List<? extends JolCraftSubDataProvider<TTarget>> subProviders() {
        return List.of();
    }

    @NotNull
    default List<JolCraftDataProvider<?>> chain() {
        List<JolCraftDataProvider<?>> reversed = new ArrayList<>();
        Set<JolCraftDataProvider<?>> seen = new HashSet<>();

        JolCraftDataProvider<?> current = this;
        while (current != null) {
            if (!seen.add(current)) {
                throw new IllegalStateException("Provider parent cycle detected at: " + current.name());
            }

            reversed.add(current);
            current = current.parent();
        }

        List<JolCraftDataProvider<?>> chain = new ArrayList<>(reversed.size());
        for (int i = reversed.size() - 1; i >= 0; i--) {
            chain.add(reversed.get(i));
        }

        return List.copyOf(chain);
    }

    @Nullable
    default String folder() {
        return null;
    }

    @NotNull
    default String validatedFolder() {
        String folder = folder();

        if (folder == null) {
            return "";
        }

        if (folder.isBlank()) {
            throw new IllegalStateException("Provider folder must not be blank: " + name());
        }

        if (folder.startsWith("/") || folder.endsWith("/")) {
            throw new IllegalStateException(
                    "Provider folder must not start or end with '/': " + name() + " -> " + folder
            );
        }

        if (folder.contains("//")) {
            throw new IllegalStateException(
                    "Provider folder must not contain '//': " + name() + " -> " + folder
            );
        }

        if (folder.contains("\\")) {
            throw new IllegalStateException(
                    "Provider folder must not contain '\\': " + name() + " -> " + folder
            );
        }

        if (folder.contains("..")) {
            throw new IllegalStateException(
                    "Provider folder must not contain '..': " + name() + " -> " + folder
            );
        }

        return folder;
    }

    default void generateSelfAndChildren(
            @NotNull TTarget target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        Objects.requireNonNull(target, JolCraftParameterIds.TARGET);
        Objects.requireNonNull(tracking, JolCraftDictionary.TRACK);

        run(target, packOutput, lookupProvider, existingFileHelper, tracking);

        for (JolCraftSubDataProvider<TTarget> subProvider : subProviders()) {
            subProvider.generateSelfAndChildren(target, packOutput, lookupProvider, existingFileHelper, tracking);
        }
    }

    default void run(
            @NotNull TTarget target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {}
}