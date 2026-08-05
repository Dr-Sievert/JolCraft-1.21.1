package net.sievert.jolcraft.datagen.tag.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.InstrumentTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.tag.JolCraftMainTagProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.instrument.JolCraftInstruments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class JolCraftInstrumentTagProvider
        extends InstrumentTagsProvider
        implements JolCraftMainTagProvider<JolCraftInstrumentTagProvider> {

    private final @Nullable ExistingFileHelper existingFileHelper;

    public JolCraftInstrumentTagProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(
                output,
                lookupProvider,
                JolCraft.MOD_ID,
                existingFileHelper
        );

        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public @NotNull String tagType() {
        return JolCraftDictionary.INSTRUMENT;
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

    @Override
    protected void addTags(
            HolderLookup.@NotNull Provider provider
    ) {
        generate(
                this,
                null,
                CompletableFuture.completedFuture(provider),
                existingFileHelper
        );

        JolCraftDataTracking.logExplicitCount(
                this,
                this.builders.size(),
                JolCraftStrings.spaced(
                        tagType(),
                        JolCraftStrings.plural(domain().getId())
                )
        );
    }

    @Override
    public void run(
            @NotNull JolCraftInstrumentTagProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        target.tag(JolCraftTags.Instruments.WAR_HORNS)
                .add(
                        JolCraftInstruments.WAR_HORN.getKey()
                );
    }
}