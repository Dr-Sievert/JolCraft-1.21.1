package net.sievert.jolcraft.datagen.biome;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class JolCraftBiomeTagProvider extends BiomeTagsProvider {

    public JolCraftBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, JolCraft.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //General

        tag(JolCraftTags.Biomes.MOUNTAINS_AND_HILLS)
                .addTag(BiomeTags.IS_MOUNTAIN)
                .addTag(BiomeTags.IS_HILL);

        //Structures

        tag(JolCraftTags.Biomes.DWARVEN)
                .add(
                        Biomes.FOREST,
                        Biomes.FLOWER_FOREST,
                        Biomes.BIRCH_FOREST,
                        Biomes.OLD_GROWTH_BIRCH_FOREST,
                        Biomes.DARK_FOREST,
                        Biomes.PALE_GARDEN,
                        Biomes.GROVE,

                        Biomes.WINDSWEPT_HILLS,
                        Biomes.WINDSWEPT_FOREST,
                        Biomes.WINDSWEPT_GRAVELLY_HILLS,

                        Biomes.MEADOW,
                        Biomes.FROZEN_PEAKS,
                        Biomes.JAGGED_PEAKS,
                        Biomes.STONY_PEAKS,
                        Biomes.SNOWY_SLOPES,
                        Biomes.CHERRY_GROVE,

                        Biomes.TAIGA,
                        Biomes.SNOWY_TAIGA,
                        Biomes.OLD_GROWTH_PINE_TAIGA,
                        Biomes.OLD_GROWTH_SPRUCE_TAIGA
                );

        tag(JolCraftTags.Biomes.HAS_FORGE).addTag(JolCraftTags.Biomes.DWARVEN);
        tag(JolCraftTags.Biomes.HAS_DWARVEN_TRAIL_RUIN).addTag(JolCraftTags.Biomes.DWARVEN);
    }

    @Override
    public @NotNull String getName() {
        return "JolCraft Biome Tags";
    }
}
