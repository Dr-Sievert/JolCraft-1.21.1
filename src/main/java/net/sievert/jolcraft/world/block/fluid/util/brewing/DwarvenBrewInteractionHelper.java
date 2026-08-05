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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
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
     * Predicts whether the supplied container interaction can succeed without
     * mutating either the item or the brewing tank.
     */
    public static ItemInteractionResult getInteractionResult(
            ItemStack usedItem,
            IFluidHandler fluidHandler,
            boolean hasFinishedFluid
    ) {
        if (usedItem.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack storedFluid = fluidHandler.getFluidInTank(0);

        if (usedItem.is(JolCraftItems.GLASS_MUG.get())) {
            return canExtract(
                    fluidHandler,
                    DwarvenBrewFluidHelper.MUG_VOLUME,
                    hasFinishedFluid,
                    JolCraftFluids.DWARVEN_BREW.get()
            ) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.FAIL;
        }

        if (usedItem.is(JolCraftItems.DWARVEN_BREW.get())) {
            return getBrewMugFillAmount(
                    usedItem,
                    fluidHandler
            ) > 0 ? ItemInteractionResult.SUCCESS : ItemInteractionResult.FAIL;
        }

        if (usedItem.is(Items.GLASS_BOTTLE)) {
            Fluid expectedFluid = getBottleFluid(storedFluid);

            return expectedFluid != null
                    && canExtract(
                    fluidHandler,
                    JolCraftBrewingItems.BOTTLE_VOLUME,
                    hasFinishedFluid,
                    expectedFluid
            ) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.FAIL;
        }

        if (storedFluid.is(JolCraftFluids.YEAST.get())
                || DwarvenBrewFluidHelper.isFinishedTannin(storedFluid)) {
            return ItemInteractionResult.FAIL;
        }

        IFluidHandlerItem itemHandler = usedItem.getCapability(
                Capabilities.FluidHandler.ITEM
        );

        if (itemHandler == null) {
            return ItemInteractionResult.FAIL;
        }

        if (canFillItemFromTank(
                itemHandler,
                fluidHandler
        ) || canFillTankFromItem(
                itemHandler,
                fluidHandler
        )) {
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.FAIL;
    }

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
        ItemInteractionResult predictedResult = getInteractionResult(
                usedItem,
                fluidHandler,
                hasFinishedFluid
        );

        if (predictedResult != ItemInteractionResult.SUCCESS) {
            return predictedResult;
        }

        FluidStack storedFluid = fluidHandler.getFluidInTank(0);

        if (usedItem.is(JolCraftItems.GLASS_MUG.get())) {
            return tryExtract(
                    level,
                    pos,
                    player,
                    hand,
                    usedItem,
                    fluidHandler,
                    JolCraftItems.DWARVEN_BREW.get(),
                    DwarvenBrewFluidHelper.MUG_VOLUME,
                    hasFinishedFluid,
                    JolCraftFluids.DWARVEN_BREW.get()
            );
        }

        if (usedItem.is(JolCraftItems.DWARVEN_BREW.get())) {
            return tryInsertBrewMug(
                    level,
                    pos,
                    player,
                    hand,
                    usedItem,
                    fluidHandler
            );
        }

        if (usedItem.is(Items.GLASS_BOTTLE)) {
            Fluid expectedFluid = getBottleFluid(storedFluid);

            if (expectedFluid == null) {
                return ItemInteractionResult.FAIL;
            }

            return tryExtract(
                    level,
                    pos,
                    player,
                    hand,
                    usedItem,
                    fluidHandler,
                    DwarvenBrewFluidHelper.isFinishedTannin(storedFluid)
                            ? JolCraftItems.TANNIN.get()
                            : JolCraftItems.YEAST.get(),
                    JolCraftBrewingItems.BOTTLE_VOLUME,
                    hasFinishedFluid,
                    expectedFluid
            );
        }

        FluidActionResult fillResult = FluidUtil.tryFillContainer(
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
        int fillAmount = getBrewMugFillAmount(
                mugBrew,
                fluidHandler
        );

        if (fillAmount <= 0) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack insertedBrew = mugBrew.copy();

        insertedBrew.setAmount(fillAmount);

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
     * Extracts a fixed amount of finished brew, yeast or tannin into the supplied
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
            Fluid expectedFluid
    ) {
        if (!canExtract(
                fluidHandler,
                amount,
                hasFinishedFluid,
                expectedFluid
        )) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack extracted = fluidHandler.drain(
                amount,
                IFluidHandler.FluidAction.EXECUTE
        );

        if (extracted.getAmount() != amount
                || !extracted.is(expectedFluid)) {
            return ItemInteractionResult.FAIL;
        }

        ItemStack result = createFilledContainer(
                filledContainer,
                extracted
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
     * Returns whether a full brew mug can be inserted into the supplied handler.
     */
    private static int getBrewMugFillAmount(
            ItemStack usedItem,
            IFluidHandler fluidHandler
    ) {
        return getBrewMugFillAmount(
                DwarvenBrewFluidHelper.getBrewFromMug(usedItem),
                fluidHandler
        );
    }

    private static int getBrewMugFillAmount(
            FluidStack mugBrew,
            IFluidHandler fluidHandler
    ) {
        if (mugBrew.getAmount() != DwarvenBrewFluidHelper.MUG_VOLUME) {
            return 0;
        }

        int accepted = fluidHandler.fill(
                mugBrew,
                IFluidHandler.FluidAction.SIMULATE
        );

        return accepted == DwarvenBrewFluidHelper.MUG_VOLUME
                ? DwarvenBrewFluidHelper.MUG_VOLUME
                : 0;
    }

    /**
     * Returns whether a fixed amount of the expected finished fluid can be
     * extracted from the supplied handler.
     */
    private static boolean canExtract(
            IFluidHandler fluidHandler,
            int amount,
            boolean hasFinishedFluid,
            Fluid expectedFluid
    ) {
        if (!hasFinishedFluid || amount <= 0) {
            return false;
        }

        FluidStack simulated = fluidHandler.drain(
                amount,
                IFluidHandler.FluidAction.SIMULATE
        );

        return simulated.getAmount() == amount
                && simulated.is(expectedFluid);
    }

    /**
     * Returns whether the item can accept fluid currently stored in the
     * brewing tank.
     */
    private static boolean canFillItemFromTank(
            IFluidHandlerItem itemHandler,
            IFluidHandler fluidHandler
    ) {
        FluidStack storedFluid = fluidHandler.getFluidInTank(0);

        if (storedFluid.isEmpty()) {
            return false;
        }

        int accepted = itemHandler.fill(
                storedFluid.copy(),
                IFluidHandler.FluidAction.SIMULATE
        );

        if (accepted <= 0) {
            return false;
        }

        FluidStack drained = fluidHandler.drain(
                accepted,
                IFluidHandler.FluidAction.SIMULATE
        );

        return drained.getAmount() == accepted;
    }

    /**
     * Returns whether the brewing tank can accept any fluid currently stored
     * in the item.
     */
    private static boolean canFillTankFromItem(
            IFluidHandlerItem itemHandler,
            IFluidHandler fluidHandler
    ) {
        for (int tank = 0; tank < itemHandler.getTanks(); tank++) {
            FluidStack contained = itemHandler.getFluidInTank(tank);

            if (contained.isEmpty()) {
                continue;
            }

            int accepted = fluidHandler.fill(
                    contained,
                    IFluidHandler.FluidAction.SIMULATE
            );

            if (accepted <= 0) {
                continue;
            }

            FluidStack requested = contained.copy();

            requested.setAmount(accepted);

            FluidStack drained = itemHandler.drain(
                    requested,
                    IFluidHandler.FluidAction.SIMULATE
            );

            if (drained.getAmount() == accepted
                    && FluidStack.isSameFluidSameComponents(
                    drained,
                    contained
            )) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates canonical brewing containers so extracted yeast and tannin use
     * the same component data as creative and generated variants.
     */
    private static ItemStack createFilledContainer(
            Item filledContainer,
            FluidStack extracted
    ) {
        if (filledContainer == JolCraftItems.YEAST.get()) {
            return JolCraftBrewingItems.createYeastStack(
                    filledContainer,
                    DwarvenBrewFluidHelper.getBrewingSpeed(
                            extracted
                    )
            );
        }

        if (filledContainer == JolCraftItems.TANNIN.get()) {
            return JolCraftBrewingItems.createTanninStack(
                    filledContainer,
                    extracted.getFluid(),
                    DwarvenBrewFluidHelper.getMaxAge(
                            extracted
                    )
            );
        }

        ItemStack result = new ItemStack(
                filledContainer
        );

        result.set(
                JolCraftDataComponents.FLUID_CONTENT.get(),
                SimpleFluidContent.copyOf(
                        extracted
                )
        );

        return result;
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

        if (!player.isCreative()) {
            usedItem.shrink(1);
        } else if (player.getInventory().contains(filledContainer)) {
            return;
        }

        JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                serverPlayer,
                filledContainer
        );
    }

    private static Fluid getBottleFluid(
            FluidStack storedFluid
    ) {
        if (storedFluid.is(JolCraftFluids.YEAST.get())) {
            return JolCraftFluids.YEAST.get();
        }

        if (storedFluid.is(JolCraftFluids.TANNIN.get())) {
            return JolCraftFluids.TANNIN.get();
        }

        if (storedFluid.is(JolCraftFluids.REFINED_TANNIN.get())) {
            return JolCraftFluids.REFINED_TANNIN.get();
        }

        return null;
    }
}