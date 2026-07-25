package net.sievert.jolcraft.world.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;

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
                FluidUtil.getFluidContained(stack)
                        .filter(fluid -> fluid.is(
                                JolCraftFluids.DWARVEN_BREW.get()
                        ))
                        .orElse(FluidStack.EMPTY);

        if (brew.isEmpty()
                || brew.getAmount() < FluidType.BUCKET_VOLUME
                || getBrewAge(brew) > 0L) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        brew = brew.copyWithAmount(
                FluidType.BUCKET_VOLUME
        );

        BlockState fermentingCauldron =
                JolCraftBlocks.FERMENTING_CAULDRON.get()
                        .defaultBlockState()
                        .setValue(
                                LayeredCauldronBlock.LEVEL,
                                3
                        );

        level.setBlockAndUpdate(
                pos,
                fermentingCauldron
        );

        BlockEntity blockEntity =
                level.getBlockEntity(pos);

        if (!(blockEntity instanceof FermentingCauldronBlockEntity cauldron)) {
            level.setBlockAndUpdate(
                    pos,
                    Blocks.CAULDRON.defaultBlockState()
            );

            return ItemInteractionResult.FAIL;
        }

        int inserted =
                cauldron.getBrewFluidHandler()
                        .fill(
                                brew,
                                IFluidHandler.FluidAction.EXECUTE
                        );

        if (inserted != FluidType.BUCKET_VOLUME) {
            level.setBlockAndUpdate(
                    pos,
                    Blocks.CAULDRON.defaultBlockState()
            );

            return ItemInteractionResult.FAIL;
        }

        if (!player.isCreative()) {
            player.setItemInHand(
                    hand,
                    new ItemStack(Items.BUCKET)
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

        return ItemInteractionResult.SUCCESS;
    }

    private static long getBrewAge(
            FluidStack brew
    ) {
        return brew.getOrDefault(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );
    }
}