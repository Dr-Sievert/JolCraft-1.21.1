package net.sievert.jolcraft.datagen.tag.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.tag.JolCraftMainTagProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class JolCraftBiomeTagProvider
        extends BiomeTagsProvider
        implements JolCraftMainTagProvider<JolCraftBiomeTagProvider> {

    private final @Nullable ExistingFileHelper existingFileHelper;

    public JolCraftBiomeTagProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, JolCraft.MOD_ID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public @NotNull String tagType() {
        return JolCraftDictionary.BIOME;
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        generate(this, null, CompletableFuture.completedFuture(provider), existingFileHelper);

        JolCraftDataTracking.logExplicitCount(
                this,
                this.builders.size(),
                JolCraftStrings.spaced(tagType(), JolCraftStrings.plural(domain().getId()))
        );
    }

    @Override
    public void run(
            @NotNull JolCraftBiomeTagProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        target.tag(JolCraftTags.Biomes.MOUNTAINS_AND_HILLS)
                .addTag(BiomeTags.IS_MOUNTAIN)
                .addTag(BiomeTags.IS_HILL);

        target.tag(JolCraftTags.Biomes.DWARVEN)
                .addTag(JolCraftTags.Biomes.MOUNTAINS_AND_HILLS)
                .addTag(BiomeTags.IS_FOREST)
                .addTag(BiomeTags.IS_TAIGA);

        target.tag(JolCraftTags.Biomes.MITHRIL_SPECIAL)
                .addTag(JolCraftTags.Biomes.MOUNTAINS_AND_HILLS)
                .add(Biomes.DEEP_DARK);
    }
}