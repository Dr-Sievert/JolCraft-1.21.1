package net.sievert.jolcraft.world.worldgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.worldgen.JolCraftPlacedFeatureIds;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.worldgen.placement.custom.MaxYPlacementFilter;
import net.sievert.jolcraft.world.worldgen.placement.custom.MinYPlacementFilter;
import net.sievert.jolcraft.world.worldgen.predicate.custom.DarknessPredicate;

import java.util.List;

@SuppressWarnings("deprecation")
public class JolCraftPlacedFeatures {

    // Vegetation

    public static final ResourceKey<PlacedFeature> BLOODROOT_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.BLOODROOT_PLACED);

    public static final ResourceKey<PlacedFeature> BLOODROOT_SPECIAL_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.BLOODROOT_SPECIAL_PLACED);

    public static final ResourceKey<PlacedFeature> CYANELLA_PATCH_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.CYANELLA_PATCH_PLACED);

    public static final ResourceKey<PlacedFeature> SKYBELL_PATCH_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.SKYBELL_PATCH_PLACED);

    public static final ResourceKey<PlacedFeature> SKYBELL_PATCH_SPECIAL_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.SKYBELL_PATCH_SPECIAL_PLACED);

    public static final ResourceKey<PlacedFeature> DUSKCAP_PATCH_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.DUSKCAP_PATCH_PLACED);

    public static final ResourceKey<PlacedFeature> DEEPSLATE_BULBS_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.DEEPSLATE_BULBS_PATCH_PLACED);

    // Ores

    public static final ResourceKey<PlacedFeature> ORE_BASALT_GEODE_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.ORE_BASALT_GEODE_PLACED);

    public static final ResourceKey<PlacedFeature> ORE_TUFF_VITRIOL_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.ORE_TUFF_VITRIOL_PLACED);

    public static final ResourceKey<PlacedFeature> ORE_MITHRIL_SMALL_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.ORE_MITHRIL_SMALL_PLACED);

    public static final ResourceKey<PlacedFeature> ORE_MITHRIL_MEDIUM_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.ORE_MITHRIL_MEDIUM_PLACED);

    public static final ResourceKey<PlacedFeature> ORE_MITHRIL_LARGE_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.ORE_MITHRIL_LARGE_PLACED);

    public static final ResourceKey<PlacedFeature> ORE_MITHRIL_SPECIAL_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.ORE_MITHRIL_SPECIAL_PLACED);

    public static final ResourceKey<PlacedFeature> ORE_MITHRIL_BURIED_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.ORE_MITHRIL_BURIED_PLACED);

    // Geodes

    public static final ResourceKey<PlacedFeature> BASALT_GEODE_PLACED_KEY =
            registerKey(JolCraftPlacedFeatureIds.BASALT_GEODE_PLACED);



    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        //Vegetation
        register(
                context,
                BLOODROOT_PLACED_KEY,
                configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.BLOODROOT_KEY),
                List.of(
                        CountPlacement.of(1),
                        RarityFilter.onAverageOnceEvery(2),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(0),
                                VerticalAnchor.absolute(64)
                        ),
                        EnvironmentScanPlacement.scanningFor(
                                Direction.UP,
                                BlockPredicate.solid(),
                                BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                12
                        ),
                        RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
                        MinYPlacementFilter.of(0),
                        MaxYPlacementFilter.of(64),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.wouldSurvive(
                                        JolCraftBlocks.BLOODROOT.get().defaultBlockState(),
                                        BlockPos.ZERO
                                )
                        ),
                        BiomeFilter.biome()
                )
        );

        register(
                context,
                BLOODROOT_SPECIAL_PLACED_KEY,
                configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.BLOODROOT_KEY),
                List.of(
                        CountPlacement.of(3),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(0),
                                VerticalAnchor.absolute(64)
                        ),
                        EnvironmentScanPlacement.scanningFor(
                                Direction.UP,
                                BlockPredicate.solid(),
                                BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                12
                        ),
                        RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
                        MinYPlacementFilter.of(0),
                        MaxYPlacementFilter.of(64),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.wouldSurvive(
                                        JolCraftBlocks.BLOODROOT.get().defaultBlockState(),
                                        BlockPos.ZERO
                                )
                        ),
                        BiomeFilter.biome()
                )
        );

        register(
                context,
                CYANELLA_PATCH_PLACED_KEY,
                configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.CYANELLA_PATCH_KEY),
                List.of(
                        CountOnEveryLayerPlacement.of(1),
                        RarityFilter.onAverageOnceEvery(4),
                        BiomeFilter.biome()
                )
        );

        register(
                context,
                SKYBELL_PATCH_PLACED_KEY,
                configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.SKYBELL_PATCH_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(5),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING),
                        MinYPlacementFilter.of(100),
                        BiomeFilter.biome()
                )
        );

        register(
                context,
                SKYBELL_PATCH_SPECIAL_PLACED_KEY,
                configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.SKYBELL_PATCH_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(3),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING),
                        MinYPlacementFilter.of(115),
                        BiomeFilter.biome()
                )
        );

        register(
                context,
                DUSKCAP_PATCH_PLACED_KEY,
                configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.DUSKCAP_PATCH_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(3),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-64),
                                VerticalAnchor.absolute(0)
                        ),
                        BiomeFilter.biome()
                )
        );

        register(
                context,
                DEEPSLATE_BULBS_PLACED_KEY,
                configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.DEEPSLATE_BULBS_PATCH_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(2),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-64),
                                VerticalAnchor.absolute(0)
                        ),
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.allOf(
                                        BlockPredicate.matchesTag(
                                                new Vec3i(0, -1, 0),
                                                JolCraftTags.Blocks.DEEPSLATE_BULBS_PLANTABLE
                                        ),
                                        new DarknessPredicate(8)
                                )
                        ),
                        BiomeFilter.biome()
                )
        );

        //Ores

        register(
                context,
                ORE_BASALT_GEODE_PLACED_KEY,
                configuredFeatures.getOrThrow(
                        JolCraftConfiguredFeatures.ORE_BASALT_GEODE_KEY
                ),
                JolCraftOreReplacement.commonNetherOrePlacement(100)
        );

        register(
                context,
                ORE_TUFF_VITRIOL_PLACED_KEY,
                configuredFeatures.getOrThrow(
                        JolCraftConfiguredFeatures.ORE_TUFF_VITRIOL_KEY
                ),
                JolCraftOreReplacement.commonOrePlacement(
                        8,
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-64),
                                VerticalAnchor.absolute(0)
                        )
                )
        );

        register(context, ORE_MITHRIL_SMALL_PLACED_KEY, configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.ORE_MITHRIL_SMALL_KEY),
                JolCraftOreReplacement.rareOrePlacement(1, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));

        register(context, ORE_MITHRIL_MEDIUM_PLACED_KEY, configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.ORE_MITHRIL_MEDIUM_KEY),
                JolCraftOreReplacement.rareOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));

        register(context, ORE_MITHRIL_LARGE_PLACED_KEY, configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.ORE_MITHRIL_LARGE_KEY),
                JolCraftOreReplacement.rareOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));

        register(context, ORE_MITHRIL_SPECIAL_PLACED_KEY, configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.ORE_MITHRIL_SPECIAL_KEY),
                JolCraftOreReplacement.rareOrePlacement(1, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));

        register(context, ORE_MITHRIL_BURIED_PLACED_KEY, configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.ORE_MITHRIL_BURIED_KEY),
                JolCraftOreReplacement.rareOrePlacement(1, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));

        //Geodes
        register(
                context,
                BASALT_GEODE_PLACED_KEY,
                configuredFeatures.getOrThrow(JolCraftConfiguredFeatures.BASALT_GEODE_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(8),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-64),
                                VerticalAnchor.absolute(0)
                        ),
                        BiomeFilter.biome()
                )
        );
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, JolCraft.location(name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}