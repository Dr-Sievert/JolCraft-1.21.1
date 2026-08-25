package net.sievert.jolcraft.world.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.custom.MortarBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MortarBlock extends BaseEntityBlock {

    public static final MapCodec<MortarBlock> CODEC =
            simpleCodec(MortarBlock::new);

    private static final VoxelShape SHAPE =
            Block.box(
                    2.0D,
                    0.0D,
                    2.0D,
                    14.0D,
                    5.0D,
                    14.0D
            );

    public MortarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends MortarBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return new MortarBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!level.isClientSide) {
            BlockEntity blockEntity =
                    level.getBlockEntity(pos);

            if (blockEntity instanceof MortarBlockEntity mortar) {
                player.openMenu(mortar);
            } else {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK,
                        "Mortar at {} has missing/wrong BlockEntity (found={})",
                        JolCraftLogs.roundedPos(pos),
                        blockEntity == null
                                ? "null"
                                : blockEntity.getClass().getName()
                );
            }
        }

        return InteractionResult.SUCCESS;
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
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity =
                    level.getBlockEntity(pos);

            if (blockEntity instanceof MortarBlockEntity mortar) {
                Containers.dropContents(
                        level,
                        pos,
                        mortar
                );

                level.updateNeighbourForOutputSignal(
                        pos,
                        this
                );
            }
        }

        super.onRemove(
                state,
                level,
                pos,
                newState,
                movedByPiston
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}