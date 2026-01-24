package net.sievert.jolcraft.worldgen.feature;

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
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.custom.crop.DeepslateBulbsCropBlock;

import java.util.List;

public class JolCraftConfiguredFeatures {

    //Vegetation
    public static final ResourceKey<ConfiguredFeature<?, ?>> DUSKCAP_PATCH_KEY = registerKey("duskcap_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_BULBS_PATCH_KEY = registerKey("deepslate_bulbs_patch");

    //Ores
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_MITHRIL_ORE_KEY = registerKey("small_mithril_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEDIUM_MITHRIL_ORE_KEY = registerKey("medium_mithril_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LARGE_MITHRIL_ORE_KEY = registerKey("large_mithril_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPECIAL_MITHRIL_ORE_KEY = registerKey("special_mithril_ore");

    //Geodes
    public static final ResourceKey<ConfiguredFeature<?, ?>> BASALT_GEODE_KEY = registerKey("basalt_geode");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {

        //Vegetation
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

        List<OreConfiguration.TargetBlockState> overworldMithrilOres = List.of(
                OreConfiguration.target(deepslateReplaceables, JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get().defaultBlockState()));

        register(context, SMALL_MITHRIL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 1));
        register(context, MEDIUM_MITHRIL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 3));
        register(context, LARGE_MITHRIL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 5));
        register(context, SPECIAL_MITHRIL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 5));

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

        GeodeLayerSettings basaltGeodeLayers = new GeodeLayerSettings(
                0.5,  // filling radius (center)
                0.7,  // inner shell radius (where budding/geode block can appear)
                0.9,  // middle shell radius
                1.3   // outer shell radius
        );

        GeodeCrackSettings basaltGeodeCrack = new GeodeCrackSettings(
                0.98, // crack chance (almost always cracked)
                2.0,  // base crack size
                2     // crack point offset
        );

        GeodeConfiguration basaltGeodeConfig = new GeodeConfiguration(
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

        register(context, BASALT_GEODE_KEY, Feature.GEODE, basaltGeodeConfig);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, JolCraft.location(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
