package net.sievert.jolcraft.world.block.fluid.util.brewing;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.item.registry.JolCraftBrewingItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

/**
 * Handles player interactions between brewing fluid tanks and supported
 * fluid containers.
 */
public final class DwarvenBrewInteractionHelper {

    private DwarvenBrewInteractionHelper() {}

    /**
     * Attempts to fill or empty the held container using the supplied
     * brewing fluid handler.
     */
    public static ItemInteractionResult tryInteractFluidContainer(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack usedItem,
            IFluidHandler fluidHandler,
            boolean hasFinishedFluid
    ) {
        FluidStack storedFluid = fluidHandler.getFluidInTank(0);

        if (usedItem.is(
                JolCraftItems.GLASS_MUG.get()
        )) {
            return tryExtract(
                    level,
                    pos,
                    player,
                    hand,
                    usedItem,
                    fluidHandler,
                    JolCraftItems.DWARVEN_BREW.get(),
                    DwarvenBrewFluidHelper.getMugDrainAmount(
                            storedFluid.getAmount()
                    ),
                    hasFinishedFluid,
                    true
            );
        }

        if (usedItem.is(
                JolCraftItems.DWARVEN_BREW.get()
        )) {
            return tryInsertBrewMug(
                    level,
                    pos,
                    player,
                    hand,
                    usedItem,
                    fluidHandler
            );
        }

        if (usedItem.is(
                Items.GLASS_BOTTLE
        )) {
            return tryExtract(
                    level,
                    pos,
                    player,
                    hand,
                    usedItem,
                    fluidHandler,
                    JolCraftItems.YEAST.get(),
                    JolCraftBrewingItems.YEAST_BOTTLE_VOLUME,
                    hasFinishedFluid,
                    false
            );
        }

        if (storedFluid.is(
                JolCraftFluids.YEAST.get()
        )) {
            return ItemInteractionResult.FAIL;
        }

        FluidActionResult fillResult =
                FluidUtil.tryFillContainer(
                        usedItem,
                        fluidHandler,
                        Integer.MAX_VALUE,
                        player,
                        true
                );

        if (fillResult.isSuccess()) {
            giveFilledContainer(
                    player,
                    hand,
                    usedItem,
                    fillResult.getResult()
            );

            player.awardStat(
                    Stats.ITEM_USED.get(
                            usedItem.getItem()
                    )
            );

            return ItemInteractionResult.SUCCESS;
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

    /**
     * Transfers the brew stored in a filled mug into the supplied fluid
     * handler and returns an empty mug to the player.
     */
    private static ItemInteractionResult tryInsertBrewMug(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack usedItem,
            IFluidHandler fluidHandler
    ) {
        FluidStack mugBrew = DwarvenBrewFluidHelper.getBrewFromMug(usedItem);

        if (mugBrew.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }

        int mugAmount = mugBrew.getAmount();

        if (mugAmount != DwarvenBrewFluidHelper.MUG_VOLUME
                && mugAmount != DwarvenBrewFluidHelper.FIRST_MUG_VOLUME) {
            return ItemInteractionResult.FAIL;
        }

        int remainingCapacity = fluidHandler.getTankCapacity(0) - fluidHandler.getFluidInTank(0).getAmount();

        int fillAmount = mugAmount;

        if (mugAmount == DwarvenBrewFluidHelper.MUG_VOLUME && remainingCapacity == DwarvenBrewFluidHelper.FIRST_MUG_VOLUME) {
            fillAmount = DwarvenBrewFluidHelper.FIRST_MUG_VOLUME;
        } else if (remainingCapacity < mugAmount) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack insertedBrew = mugBrew.copy();

        insertedBrew.setAmount(fillAmount);

        int accepted = fluidHandler.fill(
                insertedBrew,
                IFluidHandler.FluidAction.SIMULATE
        );

        if (accepted != fillAmount) {
            return ItemInteractionResult.FAIL;
        }

        int inserted = fluidHandler.fill(
                insertedBrew,
                IFluidHandler.FluidAction.EXECUTE
        );

        if (inserted != fillAmount) {
            return ItemInteractionResult.FAIL;
        }

        if (!player.isCreative()) {
            player.setItemInHand(
                    hand,
                    ItemUtils.createFilledResult(
                            usedItem,
                            player,
                            new ItemStack(JolCraftItems.GLASS_MUG.get())
                    )
            );
        }

        player.awardStat(Stats.ITEM_USED.get(usedItem.getItem()));

        JolCraftSoundHelper.block(
                level,
                pos,
                SoundEvents.BOTTLE_EMPTY,
                1.0F,
                1.0F
        );

        return ItemInteractionResult.SUCCESS;
    }

    /**
     * Extracts a fixed amount of finished brew or yeast into the supplied
     * empty container.
     */
    private static ItemInteractionResult tryExtract(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack usedItem,
            IFluidHandler fluidHandler,
            Item filledContainer,
            int amount,
            boolean hasFinishedFluid,
            boolean brew
    ) {
        if (!hasFinishedFluid || amount <= 0) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack simulated = fluidHandler.drain(
                amount,
                IFluidHandler.FluidAction.SIMULATE
        );

        if (simulated.getAmount() != amount
                || !isExpectedFluid(
                simulated,
                brew
        )) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack extracted = fluidHandler.drain(
                amount,
                IFluidHandler.FluidAction.EXECUTE
        );

        ItemStack result = new ItemStack(filledContainer);

        result.set(
                JolCraftDataComponents.FLUID_CONTENT.get(),
                SimpleFluidContent.copyOf(
                        extracted
                )
        );

        giveFilledContainer(
                player,
                hand,
                usedItem,
                result
        );

        player.awardStat(Stats.ITEM_USED.get(usedItem.getItem()));

        JolCraftSoundHelper.block(
                level,
                pos,
                SoundEvents.BOTTLE_FILL,
                1.0F,
                1.0F
        );

        return ItemInteractionResult.SUCCESS;
    }

    /**
     * Consumes the used container when required and inserts the filled
     * replacement into the player's inventory or drops it nearby.
     */
    private static void giveFilledContainer(
            Player player,
            InteractionHand hand,
            ItemStack usedItem,
            ItemStack filledContainer
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!player.isCreative()) {usedItem.shrink(1);
        } else if (player.getInventory().contains(filledContainer)) {
            return;
        }

        JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                serverPlayer,
                filledContainer
        );
    }

    /**
     * Returns whether the extracted fluid matches the expected finished
     * brewing fluid.
     */
    private static boolean isExpectedFluid(
            FluidStack fluid,
            boolean brew
    ) {
        return brew ? fluid.is(JolCraftFluids.DWARVEN_BREW.get()) : fluid.is(JolCraftFluids.YEAST.get());
    }
}