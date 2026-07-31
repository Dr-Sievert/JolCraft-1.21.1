package net.sievert.jolcraft.world.block.custom.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.world.worldgen.feature.JolCraftConfiguredFeatures;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FesterlingBlock extends MushroomBlock {

    private static final VoxelShape SHAPE =
            Block.box(
                    6.0,
                    0.0,
                    6.0,
                    10.0,
                    6.0,
                    10.0
            );

    public FesterlingBlock(BlockBehaviour.Properties properties) {
        super(
                JolCraftConfiguredFeatures.HUGE_FESTERLING_KEY,
                properties
        );
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(
            BlockState state,
            LevelReader level,
            BlockPos pos
    ) {
        return isValidSubstrate(
                level.getBlockState(pos.below())
        );
    }

    private static boolean isValidSubstrate(BlockState state) {
        if (state.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        }

        return state.is(BlockTags.LOGS)
                && (!state.hasProperty(BlockStateProperties.AXIS)
                || state.getValue(BlockStateProperties.AXIS)
                == Direction.Axis.Y);
    }
}