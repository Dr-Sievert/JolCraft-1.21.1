package net.sievert.jolcraft.world.block.custom.brewing;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A vanilla cauldron replacement that creates dwarven brew.
 */
@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingCauldronBlock extends AbstractCauldronBlock implements EntityBlock {

    public static final MapCodec<FermentingCauldronBlock> CODEC = simpleCodec(FermentingCauldronBlock::new);

    private static final float MIN_CONTENT_HEIGHT = 6.0F / 16.0F;
    private static final float MAX_CONTENT_HEIGHT = 15.0F / 16.0F;

    private static final int EXTINGUISH_DRAIN_AMOUNT = Mth.ceil(FluidType.BUCKET_VOLUME / 3.0F);

    public FermentingCauldronBlock(
            Properties properties
    ) {
        this(CauldronInteraction.EMPTY, properties);
    }

    public FermentingCauldronBlock(
            CauldronInteraction.InteractionMap interactions,
            Properties properties
    ) {
        super(properties, interactions);
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> codec() {
        return CODEC;
    }


    /**
     * Delegates item interactions to the fermenting cauldron block entity.
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
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof FermentingCauldronBlockEntity cauldron) {
            return cauldron.handleInteraction(
                    player,
                    hand,
                    stack
            );
        }

        JolCraftLogs.warn(
                JolCraftLogTags.BLOCK,
                "FermentingCauldron at {} has missing/wrong BlockEntity (found={})",
                JolCraftLogs.roundedPos(pos),
                blockEntity == null ? "null" : blockEntity.getClass().getName()
        );

        return ItemInteractionResult.SUCCESS;
    }


    /**
     * Always reports the cauldron as full since the backing fluid tank, rather than block state, determines the stored brew amount.
     */
    @Override
    public boolean isFull(
            BlockState state
    ) {
        return true;
    }

    /**
     * Outputs a comparator strength based on the current brew volume.
     */
    @Override
    protected int getAnalogOutputSignal(
            BlockState state,
            Level level,
            BlockPos pos
    ) {
        if (!(level.getBlockEntity(pos) instanceof FermentingCauldronBlockEntity cauldron)) {
            return 0;
        }

        int amount = cauldron.getBrewAmount();

        if (amount <= 0) {
            return 0;
        }

        return Mth.clamp(
                Mth.ceil(amount * 3.0F / FluidType.BUCKET_VOLUME),
                1,
                3
        );
    }

    @Override
    public @NotNull FermentingCauldronBlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return new FermentingCauldronBlockEntity(pos, state);
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

        return type == JolCraftBlockEntities.FERMENTING_CAULDRON.get() ? (BlockEntityTicker<T>) TickingBlockEntity.tickOnServer() : null;
    }

    /**
     * Returns a vanilla cauldron when the block is cloned in creative mode.
     */
    @Override
    public ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        return new ItemStack(Items.CAULDRON);
    }

    @Override
    public RenderShape getRenderShape(
            BlockState state
    ) {
        return RenderShape.MODEL;
    }
}