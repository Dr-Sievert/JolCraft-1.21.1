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

public final class HugeDuskcapFeature extends AbstractHugeMushroomFeature {

    private static final int TREE_HEIGHT = 7;

    private static final int OUTER_CAP_RADIUS = 2;
    private static final int UPPER_CAP_RADIUS = 1;

    private static final int LOWER_CAP_BOTTOM_OFFSET = 5;
    private static final int LOWER_CAP_TOP_OFFSET = 2;
    private static final int UPPER_CAP_OFFSET = 1;

    public HugeDuskcapFeature(
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
            int unusedFoliageRadius,
            int y
    ) {
        if (y >= treeHeight - LOWER_CAP_BOTTOM_OFFSET
                && y <= treeHeight - LOWER_CAP_TOP_OFFSET) {
            return OUTER_CAP_RADIUS;
        }

        if (y == treeHeight - UPPER_CAP_OFFSET) {
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
        int lowerCapBottom =
                treeHeight - LOWER_CAP_BOTTOM_OFFSET;

        int lowerCapTop =
                treeHeight - LOWER_CAP_TOP_OFFSET;

        for (int y = lowerCapBottom; y <= lowerCapTop; y++) {
            boolean bottomLayer =
                    y == lowerCapBottom;

            placeCapRing(
                    level,
                    random,
                    origin,
                    mutablePos,
                    config,
                    y,
                    OUTER_CAP_RADIUS,
                    bottomLayer,
                    y == lowerCapTop,
                    bottomLayer
            );
        }

        placeCapRing(
                level,
                random,
                origin,
                mutablePos,
                config,
                treeHeight - UPPER_CAP_OFFSET,
                UPPER_CAP_RADIUS,
                true,
                true,
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
            BlockState tipState = applyFaces(
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
            );

            this.setBlock(
                    level,
                    mutablePos,
                    tipState
            );
        }
    }

    private void placeCapRing(
            @NotNull LevelAccessor level,
            @NotNull RandomSource random,
            @NotNull BlockPos origin,
            @NotNull BlockPos.MutableBlockPos mutablePos,
            @NotNull HugeMushroomFeatureConfiguration config,
            int y,
            int radius,
            boolean includeCorners,
            boolean up,
            boolean down
    ) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (!isRingBlock(
                        x,
                        z,
                        radius,
                        includeCorners
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

                boolean corner =
                        Math.abs(x) == radius
                                && Math.abs(z) == radius;

                BlockState capState = applyFaces(
                        config.capProvider.getState(
                                random,
                                mutablePos
                        ),
                        !occupiesLayer(
                                x - 1,
                                z,
                                radius,
                                includeCorners
                        ),
                        !occupiesLayer(
                                x + 1,
                                z,
                                radius,
                                includeCorners
                        ),
                        !occupiesLayer(
                                x,
                                z - 1,
                                radius,
                                includeCorners
                        ),
                        !occupiesLayer(
                                x,
                                z + 1,
                                radius,
                                includeCorners
                        ),
                        up || corner,
                        down
                );

                this.setBlock(
                        level,
                        mutablePos,
                        capState
                );
            }
        }
    }

    private static boolean occupiesLayer(
            int x,
            int z,
            int radius,
            boolean includeCorners
    ) {
        if (x == 0 && z == 0) {
            return true;
        }

        return isRingBlock(
                x,
                z,
                radius,
                includeCorners
        );
    }

    private static boolean isRingBlock(
            int x,
            int z,
            int radius,
            boolean includeCorners
    ) {
        int absoluteX = Math.abs(x);
        int absoluteZ = Math.abs(z);

        if (absoluteX > radius
                || absoluteZ > radius) {
            return false;
        }

        boolean xEdge =
                absoluteX == radius;

        boolean zEdge =
                absoluteZ == radius;

        if (!xEdge && !zEdge) {
            return false;
        }

        return includeCorners
                || !xEdge
                || !zEdge;
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