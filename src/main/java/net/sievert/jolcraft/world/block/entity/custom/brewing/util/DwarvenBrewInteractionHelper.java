package net.sievert.jolcraft.world.block.entity.custom.brewing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemHelper;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public final class DwarvenBrewInteractionHelper {

    private DwarvenBrewInteractionHelper() {}

    /**
     * Extracts one mug-sized quantity from the supplied fluid handler.
     *
     * The caller is responsible for confirming that mug extraction is
     * currently allowed, such as requiring finished brew in a cauldron.
     *
     * Creative players receive a filled mug without draining the handler.
     */
    public static ItemInteractionResult tryExtractMug(
            Player player,
            InteractionHand hand,
            ItemStack emptyMug,
            IFluidHandler fluidHandler,
            int storedAmount
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return ItemInteractionResult.FAIL;
        }

        int drainAmount = DwarvenBrewFluidHelper.getMugDrainAmount(
                storedAmount
        );

        if (drainAmount <= 0) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack simulated = fluidHandler.drain(
                drainAmount,
                IFluidHandler.FluidAction.SIMULATE
        );

        if (simulated.isEmpty()
                || simulated.getAmount() != drainAmount) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack drained = fluidHandler.drain(
                drainAmount,
                player.isCreative()
                        ? IFluidHandler.FluidAction.SIMULATE
                        : IFluidHandler.FluidAction.EXECUTE
        );

        if (drained.isEmpty()
                || drained.getAmount() != drainAmount) {
            return ItemInteractionResult.FAIL;
        }

        ItemStack output =
                DwarvenBrewFluidHelper.createBrewMug(
                        drained
                );

        player.awardStat(
                Stats.ITEM_USED.get(
                        emptyMug.getItem()
                )
        );

        JolCraftItemHelper.consume(
                serverPlayer,
                hand
        );

        JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                serverPlayer,
                output
        );

        PlaySound.bottleFill(
                player,
                0.8F,
                0.9F
        );

        return ItemInteractionResult.SUCCESS;
    }


    public static ItemInteractionResult tryInteractFluidContainer(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack usedItem,
            IFluidHandler fluidHandler,
            boolean hasExtractableFluid
    ) {
        if (level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }

        if (usedItem.is(
                JolCraftItems.GLASS_MUG.get()
        )) {
            if (!hasExtractableFluid) {
                return ItemInteractionResult.FAIL;
            }

            return tryExtractMug(
                    player,
                    hand,
                    usedItem,
                    fluidHandler,
                    fluidHandler.getFluidInTank(
                            0
                    ).getAmount()
            );
        }

        if (usedItem.is(
                JolCraftItems.DWARVEN_BREW.get()
        )) {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return ItemInteractionResult.FAIL;
            }

            int storedAmount =
                    fluidHandler.getFluidInTank(
                            0
                    ).getAmount();

            int fillAmount =
                    DwarvenBrewFluidHelper.getMugFillAmount(
                            storedAmount,
                            fluidHandler.getTankCapacity(
                                    0
                            )
                    );

            if (fillAmount <= 0) {
                return ItemInteractionResult.FAIL;
            }

            FluidStack incoming =
                    DwarvenBrewFluidHelper.createBrewFluidFromMug(
                            usedItem,
                            fillAmount
                    );

            int simulated =
                    fluidHandler.fill(
                            incoming,
                            IFluidHandler.FluidAction.SIMULATE
                    );

            if (simulated != fillAmount) {
                return ItemInteractionResult.FAIL;
            }

            int inserted =
                    fluidHandler.fill(
                            incoming,
                            IFluidHandler.FluidAction.EXECUTE
                    );

            if (inserted != fillAmount) {
                return ItemInteractionResult.FAIL;
            }

            player.awardStat(
                    Stats.ITEM_USED.get(
                            usedItem.getItem()
                    )
            );

            JolCraftItemHelper.consume(
                    serverPlayer,
                    hand
            );

            JolCraftSoundHelper.block(
                    level,
                    pos,
                    SoundEvents.BOTTLE_EMPTY,
                    0.8F,
                    0.9F
            );

            return ItemInteractionResult.SUCCESS;
        }

        if (player.isCreative()
                && hasExtractableFluid) {
            ItemInteractionResult creativeResult =
                    tryCreativeExtraction(
                            level,
                            pos,
                            player,
                            usedItem,
                            fluidHandler
                    );

            if (creativeResult
                    == ItemInteractionResult.SUCCESS) {
                return creativeResult;
            }
        }

        if (!FluidUtil.interactWithFluidHandler(
                player,
                hand,
                fluidHandler
        )) {
            return ItemInteractionResult.FAIL;
        }

        player.awardStat(
                Stats.ITEM_USED.get(
                        usedItem.getItem()
                )
        );

        return ItemInteractionResult.SUCCESS;
    }

    private static ItemInteractionResult tryCreativeExtraction(
            Level level,
            BlockPos pos,
            Player player,
            ItemStack usedItem,
            IFluidHandler fluidHandler
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return ItemInteractionResult.FAIL;
        }

        FluidActionResult result = FluidUtil.tryFillContainer(
                usedItem,
                fluidHandler,
                Integer.MAX_VALUE,
                player,
                false
        );

        if (!result.isSuccess()) {
            return ItemInteractionResult.FAIL;
        }

        JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                serverPlayer,
                result.getResult()
        );

        player.awardStat(
                Stats.ITEM_USED.get(
                        usedItem.getItem()
                )
        );

        JolCraftSoundHelper.block(
                level,
                pos,
                SoundEvents.BUCKET_FILL,
                1.0F,
                1.0F
        );

        return ItemInteractionResult.SUCCESS;
    }
}