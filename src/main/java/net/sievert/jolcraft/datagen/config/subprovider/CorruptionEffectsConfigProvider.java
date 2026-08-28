package net.sievert.jolcraft.datagen.config.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.config.custom.brewing.CorruptionEffectsConfig;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataPathResolver;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.config.ConfigCodecWriter;
import net.sievert.jolcraft.datagen.config.JolCraftConfigProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CorruptionEffectsConfigProvider
        implements JolCraftSubDataProvider<CachedOutput> {

    private final JolCraftConfigProvider parent;

    public CorruptionEffectsConfigProvider(
            @NotNull JolCraftConfigProvider parent
    ) {
        this.parent = parent;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(
                JolCraftDictionary.CORRUPTION,
                JolCraftStrings.plural(
                        JolCraftDictionary.EFFECT
                )
        );
    }

    @Override
    public @NotNull String folder() {
        return JolCraftDictionary.BREWING;
    }

    @Override
    public @NotNull JolCraftDataProvider<CachedOutput> parent() {
        return parent;
    }

    @Override
    public void run(
            @NotNull CachedOutput target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<
                    HolderLookup.Provider
                    > lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        if (packOutput == null) {
            throw new IllegalStateException(
                    "PackOutput is required for config datagen"
            );
        }

        if (lookupProvider == null) {
            throw new IllegalStateException(
                    "Registry lookup is required for corruption effects config datagen"
            );
        }

        String path =
                JolCraftDataPathResolver.resolvePath(
                        this,
                        id()
                );

        lookupProvider.thenCompose(registries ->
                ConfigCodecWriter.write(
                        target,
                        packOutput,
                        path,
                        CorruptionEffectsConfig.CODEC,
                        CorruptionEffectsConfig.defaults(),
                        registries
                )
        ).join();

        tracking.record(
                this,
                path
        );

        tracking.logTrackedOutputCount(
                this,
                JolCraftStrings.spaced(
                        JolCraftDictionary.CORRUPTION,
                        JolCraftStrings.plural(
                                JolCraftDictionary.EFFECT
                        ),
                        JolCraftDictionary.CONFIG
                )
        );
    }
}