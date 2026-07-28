package net.sievert.jolcraft.world.block.fluid.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.JolCraftItems;

public final class JolCraftCauldronInteractions {

    private JolCraftCauldronInteractions() {}

    public static void register() {
        CauldronInteraction.EMPTY.map().put(
                JolCraftItems.DWARVEN_BREW_BUCKET.get(),
                JolCraftCauldronInteractions::fillWithBrew
        );
    }

    private static ItemInteractionResult fillWithBrew(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack stack
    ) {
        FluidStack brew =
                FluidUtil.getFluidContained(
                                stack
                        )
                        .filter(
                                fluid -> fluid.is(
                                        JolCraftFluids.DWARVEN_BREW.get()
                                )
                        )
                        .orElse(
                                FluidStack.EMPTY
                        );

        if (brew.getAmount()
                < FluidType.BUCKET_VOLUME) {
            return ItemInteractionResult
                    .PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockState fermentingCauldron =
                JolCraftBlocks.FERMENTING_CAULDRON.get()
                        .defaultBlockState();

        level.setBlockAndUpdate(
                pos,
                fermentingCauldron
        );

        BlockEntity blockEntity =
                level.getBlockEntity(
                        pos
                );

        if (!(blockEntity instanceof
                FermentingCauldronBlockEntity cauldron)) {
            level.setBlockAndUpdate(
                    pos,
                    Blocks.CAULDRON.defaultBlockState()
            );

            return ItemInteractionResult.FAIL;
        }

        int inserted =
                cauldron.getBrewFluidHandler()
                        .fill(
                                brew.copyWithAmount(
                                        FluidType.BUCKET_VOLUME
                                ),
                                IFluidHandler.FluidAction.EXECUTE
                        );

        if (inserted != FluidType.BUCKET_VOLUME) {
            level.setBlockAndUpdate(
                    pos,
                    Blocks.CAULDRON.defaultBlockState()
            );

            return ItemInteractionResult.FAIL;
        }

        player.awardStat(
                Stats.FILL_CAULDRON
        );

        player.awardStat(
                Stats.ITEM_USED.get(
                        stack.getItem()
                )
        );

        if (!player.isCreative()) {
            player.setItemInHand(
                    hand,
                    new ItemStack(
                            Items.BUCKET
                    )
            );
        }

        level.playSound(
                null,
                pos,
                SoundEvents.BUCKET_EMPTY,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        level.gameEvent(
                null,
                GameEvent.FLUID_PLACE,
                pos
        );

        return ItemInteractionResult.SUCCESS;
    }
}