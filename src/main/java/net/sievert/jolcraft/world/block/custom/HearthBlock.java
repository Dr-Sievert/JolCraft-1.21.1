package net.sievert.jolcraft.world.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.data.attachment.custom.hearth.Hearth;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
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

        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        BlockState otherState = level.getBlockState(otherPos);

        if (otherState.is(this) && otherState.getValue(HALF) != half) {
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
            LevelReader level,
            ScheduledTickAccess tick,
            BlockPos pos,
            Direction dir,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (dir.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (dir == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && dir == Direction.DOWN && !state.canSurvive(level, pos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, level, tick, pos, dir, neighborPos, neighborState, random);
        } else {
            return neighborState.is(this) && neighborState.getValue(HALF) != half
                    ? state
                    : Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            state = level.getBlockState(pos);
            if (!state.is(this)) {
                JolCraftLogs.warn(JolCraftLogTags.BLOCK,
                        "Hearth upper-half used but lower-half missing at {}",
                        pos);
                return InteractionResult.FAIL;
            }
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (state.getValue(LIT)) {
            return InteractionResult.FAIL;
        }

        if (player.isCreative() && !state.getValue(LIT)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HearthBlockEntity hearth) {
                boolean wasNew = hearth.activateFor(player.getUUID());
                if (wasNew) {
                    setLitBoth(level, pos);
                    JolCraftSoundHelper.block(
                            level,
                            pos,
                            SoundEvents.BLAZE_SHOOT,
                            1.0F,
                            0.8F
                    );
                }
            } else {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK,
                        "Hearth at {} has missing/wrong BlockEntity (found={})",
                        pos,
                        (be == null ? "null" : be.getClass().getName())
                );
            }
            return InteractionResult.SUCCESS;
        }

        if (!player.isCreative()) {
            Hearth hearthAttachment = Hearth.get(player);
            if (hearthAttachment.hasLitThisDay()) {
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_COOLDOWN).withStyle(ChatFormatting.GRAY), true
                );
                return InteractionResult.SUCCESS;
            }
        }

        boolean isCoal = stack.is(Items.COAL) || stack.is(Items.CHARCOAL);

        if (!isCoal) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_NEED_COAL).withStyle(ChatFormatting.GRAY), true
            );
            return InteractionResult.SUCCESS;
        }

        boolean monstersNearby = !level.getEntitiesOfClass(
                Monster.class,
                new AABB(
                        pos.getX() + 0.5 - 8, pos.getY() + 0.5 - 5, pos.getZ() + 0.5 - 8,
                        pos.getX() + 0.5 + 8, pos.getY() + 0.5 + 5, pos.getZ() + 0.5 + 8
                ),
                mob -> mob.isPreventingPlayerRest(level instanceof ServerLevel s ? s : null, player)
        ).isEmpty();

        if (monstersNearby) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_NOT_SAFE).withStyle(ChatFormatting.RED), true
            );
            return InteractionResult.SUCCESS;
        }

        boolean bedNearby = false;
        if (player instanceof ServerPlayer serverPlayer) {
            BlockPos bed = serverPlayer.getRespawnPosition();
            if (bed != null && serverPlayer.getRespawnDimension().equals(level.dimension())) {
                double distSq = bed.distSqr(pos);
                if (distSq <= 100) {
                    BlockState bedState = level.getBlockState(bed);
                    if (bedState.getBlock() instanceof BedBlock) {
                        bedNearby = true;
                    }
                }
            }
        }

        if (!bedNearby) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_NO_BED_NEARBY).withStyle(ChatFormatting.GRAY), true
            );
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HearthBlockEntity hearth) {
            boolean wasNew = hearth.activateFor(player.getUUID());
            if (wasNew) {
                setLitBoth(level, pos);
                JolCraftSoundHelper.block(
                        level,
                        pos,
                        SoundEvents.BLAZE_SHOOT,
                        1.0F,
                        0.8F
                );
                if (!player.isCreative()) {
                    Hearth.get(player).setHasLitThisDay(true);
                    stack.shrink(1);
                }
            }
        } else {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK,
                    "Hearth at {} has missing/wrong BlockEntity (found={})",
                    pos,
                    (be == null ? "null" : be.getClass().getName())
            );
        }

        return InteractionResult.SUCCESS;
    }

    private static void setLitBoth(Level level, BlockPos lowerPos) {
        BlockState lower = level.getBlockState(lowerPos);
        if (!(lower.getBlock() instanceof HearthBlock)) return;
        if (lower.getValue(HALF) != DoubleBlockHalf.LOWER) return;

        if (!lower.getValue(LIT)) {
            level.setBlock(lowerPos, lower.setValue(LIT, true), 3);
        }

        BlockPos upperPos = lowerPos.above();
        BlockState upper = level.getBlockState(upperPos);
        if (upper.is(lower.getBlock()) && upper.getValue(HALF) == DoubleBlockHalf.UPPER) {
            if (!upper.getValue(LIT)) {
                level.setBlock(upperPos, upper.setValue(LIT, true), 3);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) return;

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
            double cx = pos.getX() + 0.5D;
            double cy = pos.getY() + 1.85D;
            double cz = pos.getZ() + 0.5D;

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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HearthBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, JolCraftBlockEntities.HEARTH.get(), (lvl, pos, st, be) -> {
            be.tick();
        });
    }
}
