package net.sievert.jolcraft.world.worldgen.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.Tags;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftConfiguredFeatureIds;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.plant.crop.DeepslateBulbsCropBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JolCraftConfiguredFeatures {

    // Vegetation

    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOODROOT_KEY =
            registerKey(JolCraftConfiguredFeatureIds.BLOODROOT);

    public static final ResourceKey<ConfiguredFeature<?, ?>> CYANELLA_PATCH_KEY =
            registerKey(JolCraftConfiguredFeatureIds.CYANELLA_PATCH);

    public static final ResourceKey<ConfiguredFeature<?, ?>> SKYBELL_PATCH_KEY =
            registerKey(JolCraftConfiguredFeatureIds.SKYBELL_PATCH);

    public static final ResourceKey<ConfiguredFeature<?, ?>> DUSKCAP_PATCH_KEY =
            registerKey(JolCraftConfiguredFeatureIds.DUSKCAP_PATCH);

    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_DUSKCAP_KEY =
            registerKey(JolCraftConfiguredFeatureIds.HUGE_DUSKCAP);

    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_FESTERLING_KEY =
            registerKey(JolCraftConfiguredFeatureIds.HUGE_FESTERLING);

    public static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_BULBS_PATCH_KEY =
            registerKey(JolCraftConfiguredFeatureIds.DEEPSLATE_BULBS_PATCH);

    // Ores

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TUFF_VITRIOL_KEY =
            registerKey(JolCraftConfiguredFeatureIds.ORE_TUFF_VITRIOL);

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MITHRIL_SMALL_KEY =
            registerKey(JolCraftConfiguredFeatureIds.ORE_MITHRIL_SMALL);

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MITHRIL_MEDIUM_KEY =
            registerKey(JolCraftConfiguredFeatureIds.ORE_MITHRIL_MEDIUM);

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MITHRIL_LARGE_KEY =
            registerKey(JolCraftConfiguredFeatureIds.ORE_MITHRIL_LARGE);

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MITHRIL_SPECIAL_KEY =
            registerKey(JolCraftConfiguredFeatureIds.ORE_MITHRIL_SPECIAL);

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MITHRIL_BURIED_KEY =
            registerKey(JolCraftConfiguredFeatureIds.ORE_MITHRIL_BURIED);

    // Geodes

    public static final ResourceKey<ConfiguredFeature<?, ?>> BASALT_GEODE_KEY =
            registerKey(JolCraftConfiguredFeatureIds.BASALT_GEODE);



    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {

        //Vegetation
        register(
                context,
                BLOODROOT_KEY,
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(JolCraftBlocks.BLOODROOT.get())
                )
        );

        register(
                context,
                CYANELLA_PATCH_KEY,
                Feature.NETHER_FOREST_VEGETATION,
                new NetherForestVegetationConfig(
                        BlockStateProvider.simple(JolCraftBlocks.CYANELLA.get()),
                        5,
                        3
                )
        );

        register(
                context,
                SKYBELL_PATCH_KEY,
                Feature.FLOWER,
                new RandomPatchConfiguration(
                        15,
                        5,
                        3,
                        PlacementUtils.onlyWhenEmpty(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(JolCraftBlocks.SKYBELL.get()))
                        )
                )
        );

        register(
                context,
                DUSKCAP_PATCH_KEY,
                Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        30,
                        7,
                        3,
                        PlacementUtils.onlyWhenEmpty(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(JolCraftBlocks.DUSKCAP.get()))
                        )
                )
        );

        register(
                context,
                HUGE_DUSKCAP_KEY,
                JolCraftFeatures.HUGE_DUSKCAP.get(),
                new HugeMushroomFeatureConfiguration(
                        BlockStateProvider.simple(JolCraftBlocks.DUSKCAP_BLOCK.get()),
                        BlockStateProvider.simple(JolCraftBlocks.DUSKCAP_STEM.get()),
                        2
                )
        );

        register(
                context,
                HUGE_FESTERLING_KEY,
                JolCraftFeatures.HUGE_FESTERLING.get(),
                new HugeMushroomFeatureConfiguration(
                        BlockStateProvider.simple(JolCraftBlocks.FESTERLING_BLOCK.get()),
                        BlockStateProvider.simple(JolCraftBlocks.FESTERLING_STEM.get()),
                        2
                )
        );

        SimpleWeightedRandomList.Builder<BlockState> builder = SimpleWeightedRandomList.builder();
        for (int i = 0; i <= 9; i++) {
            builder.add(
                    JolCraftBlocks.DEEPSLATE_BULBS_CROP.get().defaultBlockState().setValue(DeepslateBulbsCropBlock.AGE, i), 1
            );
        }
        WeightedStateProvider provider = new WeightedStateProvider(builder);

        register(
                context,
                DEEPSLATE_BULBS_PATCH_KEY,
                Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        25,
                        3,
                        2,
                        PlacementUtils.onlyWhenEmpty(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(provider)
                        )
                )
        );

        //Ores
        RuleTest stoneReplaceables = new TagMatchTest(Tags.Blocks.ORE_BEARING_GROUND_STONE);
        RuleTest deepslateReplaceables = new TagMatchTest(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE);
        RuleTest netherrackReplaceables = new TagMatchTest(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK);
        RuleTest endReplaceables = new BlockMatchTest(Blocks.END_STONE);
        RuleTest tuffReplaceables = new BlockMatchTest(Blocks.TUFF);

        List<OreConfiguration.TargetBlockState> overworldMithrilOres = List.of(
                OreConfiguration.target(deepslateReplaceables, JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get().defaultBlockState()));

        register(context, ORE_MITHRIL_SMALL_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 1, 0.5F));
        register(context, ORE_MITHRIL_MEDIUM_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 3, 0.6F));
        register(context, ORE_MITHRIL_LARGE_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 5, 0.7F));
        register(context, ORE_MITHRIL_SPECIAL_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 5));
        register(context, ORE_MITHRIL_BURIED_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 5, 1.0F));

        List<OreConfiguration.TargetBlockState> tuffVitriolOres = List.of(
                OreConfiguration.target(
                        tuffReplaceables,
                        JolCraftBlocks.TUFF_VITRIOL_ORE.get().defaultBlockState()
                )
        );

        register(
                context,
                ORE_TUFF_VITRIOL_KEY,
                Feature.ORE,
                new OreConfiguration(tuffVitriolOres, 3)
        );

        //Geodes
        GeodeBlockSettings basaltGeodeBlocks = new GeodeBlockSettings(
                BlockStateProvider.simple(Blocks.AIR),                        // center (hollow)
                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),              // inner shell (usually)
                BlockStateProvider.simple(JolCraftBlocks.GEODE_BLOCK.get()),  // alternate inner shell (sometimes)
                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),              // middle shell
                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),              // outer shell
                List.of(Blocks.AIR.defaultBlockState()),
                BlockTags.FEATURES_CANNOT_REPLACE,
                BlockTags.GEODE_INVALID_BLOCKS
        );

        GeodeConfiguration basaltGeodeConfig = getGeodeConfiguration(basaltGeodeBlocks);

        register(context, BASALT_GEODE_KEY, JolCraftFeatures.BASALT_GEODE.get(), basaltGeodeConfig);
    }

    private static @NotNull GeodeConfiguration getGeodeConfiguration(GeodeBlockSettings basaltGeodeBlocks) {
        GeodeLayerSettings basaltGeodeLayers = new GeodeLayerSettings(
                0.5,  // filling radius (center)
                0.7,  // inner shell radius (where budding/geode block can appear)
                0.9,  // middle shell radius
                1.3   // outer shell radius
        );

        GeodeCrackSettings basaltGeodeCrack = new GeodeCrackSettings(
                0.8, // crack chance
                2.0,  // base crack size
                2     // crack point offset
        );

        return new GeodeConfiguration(
                basaltGeodeBlocks,
                basaltGeodeLayers,
                basaltGeodeCrack,
                0.0,   // usePotentialPlacementsChance
                0.15,  // useAlternateLayer0Chance
                false, // placementsRequireLayer0Alternate
                UniformInt.of(1, 1), // outerWallDistance
                UniformInt.of(2, 2), // distributionPoints
                UniformInt.of(0, 1), // pointOffset
                -4,  // minGenOffset
                4,   // maxGenOffset
                0.05, // noiseMultiplier
                1     // invalidBlocksThreshold
        );
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, JolCraft.location(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
