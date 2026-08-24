package net.sievert.jolcraft.world.block.custom.plant.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HopsCropTopBlock extends HopsCropBottomBlock {

    public static final int MAX_AGE = 4;
    public static final IntegerProperty AGE = IntegerProperty.create(
            JolCraftDictionary.AGE,
            0,
            MAX_AGE
    );

    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
    };

    public HopsCropTopBlock(
            Properties properties,
            Supplier<? extends ItemLike> seedItem
    ) {
        super(
                properties,
                seedItem,
                () -> null
        );
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(AGE, 0)
        );
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE_BY_AGE[this.getAge(state)];
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(AGE);
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!this.canSurvive(
                state,
                level,
                pos
        )) {
            level.removeBlock(
                    pos,
                    false
            );
        }
    }

    @Override
    public boolean canSurvive(
            BlockState state,
            LevelReader level,
            BlockPos pos
    ) {
        BlockState bottomState =
                level.getBlockState(pos.below());

        return bottomState.is(JolCraftTags.Blocks.HOPS_BOTTOM)
                && bottomState.getValue(HopsCropBottomBlock.AGE) >= 5;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (direction == Direction.DOWN
                && (!neighborState.is(JolCraftTags.Blocks.HOPS_BOTTOM)
                || neighborState.getValue(HopsCropBottomBlock.AGE) < 5)) {
            level.scheduleTick(
                    pos,
                    this,
                    1
            );
        }

        return state;
    }

    @Override
    public void growCrops(
            Level level,
            BlockPos pos,
            BlockState state
    ) {
        super.growCrops(
                level,
                pos,
                state
        );

        this.syncBottomBlock(
                level,
                pos,
                this.getAge(level.getBlockState(pos))
        );
    }

    private void syncBottomBlock(
            Level level,
            BlockPos pos,
            int topAge
    ) {
        BlockPos bottomPos = pos.below();
        BlockState bottomState =
                level.getBlockState(bottomPos);

        if (bottomState.is(JolCraftTags.Blocks.HOPS_BOTTOM)) {
            int bottomAge = Math.min(
                    topAge + 5,
                    HopsCropBottomBlock.MAX_AGE
            );

            if (bottomState.getValue(HopsCropBottomBlock.AGE)
                    != bottomAge) {
                level.setBlock(
                        bottomPos,
                        bottomState.setValue(
                                HopsCropBottomBlock.AGE,
                                bottomAge
                        ),
                        Block.UPDATE_CLIENTS
                );
            }
        }
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {
        if (!state.is(newState.getBlock())
                && level.getBlockState(pos.below())
                .is(JolCraftTags.Blocks.HOPS_BOTTOM)) {
            level.removeBlock(
                    pos.below(),
                    false
            );
        }

        super.onRemove(
                state,
                level,
                pos,
                newState,
                isMoving
        );
    }
}