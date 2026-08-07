package net.sievert.jolcraft.event.game.player.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingBarrelBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewInteractionHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipeInput;

@SuppressWarnings("deprecation")
public final class BrewingBlockConversionEventsHelper {

    private BrewingBlockConversionEventsHelper() {}

    /**
     * Converts eligible vanilla brewing containers before their normal
     * right-click interaction is processed.
     */
    public static void tryHandle(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        BlockState state = level.getBlockState(event.getPos());

        if (tryConvertWaterCauldron(event, level, state)) return;

        tryConvertBarrel(event, level, state);
    }

    /**
     * Converts a full water cauldron when the held item can begin
     * a fermenting cauldron recipe.
     */
    private static boolean tryConvertWaterCauldron(
            PlayerInteractEvent.RightClickBlock event,
            ServerLevel level,
            BlockState state
    ) {
        if (!state.is(Blocks.WATER_CAULDRON) || state.getValue(LayeredCauldronBlock.LEVEL) != LayeredCauldronBlock.MAX_FILL_LEVEL) {
            return false;
        }

        ItemStack usedItem = event.getItemStack();

        FermentingCauldronRecipeInput input = new FermentingCauldronRecipeInput(
                usedItem.copyWithCount(1),
                ItemStack.EMPTY
        );

        if (level.getRecipeManager().getRecipeFor(JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get(), input, level).isEmpty()) return false;

        BlockPos pos = event.getPos();

        JolCraftLogs.debug(
                JolCraftLogTags.BLOCK,
                "Converting water cauldron -> fermenting cauldron player={} pos={} item={}",
                event.getEntity().getUUID(),
                JolCraftLogs.roundedPos(pos),
                usedItem.getItem()
                        .builtInRegistryHolder()
                        .key()
                        .location()
        );

        level.setBlock(
                pos,
                JolCraftBlocks.FERMENTING_CAULDRON.get().defaultBlockState(),
                Block.UPDATE_ALL
        );

        if (level.getBlockEntity(pos) instanceof FermentingCauldronBlockEntity cauldron) {
            ItemInteractionResult result = cauldron.handleInteraction(
                    event.getEntity(),
                    event.getHand(),
                    usedItem
            );

            if (completeInteraction(event, result, "fermenting cauldron")) {
                return true;
            }
        }

        revertConversion(
                event,
                level,
                state,
                "fermenting cauldron"
        );

        return true;
    }

    /**
     * Converts an empty vanilla barrel when the held container contains
     * Dwarven brew that can be transferred into an aging barrel.
     */
    private static void tryConvertBarrel(
            PlayerInteractEvent.RightClickBlock event,
            ServerLevel level,
            BlockState state
    ) {
        BlockPos pos = event.getPos();
        ItemStack usedItem = event.getItemStack();

        if (!state.is(Blocks.BARREL)
                || !(level.getBlockEntity(pos) instanceof BarrelBlockEntity vanillaBarrel)
                || vanillaBarrel.getLootTable() != null
                || !vanillaBarrel.isEmpty()
                || !canFillEmptyFermentingBarrel(usedItem)) {
            return;
        }

        JolCraftLogs.debug(
                JolCraftLogTags.BLOCK,
                "Converting barrel -> fermenting barrel player={} pos={} item={}",
                event.getEntity().getUUID(),
                JolCraftLogs.roundedPos(pos),
                usedItem.getItem()
                        .builtInRegistryHolder()
                        .key()
                        .location()
        );

        BlockState fermentingBarrel = JolCraftBlocks.FERMENTING_BARREL.get()
                .defaultBlockState()
                .setValue(
                        FermentingBarrelBlock.FACING,
                        state.getValue(BarrelBlock.FACING)
                );

        level.setBlock(
                pos,
                fermentingBarrel,
                Block.UPDATE_ALL
        );

        if (level.getBlockEntity(pos) instanceof FermentingBarrelBlockEntity fermentingBarrelEntity) {
            ItemInteractionResult result = fermentingBarrelEntity.handleInteraction(
                    event.getEntity(),
                    event.getHand(),
                    usedItem
            );

            if (completeInteraction(event, result, "fermenting barrel")) {
                return;
            }
        }

        revertConversion(
                event,
                level,
                state,
                "fermenting barrel"
        );

    }

    /**
     * Validates the initiating container against an empty fermenting barrel
     * before the vanilla barrel is replaced.
     */
    private static boolean canFillEmptyFermentingBarrel(
            ItemStack usedItem
    ) {
        FluidTank simulatedTank = new FluidTank(
                FluidType.BUCKET_VOLUME,
                DwarvenBrewFluidHelper::isFinishedBrew
        );

        return DwarvenBrewInteractionHelper.getInteractionResult(
                usedItem,
                simulatedTank,
                false
        ) == ItemInteractionResult.SUCCESS;
    }

    /**
     * Cancels the original interaction when the converted block successfully
     * consumes the player's action.
     */
    private static boolean completeInteraction(
            PlayerInteractEvent.RightClickBlock event,
            ItemInteractionResult result,
            String target
    ) {
        JolCraftLogs.debug(
                JolCraftLogTags.BLOCK,
                "{} interaction handled player={} pos={} result={}",
                target,
                event.getEntity().getUUID(),
                JolCraftLogs.roundedPos(event.getPos()),
                result
        );

        if (!result.consumesAction()) return false;

        event.setCancellationResult(result.result());
        event.setCanceled(true);

        return true;
    }

    /**
     * Restores the original vanilla block when the newly created brewing
     * block cannot complete the initiating interaction.
     */
    private static void revertConversion(
            PlayerInteractEvent.RightClickBlock event,
            ServerLevel level,
            BlockState originalState,
            String target
    ) {
        JolCraftLogs.warn(
                JolCraftLogTags.BLOCK,
                "{} conversion failed, reverting player={} pos={}",
                target,
                event.getEntity().getUUID(),
                JolCraftLogs.roundedPos(event.getPos())
        );

        level.setBlock(
                event.getPos(),
                originalState,
                Block.UPDATE_ALL
        );
    }
}