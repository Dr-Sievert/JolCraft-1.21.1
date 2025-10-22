package net.sievert.jolcraft.block.custom;

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
import net.sievert.jolcraft.data.custom.attachment.hearth.Hearth;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import org.jetbrains.annotations.Nullable;
import net.sievert.jolcraft.block.entity.custom.HearthBlockEntity;

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
        int maxBuildY = level.dimensionType().logicalHeight();
        if (pos.getY() < maxBuildY - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
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
                return InteractionResult.FAIL;
            }
        }

        if (state.getValue(LIT)) {
            return InteractionResult.FAIL;
        }

        if (player.isCreative() && !state.getValue(LIT)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HearthBlockEntity hearth) {
                boolean wasNew = hearth.activateFor(player.getUUID());
                if (wasNew) {
                    level.setBlock(pos, state.setValue(LIT, true), 3);
                    level.playSound(null, pos, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 0.8F);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!player.isCreative()) {
            Hearth hearthAttachment = Hearth.get(player);
            if (hearthAttachment.hasLitThisDay()) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("tooltip.jolcraft.hearth.cooldown").withStyle(ChatFormatting.GRAY), true
                );
                return InteractionResult.SUCCESS;
            }
        }

        boolean isCoal = stack.is(Items.COAL) || stack.is(Items.CHARCOAL);

        if (!isCoal) {
            System.out.println("Held item: " + stack);
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.hearth.need_coal").withStyle(ChatFormatting.GRAY), true
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
                    net.minecraft.network.chat.Component.translatable("tooltip.jolcraft.hearth.not_safe").withStyle(ChatFormatting.RED), true
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
                    net.minecraft.network.chat.Component.translatable("tooltip.jolcraft.hearth.no_bed_nearby").withStyle(ChatFormatting.GRAY), true
            );
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HearthBlockEntity hearth) {
            boolean wasNew = hearth.activateFor(player.getUUID());
            if (wasNew) {
                level.setBlock(pos, state.setValue(LIT, true), 3);
                level.playSound(null, pos, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 0.8F);
                if (!player.isCreative()) {
                    Hearth.get(player).setLitThisDay(true);
                    stack.shrink(1);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double d0 = pos.getX() + 0.5;
            double d1 = pos.getY();
            double d2 = pos.getZ() + 0.5;
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.5F, 0.8F, false);
            }

            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double d4 = random.nextDouble() * 0.6 - 0.3;
            double d5 = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : d4;
            double d6 = random.nextDouble() * 6.0 / 16.0;
            double d7 = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : d4;
            level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0);

            BlockState above = level.getBlockState(pos.above());
            if (above.getBlock() == state.getBlock() && above.getValue(HALF) == DoubleBlockHalf.UPPER) {
                double cx = pos.getX() + 0.5;
                double cy = pos.getY() + 1.85;
                double cz = pos.getZ() + 0.5;

                for (int i = 0; i < 2 + random.nextInt(2); i++) {
                    double ox = random.nextGaussian() * 0.06;
                    double oz = random.nextGaussian() * 0.06;
                    level.addParticle(ParticleTypes.SMOKE, cx + ox, cy, cz + oz, 0.0, 0.06 + random.nextDouble() * 0.02, 0.0);
                }
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
        if (type == JolCraftBlockEntities.HEARTH.get()) {
            return (lvl, pos, st, blockEntity) -> {
                if (lvl instanceof ServerLevel serverLevel) {
                    if (blockEntity instanceof HearthBlockEntity hearth) {
                        hearth.tick();
                    }
                }
            };
        }
        return null;
    }

}
