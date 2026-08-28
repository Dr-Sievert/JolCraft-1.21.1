package net.sievert.jolcraft.datagen.config;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.config.subprovider.CorruptionEffectsConfigProvider;
import net.sievert.jolcraft.datagen.config.subprovider.DwarfProfessionConfigProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class JolCraftConfigProvider
        implements DataProvider,
        JolCraftMainDataProvider<CachedOutput> {

    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    private final List<JolCraftSubDataProvider<CachedOutput>>
            subProviders;

    public JolCraftConfigProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider>
                    lookupProvider
    ) {
        this.output = output;
        this.lookupProvider = lookupProvider;

        this.subProviders = List.of(
                new DwarfProfessionConfigProvider(this),
                new CorruptionEffectsConfigProvider(this)
        );
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.CONFIG;
    }

    @Override
    public @NotNull String id() {
        return domain().getId();
    }

    @Override
    public @NotNull String name() {
        return JolCraft.MOD_NAME + " " +
                JolCraftStrings.toTitleCase(
                        JolCraftStrings.underscored(
                                id(),
                                JolCraftDictionary.PROVIDER
                        )
                );
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

    @Override
    public @NotNull List<
            JolCraftSubDataProvider<CachedOutput>
            > subProviders() {
        return subProviders;
    }

    @Override
    public @NotNull CompletableFuture<?> run(
            @NotNull CachedOutput cache
    ) {
        generate(
                cache,
                output,
                lookupProvider,
                null
        );

        return CompletableFuture.completedFuture(null);
    }
}