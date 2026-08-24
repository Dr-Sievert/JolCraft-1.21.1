package net.sievert.jolcraft.world.block.custom.plant;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BloodrootBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {

    public static final MapCodec<BloodrootBlock> CODEC = simpleCodec(BloodrootBlock::new);

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final float SPREAD_CHANCE = 0.20F;

    protected static final VoxelShape SHAPE = Block.box(
            1.0,
            7.0,
            1.0,
            15.0,
            16.0,
            15.0
    );

    public BloodrootBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        return aboveState.isFaceSturdy(level, above, Direction.DOWN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        if (state == null) {
            return null;
        }

        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

        return state.setValue(
                WATERLOGGED,
                fluidState.getType() == Fluids.WATER
        );
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
        if (direction == Direction.UP && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(
                    pos,
                    Fluids.WATER,
                    Fluids.WATER.getTickDelay(level)
            );
        }

        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos
        );
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        if (!hasValidSpreadCeiling(level, pos)) {
            return false;
        }

        return !getValidSpreadPositions(level, pos).isEmpty();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < SPREAD_CHANCE;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (!hasValidSpreadCeiling(level, pos)) {
            return;
        }

        List<BlockPos> validPositions = getValidSpreadPositions(level, pos);

        if (validPositions.isEmpty()) {
            return;
        }

        BlockPos target = validPositions.get(random.nextInt(validPositions.size()));
        FluidState fluidState = level.getFluidState(target);

        level.setBlock(
                target,
                defaultBlockState().setValue(
                        WATERLOGGED,
                        fluidState.getType() == Fluids.WATER
                ),
                Block.UPDATE_ALL
        );
    }

    private List<BlockPos> getValidSpreadPositions(LevelReader level, BlockPos origin) {
        List<BlockPos> positions = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }

                BlockPos target = origin.offset(x, 0, z);

                if (canSpreadTo(level, target)) {
                    positions.add(target);
                }
            }
        }

        return positions;
    }

    private boolean canSpreadTo(LevelReader level, BlockPos pos) {
        BlockState targetState = level.getBlockState(pos);

        if (!targetState.isAir() && targetState.getFluidState().getType() != Fluids.WATER) {
            return false;
        }

        return hasValidSpreadCeiling(level, pos)
                && defaultBlockState().canSurvive(level, pos);
    }

    private boolean hasValidSpreadCeiling(LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        return mayPlaceOn(level.getBlockState(above), level, above);
    }

    /**
     * Returns the item when the block is cloned in creative mode.
     */
    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        return new ItemStack(JolCraftItems.BLOODROOT.asItem());
    }
}