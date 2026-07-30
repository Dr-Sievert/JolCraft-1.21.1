package net.sievert.jolcraft.world.block.custom.brewing;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingBarrelBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A vanilla barrel replacement that stores and ages dwarven brew.
 */
@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingBarrelBlock extends BaseEntityBlock {

    public static final MapCodec<FermentingBarrelBlock> CODEC = simpleCodec(FermentingBarrelBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public FermentingBarrelBlock(
            Properties properties
    ) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * Delegates item interactions to the fermenting barrel block entity.
     */
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
        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof FermentingBarrelBlockEntity barrel) {
            if (level.isClientSide()) {
                return barrel.getInteractionResult(
                        hand,
                        stack
                );
            }

            return barrel.handleInteraction(
                    player,
                    hand,
                    stack
            );
        }

        if (!level.isClientSide()) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK,
                    "FermentingBarrel at {} has missing/wrong BlockEntity (found={})",
                    JolCraftLogs.roundedPos(pos),
                    blockEntity == null
                            ? "null"
                            : blockEntity.getClass().getName()
            );
        }

        return ItemInteractionResult.FAIL;
    }

    /**
     * Allows players to inspect the current brew age with an empty-hand interaction.
     */
    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof FermentingBarrelBlockEntity barrel)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return barrel.hasBrew()
                    ? InteractionResult.SUCCESS
                    : InteractionResult.PASS;
        }

        return barrel.inspectBrewAge(player)
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(
            BlockPlaceContext context
    ) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(
            BlockState state,
            Rotation rotation
    ) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(
            BlockState state,
            Mirror mirror
    ) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING);
    }

    @Override
    public @NotNull FermentingBarrelBlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return new FermentingBarrelBlockEntity(pos, state);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return null;
        }

        return type == JolCraftBlockEntities.FERMENTING_BARREL.get() ? (BlockEntityTicker<T>) TickingBlockEntity.tickOnServer() : null;
    }

    /**
     * Returns a vanilla barrel when the block is cloned in creative mode.
     */
    @Override
    public ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        return new ItemStack(Blocks.BARREL);
    }

    @Override
    public RenderShape getRenderShape(
            BlockState state
    ) {
        return RenderShape.MODEL;
    }
}