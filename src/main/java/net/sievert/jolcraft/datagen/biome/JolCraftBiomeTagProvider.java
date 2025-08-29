package net.sievert.jolcraft.datagen.biome;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class JolCraftBiomeTagProvider extends BiomeTagsProvider {

    public JolCraftBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, JolCraft.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(JolCraftTags.Biomes.MOUNTAINS_AND_HILLS)
                .addTag(BiomeTags.IS_MOUNTAIN)
                .addTag(BiomeTags.IS_HILL);
    }

    @Override
    public @NotNull String getName() {
        return "JolCraft Biome Tags";
    }
}
