package net.sievert.jolcraft.world.block.custom.brewing;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FermentingCauldronBlock extends LayeredCauldronBlock implements EntityBlock {

    public FermentingCauldronBlock(
            Biome.Precipitation precipitationType,
            CauldronInteraction.InteractionMap interactions,
            Properties properties
    ) {
        super(precipitationType, interactions, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 3));
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
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FermentingCauldronBlockEntity cauldron) {
            return cauldron.handleInteraction(player, hand, stack);
        }

        JolCraftLogs.warn(
                JolCraftLogTags.BLOCK,
                "FermentingCauldron at {} has missing/wrong BlockEntity (found={})",
                JolCraftLogs.roundedPos(pos),
                be == null ? "null" : be.getClass().getName()
        );

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public @Nullable FermentingCauldronBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FermentingCauldronBlockEntity(pos, state);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == JolCraftBlockEntities.FERMENTING_CAULDRON.get()
                ? (BlockEntityTicker<T>) TickingBlockEntity.tickOnServer()
                : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(Items.CAULDRON);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}