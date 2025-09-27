package net.sievert.jolcraft.block.custom.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.data.JolCraftTags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HopsCropTopBlock extends HopsCropBottomBlock {

    public static final int MAX_AGE = 4;
    public static final IntegerProperty TOP_AGE = IntegerProperty.create("top_age", 0, MAX_AGE);

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 0, 0, 16, 5, 16),
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(0, 0, 0, 16, 11, 16),
            Block.box(0, 0, 0, 16, 14, 16)
    };

    public HopsCropTopBlock(Properties properties, Supplier<? extends ItemLike> seedItem) {
        super(properties, seedItem, () -> null); // top block doesn't grow further
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP_AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP_AGE);
    }

    @Override
    public VoxelShape getShape(@NotNull BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(TOP_AGE)];
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return TOP_AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        if (!below.is(JolCraftTags.Blocks.HOPS_BOTTOM) || below.getValue(HopsCropBottomBlock.AGE) < 5) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(JolCraftTags.Blocks.HOPS_BOTTOM) && below.getValue(HopsCropBottomBlock.AGE) >= 5;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticker,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (direction == Direction.DOWN &&
                (!neighborState.is(JolCraftTags.Blocks.HOPS_BOTTOM) || neighborState.getValue(HopsCropBottomBlock.AGE) < 5)) {
            ticker.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, level, ticker, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() instanceof HopsCropBottomBlock;
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        super.growCrops(level, pos, state);
        int newTopAge = level.getBlockState(pos).getValue(TOP_AGE);
        syncBottomBlock(level, pos, newTopAge);
    }

    private void syncBottomBlock(Level level, BlockPos pos, int topAge) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (belowState.is(JolCraftTags.Blocks.HOPS_BOTTOM)) {
            int requiredBottomAge = topAge + 5;
            int clamped = Math.min(requiredBottomAge, HopsCropBottomBlock.MAX_AGE);
            if (belowState.getValue(HopsCropBottomBlock.AGE) != clamped) {
                level.setBlock(below, belowState.setValue(HopsCropBottomBlock.AGE, clamped), 2);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (newState.getBlock() != this) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(JolCraftTags.Blocks.HOPS_BOTTOM)) {
                level.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
            }
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!player.isCreative()) {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
        }
        BlockPos otherHalf = pos.below();
        BlockState otherState = level.getBlockState(otherHalf);
        if (otherState.is(JolCraftTags.Blocks.HOPS_BOTTOM)) {
            level.setBlock(otherHalf, Blocks.AIR.defaultBlockState(), 35);
        }
    }


}
