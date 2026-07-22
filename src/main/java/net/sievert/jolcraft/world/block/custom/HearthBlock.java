package net.sievert.jolcraft.world.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.world.player.attachment.custom.hearth.HearthAttachmentHelper;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.sievert.jolcraft.world.block.entity.custom.HearthBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HearthBlock extends BaseEntityBlock {

    public static final MapCodec<HearthBlock> CODEC = simpleCodec(HearthBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public HearthBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(HALF, DoubleBlockHalf.LOWER)
                        .setValue(LIT, false)
        );
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!(level instanceof ServerLevel)) return ItemInteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return ItemInteractionResult.SUCCESS;

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return ItemInteractionResult.FAIL;
        }

        if (hit.getDirection() != state.getValue(FACING)) {
            return ItemInteractionResult.FAIL;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof HearthBlockEntity hearth)) {
            return ItemInteractionResult.FAIL;
        }

        return hearth.handleUse(stack, state, serverPlayer, hand);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) return;
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) return;

        double x = pos.getX() + 0.5D;
        double y = pos.getY();
        double z = pos.getZ() + 0.5D;

        if (random.nextDouble() < 0.1D) {
            level.playLocalSound(
                    x, y, z,
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    0.5F,
                    0.8F,
                    false
            );
        }

        Direction facing = state.getValue(FACING);
        Direction.Axis axis = facing.getAxis();
        double lateral = random.nextDouble() * 0.6D - 0.3D;

        double dx = axis == Direction.Axis.X ? facing.getStepX() * 0.52D : lateral;
        double dz = axis == Direction.Axis.Z ? facing.getStepZ() * 0.52D : lateral;
        double dy = random.nextDouble() * 6.0D / 16.0D;

        level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0D, 0.0D, 0.0D);

        BlockState above = level.getBlockState(pos.above());
        if (above.getBlock() == state.getBlock() && above.getValue(HALF) == DoubleBlockHalf.UPPER) {

            BlockPos chimneyTop = getChimneyTop(level, pos);

            double cx = chimneyTop.getX() + 0.5D;
            double cy = chimneyTop.getY() + 0.85D;
            double cz = chimneyTop.getZ() + 0.5D;

            int count = 2 + random.nextInt(2);
            for (int i = 0; i < count; i++) {
                double ox = random.nextGaussian() * 0.06D;
                double oz = random.nextGaussian() * 0.06D;

                level.addParticle(
                        ParticleTypes.SMOKE,
                        cx + ox,
                        cy,
                        cz + oz,
                        0.0D,
                        0.06D + random.nextDouble() * 0.02D,
                        0.0D
                );
            }
        }
    }

    private static BlockPos getChimneyTop(Level level, BlockPos lowerPos) {
        BlockPos topPos = lowerPos.above();
        BlockPos cursor = topPos.above();

        while (level.getBlockState(cursor).is(Blocks.DEEPSLATE_TILE_WALL)) {
            topPos = cursor;
            cursor = cursor.above();
        }

        return topPos;
    }

    @Override
    protected MapCodec<? extends HearthBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, LIT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        if (half == DoubleBlockHalf.LOWER) {
            return Block.box(0, 0, 0, 16, 16, 16);
        } else {
            return Block.box(4, 0, 4, 12, 16, 12);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        int minY = level.dimensionType().minY();
        int maxY = minY + level.dimensionType().height();
        if (pos.getY() < maxY - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(LIT, false);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            preventDropFromBottomPart(level, pos, state, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();

        if (level instanceof ServerLevel serverLevel) {
            BlockEntity be = level.getBlockEntity(lowerPos);
            if (be instanceof HearthBlockEntity hearth && hearth.getOwner() != null) {
                ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(hearth.getOwner());
                if (HearthAttachmentHelper.isActiveHearth(player, lowerPos)) {
                    HearthAttachmentHelper.clearActiveHearthPos(player);
                }
            }
        }

        BlockPos otherPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        BlockState otherState = level.getBlockState(otherPos);

        if (otherState.is(this) && otherState.getValue(HALF) != state.getValue(HALF)) {
            level.removeBlock(otherPos, false);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    public static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(state.getBlock()) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState replacement = belowState.getFluidState().is(Fluids.WATER)
                        ? Blocks.WATER.defaultBlockState()
                        : Blocks.AIR.defaultBlockState();
                level.setBlock(below, replacement, 35);
                level.levelEvent(player, 2001, below, Block.getId(belowState));
            }
        }
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction dir,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        DoubleBlockHalf half = state.getValue(HALF);

        if (dir.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (dir == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && dir == Direction.DOWN && !state.canSurvive(level, pos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, dir, neighborState, level, pos, neighborPos);
        } else {
            return neighborState.is(this) && neighborState.getValue(HALF) != half
                    ? state
                    : Blocks.AIR.defaultBlockState();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HearthBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, JolCraftBlockEntities.HEARTH.get(), TickingBlockEntity.tickOnServer());
    }
}
