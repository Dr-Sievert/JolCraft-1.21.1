package net.sievert.jolcraft.world.worldgen.feature.custom;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.function.Predicate;

public class JolCraftGeodeFeature extends Feature<GeodeConfiguration> {

    private static final Direction[] DIRECTIONS = Direction.values();

    public JolCraftGeodeFeature(Codec<GeodeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GeodeConfiguration> context) {
        GeodeConfiguration config = context.config();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();

        int minGenOffset = config.minGenOffset;
        int maxGenOffset = config.maxGenOffset;

        List<Pair<BlockPos, Integer>> distributionPoints = Lists.newLinkedList();
        int pointCount = config.distributionPoints.sample(random);

        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(level.getSeed()));
        NormalNoise normalNoise = NormalNoise.create(worldgenRandom, -4, 1.0);

        List<BlockPos> crackPoints = Lists.newLinkedList();
        double pointScale = (double) pointCount / (double) config.outerWallDistance.getMaxValue();

        GeodeLayerSettings layerSettings = config.geodeLayerSettings;
        GeodeBlockSettings blockSettings = config.geodeBlockSettings;
        GeodeCrackSettings crackSettings = config.geodeCrackSettings;

        double fillingThreshold = 1.0 / Math.sqrt(layerSettings.filling);
        double innerThreshold = 1.0 / Math.sqrt(layerSettings.innerLayer + pointScale);
        double middleThreshold = 1.0 / Math.sqrt(layerSettings.middleLayer + pointScale);
        double outerThreshold = 1.0 / Math.sqrt(layerSettings.outerLayer + pointScale);
        double crackThreshold =
                1.0 / Math.sqrt(crackSettings.baseCrackSize + random.nextDouble() / 2.0 + (pointCount > 3 ? pointScale : 0.0));

        boolean generateCrack = (double) random.nextFloat() < crackSettings.generateCrackChance;
        int invalidBlocks = 0;

        for (int i = 0; i < pointCount; i++) {
            int xOffset = config.outerWallDistance.sample(random);
            int yOffset = config.outerWallDistance.sample(random);
            int zOffset = config.outerWallDistance.sample(random);

            BlockPos pointPos = origin.offset(xOffset, yOffset, zOffset);
            BlockState state = level.getBlockState(pointPos);

            // Vanilla change:
            // tolerate cave air; only count actual invalid blocks against placement.
            if (state.is(BlockTags.GEODE_INVALID_BLOCKS)) {
                if (++invalidBlocks > config.invalidBlocksThreshold) {
                    return false;
                }
            }

            distributionPoints.add(Pair.of(pointPos, config.pointOffset.sample(random)));
        }

        if (generateCrack) {
            int crackDirection = random.nextInt(4);
            int crackDistance = pointCount * 2 + 1;

            if (crackDirection == 0) {
                crackPoints.add(origin.offset(crackDistance, 7, 0));
                crackPoints.add(origin.offset(crackDistance, 5, 0));
                crackPoints.add(origin.offset(crackDistance, 1, 0));
            } else if (crackDirection == 1) {
                crackPoints.add(origin.offset(0, 7, crackDistance));
                crackPoints.add(origin.offset(0, 5, crackDistance));
                crackPoints.add(origin.offset(0, 1, crackDistance));
            } else if (crackDirection == 2) {
                crackPoints.add(origin.offset(crackDistance, 7, crackDistance));
                crackPoints.add(origin.offset(crackDistance, 5, crackDistance));
                crackPoints.add(origin.offset(crackDistance, 1, crackDistance));
            } else {
                crackPoints.add(origin.offset(0, 7, 0));
                crackPoints.add(origin.offset(0, 5, 0));
                crackPoints.add(origin.offset(0, 1, 0));
            }
        }

        List<BlockPos> potentialPlacements = Lists.newArrayList();
        Predicate<BlockState> replacePredicate = isReplaceable(config.geodeBlockSettings.cannotReplace);

        for (BlockPos currentPos : BlockPos.betweenClosed(origin.offset(minGenOffset, minGenOffset, minGenOffset),
                origin.offset(maxGenOffset, maxGenOffset, maxGenOffset))) {

            double noise = normalNoise.getValue(currentPos.getX(), currentPos.getY(), currentPos.getZ()) * config.noiseMultiplier;
            double geodeDensity = 0.0;
            double crackDensity = 0.0;

            for (Pair<BlockPos, Integer> point : distributionPoints) {
                geodeDensity += Mth.invSqrt(currentPos.distSqr(point.getFirst()) + point.getSecond()) + noise;
            }

            for (BlockPos crackPoint : crackPoints) {
                crackDensity += Mth.invSqrt(currentPos.distSqr(crackPoint) + (double) crackSettings.crackPointOffset) + noise;
            }

            if (geodeDensity < outerThreshold) {
                continue;
            }

            if (generateCrack && crackDensity >= crackThreshold && geodeDensity < fillingThreshold) {
                this.safeSetBlock(level, currentPos, Blocks.AIR.defaultBlockState(), replacePredicate);

                for (Direction direction : DIRECTIONS) {
                    BlockPos adjacentPos = currentPos.relative(direction);
                    FluidState fluidState = level.getFluidState(adjacentPos);
                    if (!fluidState.isEmpty()) {
                        level.scheduleTick(adjacentPos, fluidState.getType(), 0);
                    }
                }
            } else if (geodeDensity >= fillingThreshold) {
                this.safeSetBlock(level, currentPos, blockSettings.fillingProvider.getState(random, currentPos), replacePredicate);
            } else if (geodeDensity >= innerThreshold) {
                boolean useAlternateInner = (double) random.nextFloat() < config.useAlternateLayer0Chance;

                if (useAlternateInner) {
                    this.safeSetBlock(level, currentPos, blockSettings.alternateInnerLayerProvider.getState(random, currentPos), replacePredicate);
                } else {
                    this.safeSetBlock(level, currentPos, blockSettings.innerLayerProvider.getState(random, currentPos), replacePredicate);
                }

                if ((!config.placementsRequireLayer0Alternate || useAlternateInner)
                        && (double) random.nextFloat() < config.usePotentialPlacementsChance) {
                    potentialPlacements.add(currentPos.immutable());
                }
            } else if (geodeDensity >= middleThreshold) {
                this.safeSetBlock(level, currentPos, blockSettings.middleLayerProvider.getState(random, currentPos), replacePredicate);
            } else if (geodeDensity >= outerThreshold) {
                this.safeSetBlock(level, currentPos, blockSettings.outerLayerProvider.getState(random, currentPos), replacePredicate);
            }
        }

        List<BlockState> innerPlacements = blockSettings.innerPlacements;

        for (BlockPos placementPos : potentialPlacements) {
            BlockState placementState = Util.getRandom(innerPlacements, random);

            for (Direction direction : DIRECTIONS) {
                BlockState orientedState = placementState;

                if (orientedState.hasProperty(BlockStateProperties.FACING)) {
                    orientedState = orientedState.setValue(BlockStateProperties.FACING, direction);
                }

                BlockPos targetPos = placementPos.relative(direction);
                BlockState targetState = level.getBlockState(targetPos);

                if (orientedState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    orientedState = orientedState.setValue(
                            BlockStateProperties.WATERLOGGED,
                            targetState.getFluidState().isSource()
                    );
                }

                if (BuddingAmethystBlock.canClusterGrowAtState(targetState)) {
                    this.safeSetBlock(level, targetPos, orientedState, replacePredicate);
                    break;
                }
            }
        }

        return true;
    }
}