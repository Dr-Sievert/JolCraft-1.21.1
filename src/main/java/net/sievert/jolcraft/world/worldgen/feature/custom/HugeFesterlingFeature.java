package net.sievert.jolcraft.world.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import org.jetbrains.annotations.NotNull;

public final class HugeFesterlingFeature extends AbstractHugeMushroomFeature {

    private static final int TREE_HEIGHT = 3;
    private static final int LOWER_CAP_RADIUS = 2;
    private static final int UPPER_CAP_RADIUS = 1;

    public HugeFesterlingFeature(
            @NotNull Codec<HugeMushroomFeatureConfiguration> codec
    ) {
        super(codec);
    }

    @Override
    protected int getTreeHeight(@NotNull RandomSource random) {
        return TREE_HEIGHT;
    }

    @Override
    protected int getTreeRadiusForHeight(
            int unused,
            int treeHeight,
            int foliageRadius,
            int y
    ) {
        if (y == treeHeight - 2) {
            return LOWER_CAP_RADIUS;
        }

        if (y == treeHeight - 1) {
            return UPPER_CAP_RADIUS;
        }

        return 0;
    }

    @Override
    protected void placeTrunk(
            @NotNull LevelAccessor level,
            @NotNull RandomSource random,
            @NotNull BlockPos origin,
            @NotNull HugeMushroomFeatureConfiguration config,
            int treeHeight,
            @NotNull BlockPos.MutableBlockPos mutablePos
    ) {
        for (int y = 0; y < treeHeight; y++) {
            mutablePos.setWithOffset(
                    origin,
                    0,
                    y,
                    0
            );

            if (level.getBlockState(mutablePos).isSolidRender(
                    level,
                    mutablePos
            )) {
                continue;
            }

            BlockState stemState = config.stemProvider
                    .getState(
                            random,
                            mutablePos
                    )
                    .setValue(
                            HugeMushroomBlock.WEST,
                            true
                    )
                    .setValue(
                            HugeMushroomBlock.EAST,
                            true
                    )
                    .setValue(
                            HugeMushroomBlock.NORTH,
                            true
                    )
                    .setValue(
                            HugeMushroomBlock.SOUTH,
                            true
                    )
                    .setValue(
                            HugeMushroomBlock.UP,
                            y == treeHeight - 1
                    )
                    .setValue(
                            HugeMushroomBlock.DOWN,
                            y == 0
                    );

            this.setBlock(
                    level,
                    mutablePos,
                    stemState
            );
        }
    }

    @Override
    protected void makeCap(
            @NotNull LevelAccessor level,
            @NotNull RandomSource random,
            @NotNull BlockPos origin,
            int treeHeight,
            @NotNull BlockPos.MutableBlockPos mutablePos,
            @NotNull HugeMushroomFeatureConfiguration config
    ) {
        placeCapLayer(
                level,
                random,
                origin,
                mutablePos,
                config,
                treeHeight - 2,
                LOWER_CAP_RADIUS,
                false
        );

        placeCapLayer(
                level,
                random,
                origin,
                mutablePos,
                config,
                treeHeight - 1,
                UPPER_CAP_RADIUS,
                true
        );

        mutablePos.setWithOffset(
                origin,
                0,
                treeHeight,
                0
        );

        if (!level.getBlockState(mutablePos).isSolidRender(
                level,
                mutablePos
        )) {
            this.setBlock(
                    level,
                    mutablePos,
                    applyFaces(
                            config.capProvider.getState(
                                    random,
                                    mutablePos
                            ),
                            true,
                            true,
                            true,
                            true,
                            true,
                            true
                    )
            );
        }
    }

    private void placeCapLayer(
            @NotNull LevelAccessor level,
            @NotNull RandomSource random,
            @NotNull BlockPos origin,
            @NotNull BlockPos.MutableBlockPos mutablePos,
            @NotNull HugeMushroomFeatureConfiguration config,
            int y,
            int radius,
            boolean upperLayer
    ) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (!isCapBlock(
                        x,
                        z,
                        radius,
                        upperLayer
                )) {
                    continue;
                }

                mutablePos.setWithOffset(
                        origin,
                        x,
                        y,
                        z
                );

                if (level.getBlockState(mutablePos).isSolidRender(
                        level,
                        mutablePos
                )) {
                    continue;
                }

                BlockState capState = applyFaces(
                        config.capProvider.getState(
                                random,
                                mutablePos
                        ),
                        !isCapBlock(
                                x - 1,
                                z,
                                radius,
                                upperLayer
                        ),
                        !isCapBlock(
                                x + 1,
                                z,
                                radius,
                                upperLayer
                        ),
                        !isCapBlock(
                                x,
                                z - 1,
                                radius,
                                upperLayer
                        ),
                        !isCapBlock(
                                x,
                                z + 1,
                                radius,
                                upperLayer
                        ),
                        true,
                        true
                );

                this.setBlock(
                        level,
                        mutablePos,
                        capState
                );
            }
        }
    }

    private static boolean isCapBlock(
            int x,
            int z,
            int radius,
            boolean upperLayer
    ) {
        int absoluteX = Math.abs(x);
        int absoluteZ = Math.abs(z);

        if (absoluteX > radius || absoluteZ > radius) {
            return false;
        }

        if (upperLayer) {
            return x != 0 || z != 0;
        }

        boolean xEdge = absoluteX == radius;
        boolean zEdge = absoluteZ == radius;

        return (xEdge || zEdge)
                && !(xEdge && zEdge);
    }

    private static @NotNull BlockState applyFaces(
            @NotNull BlockState state,
            boolean west,
            boolean east,
            boolean north,
            boolean south,
            boolean up,
            boolean down
    ) {
        return state
                .setValue(
                        HugeMushroomBlock.WEST,
                        west
                )
                .setValue(
                        HugeMushroomBlock.EAST,
                        east
                )
                .setValue(
                        HugeMushroomBlock.NORTH,
                        north
                )
                .setValue(
                        HugeMushroomBlock.SOUTH,
                        south
                )
                .setValue(
                        HugeMushroomBlock.UP,
                        up
                )
                .setValue(
                        HugeMushroomBlock.DOWN,
                        down
                );
    }
}