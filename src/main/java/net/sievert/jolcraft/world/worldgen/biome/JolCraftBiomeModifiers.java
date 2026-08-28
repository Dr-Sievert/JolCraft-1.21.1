package net.sievert.jolcraft.world.worldgen.biome;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.worldgen.JolCraftBiomeModifierIds;
import net.sievert.jolcraft.world.worldgen.feature.JolCraftPlacedFeatures;

public class JolCraftBiomeModifiers {

    // Vegetation

    public static final ResourceKey<BiomeModifier> ADD_BLOODROOT =
            registerKey(JolCraftBiomeModifierIds.ADD_BLOODROOT);

    public static final ResourceKey<BiomeModifier> ADD_BLOODROOT_SPECIAL =
            registerKey(JolCraftBiomeModifierIds.ADD_BLOODROOT_SPECIAL);

    public static final ResourceKey<BiomeModifier> ADD_CYANELLA_PATCH =
            registerKey(JolCraftBiomeModifierIds.ADD_CYANELLA_PATCH);

    public static final ResourceKey<BiomeModifier> ADD_SKYBELL_PATCH =
            registerKey(JolCraftBiomeModifierIds.ADD_SKYBELL_PATCH);

    public static final ResourceKey<BiomeModifier> ADD_SKYBELL_SPECIAL_PATCH =
            registerKey(JolCraftBiomeModifierIds.ADD_SKYBELL_SPECIAL_PATCH);

    public static final ResourceKey<BiomeModifier> ADD_DUSKCAP_PATCH =
            registerKey(JolCraftBiomeModifierIds.ADD_DUSKCAP_PATCH);

    public static final ResourceKey<BiomeModifier> ADD_DEEPSLATE_BULBS_PATCH =
            registerKey(JolCraftBiomeModifierIds.ADD_DEEPSLATE_BULBS_PATCH);

    // Ores

    public static final ResourceKey<BiomeModifier> ADD_ORE_BASALT_GEODE =
            registerKey(JolCraftBiomeModifierIds.ADD_ORE_BASALT_GEODE);

    public static final ResourceKey<BiomeModifier> ADD_ORE_TUFF_VITRIOL =
            registerKey(JolCraftBiomeModifierIds.ADD_ORE_TUFF_VITRIOL);

    public static final ResourceKey<BiomeModifier> ADD_ORE_MITHRIL_SMALL =
            registerKey(JolCraftBiomeModifierIds.ADD_ORE_MITHRIL_SMALL);

    public static final ResourceKey<BiomeModifier> ADD_ORE_MITHRIL_MEDIUM =
            registerKey(JolCraftBiomeModifierIds.ADD_ORE_MITHRIL_MEDIUM);

    public static final ResourceKey<BiomeModifier> ADD_ORE_MITHRIL_LARGE =
            registerKey(JolCraftBiomeModifierIds.ADD_ORE_MITHRIL_LARGE);

    public static final ResourceKey<BiomeModifier> ADD_ORE_MITHRIL_SPECIAL =
            registerKey(JolCraftBiomeModifierIds.ADD_ORE_MITHRIL_SPECIAL);

    public static final ResourceKey<BiomeModifier> ADD_ORE_MITHRIL_BURIED =
            registerKey(JolCraftBiomeModifierIds.ADD_ORE_MITHRIL_BURIED);

    // Geodes

    public static final ResourceKey<BiomeModifier> ADD_BASALT_GEODE =
            registerKey(JolCraftBiomeModifierIds.ADD_BASALT_GEODE);

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        //Vegetation
        context.register(
                ADD_BLOODROOT,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.BLOODROOT_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ADD_BLOODROOT_SPECIAL,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(biomes.getOrThrow(Biomes.DRIPSTONE_CAVES)),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.BLOODROOT_SPECIAL_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ADD_CYANELLA_PATCH,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(biomes.getOrThrow(Biomes.WARPED_FOREST)),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.CYANELLA_PATCH_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ADD_SKYBELL_PATCH,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(JolCraftTags.Biomes.MOUNTAINS_AND_HILLS),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.SKYBELL_PATCH_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ADD_SKYBELL_SPECIAL_PATCH,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(biomes.getOrThrow(Biomes.MEADOW)),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.SKYBELL_PATCH_SPECIAL_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ADD_DUSKCAP_PATCH,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.DUSKCAP_PATCH_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ADD_DEEPSLATE_BULBS_PATCH,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.DEEPSLATE_BULBS_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        //Ores

        context.register(
                ADD_ORE_BASALT_GEODE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(biomes.getOrThrow(Biomes.BASALT_DELTAS)),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(
                                        JolCraftPlacedFeatures.ORE_BASALT_GEODE_PLACED_KEY
                                )
                        ),
                        GenerationStep.Decoration.UNDERGROUND_DECORATION
                )
        );

        context.register(
                ADD_ORE_TUFF_VITRIOL,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(
                                        JolCraftPlacedFeatures.ORE_TUFF_VITRIOL_PLACED_KEY
                                )
                        ),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(ADD_ORE_MITHRIL_SMALL, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.ORE_MITHRIL_SMALL_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ORE_MITHRIL_MEDIUM, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.ORE_MITHRIL_MEDIUM_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ORE_MITHRIL_LARGE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.ORE_MITHRIL_LARGE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ORE_MITHRIL_SPECIAL, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(JolCraftTags.Biomes.MITHRIL_SPECIAL),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.ORE_MITHRIL_SPECIAL_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ORE_MITHRIL_BURIED, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(JolCraftTags.Biomes.MITHRIL_SPECIAL),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.ORE_MITHRIL_BURIED_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        //Geodes
        context.register(
                ADD_BASALT_GEODE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.BASALT_GEODE_PLACED_KEY)),
                        GenerationStep.Decoration.UNDERGROUND_STRUCTURES
                )
        );

    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, JolCraft.location(name));
    }
}
