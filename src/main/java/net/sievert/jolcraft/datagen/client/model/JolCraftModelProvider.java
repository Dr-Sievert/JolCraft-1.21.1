package net.sievert.jolcraft.datagen.client.model;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.client.model.subprovider.ArtisanModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.BrewingModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.CropModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.DwarfModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.EggModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.MaterialModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.MiscModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.ScrapperModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.ToolModelSubProvider;
import net.sievert.jolcraft.datagen.client.model.subprovider.TrimModelSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public final class JolCraftModelProvider implements DataProvider, JolCraftMainDataProvider<JolCraftModelProvider> {

    private final @NotNull PackOutput packOutput;
    private final @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final @Nullable ExistingFileHelper existingFileHelper;
    private final @NotNull List<JolCraftSubDataProvider<JolCraftModelProvider>> subProviders;

    private @Nullable JolCraftDataLookups lookups;
    private @Nullable JolCraftModelBuilder builder;

    public JolCraftModelProvider(
            @NotNull PackOutput packOutput,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        this.packOutput = packOutput;
        this.lookupProvider = lookupProvider;
        this.existingFileHelper = existingFileHelper;
        this.subProviders = List.of(
                new ArtisanModelSubProvider(this),
                new BrewingModelSubProvider(this),
                new CropModelSubProvider(this),
                new DwarfModelSubProvider(this),
                new EggModelSubProvider(this),
                new MaterialModelSubProvider(this),
                new MiscModelSubProvider(this),
                new ScrapperModelSubProvider(this),
                new ToolModelSubProvider(this),
                new TrimModelSubProvider(this)
        );
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.MODEL;
    }

    @Override
    public @NotNull String id() {
        return domain().getId();
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

    @Override
    public @NotNull List<? extends JolCraftSubDataProvider<JolCraftModelProvider>> subProviders() {
        return subProviders;
    }

    public @NotNull PackOutput packOutput() {
        return packOutput;
    }

    public @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider() {
        return lookupProvider;
    }

    public @Nullable ExistingFileHelper existingFileHelper() {
        return existingFileHelper;
    }

    public @NotNull JolCraftDataLookups lookups() {
        if (lookups == null) {
            lookups = new JolCraftDataLookups(lookupProvider.join());
        }
        return lookups;
    }

    public @NotNull JolCraftModelBuilder builder() {
        if (builder == null) {
            builder = new JolCraftModelBuilder(this);
        }
        return builder;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        this.lookups = new JolCraftDataLookups(lookupProvider.join());
        this.builder = new JolCraftModelBuilder(this);

        generate(this, packOutput, lookupProvider, existingFileHelper);

        return builder().save(cachedOutput)
                .thenRun(() -> JolCraftDataTracking.logExplicitCount(
                        this,
                        builder().totalCount(),
                        JolCraftStrings.plural(JolCraftDictionary.MODEL)
                ));
    }
}