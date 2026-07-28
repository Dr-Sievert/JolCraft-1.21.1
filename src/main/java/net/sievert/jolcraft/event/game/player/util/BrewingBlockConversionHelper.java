package net.sievert.jolcraft.event.game.player.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingBarrelBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipeInput;

@SuppressWarnings("deprecation")
public final class BrewingBlockConversionHelper {

    private BrewingBlockConversionHelper() {}

    public static void tryHandle(
            PlayerInteractEvent.RightClickBlock event
    ) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BlockState state =
                level.getBlockState(
                        event.getPos()
                );

        if (!tryConvertWaterCauldron(
                event,
                level,
                state
        )) {
            tryConvertBarrel(
                    event,
                    level,
                    state
            );
        }
    }

    private static boolean tryConvertWaterCauldron(
            PlayerInteractEvent.RightClickBlock event,
            ServerLevel level,
            BlockState state
    ) {
        if (!state.is(
                Blocks.WATER_CAULDRON
        )
                || state.getValue(
                LayeredCauldronBlock.LEVEL
        ) != LayeredCauldronBlock.MAX_FILL_LEVEL) {
            return false;
        }

        ItemStack usedItem =
                event.getItemStack();

        FermentingCauldronRecipeInput input =
                new FermentingCauldronRecipeInput(
                        usedItem.copyWithCount(
                                1
                        ),
                        ItemStack.EMPTY
                );

        boolean hasRecipe =
                level.getRecipeManager()
                        .getRecipeFor(
                                JolCraftRecipes
                                        .FERMENTING_CAULDRON_TYPE
                                        .get(),
                                input,
                                level
                        )
                        .isPresent();

        if (!hasRecipe) {
            return false;
        }

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Converting water cauldron -> fermenting cauldron player={} pos={} item={}",
                event.getEntity().getUUID(),
                JolCraftLogs.roundedPos(
                        event.getPos()
                ),
                usedItem.getItem()
                        .builtInRegistryHolder()
                        .key()
                        .location()
        );

        level.setBlock(
                event.getPos(),
                JolCraftBlocks.FERMENTING_CAULDRON.get()
                        .defaultBlockState(),
                Block.UPDATE_ALL
        );

        if (level.getBlockEntity(event.getPos()) instanceof
                FermentingCauldronBlockEntity cauldron) {
            ItemInteractionResult result =
                    cauldron.handleInteraction(
                            event.getEntity(),
                            event.getHand(),
                            usedItem
                    );

            JolCraftLogs.debug(
                    JolCraftLogTags.PLAYER,
                    "Fermenting cauldron interaction handled player={} pos={} result={}",
                    event.getEntity().getUUID(),
                    JolCraftLogs.roundedPos(
                            event.getPos()
                    ),
                    result
            );

            if (result.consumesAction()) {
                event.setCancellationResult(
                        result.result()
                );

                event.setCanceled(
                        true
                );

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

    private static void tryConvertBarrel(
            PlayerInteractEvent.RightClickBlock event,
            ServerLevel level,
            BlockState state
    ) {
        ItemStack usedItem =
                event.getItemStack();

        if (!state.is(
                Blocks.BARREL
        )
                || !(level.getBlockEntity(event.getPos()) instanceof
                BarrelBlockEntity vanillaBarrel)
                || !vanillaBarrel.isEmpty()
                || !DwarvenBrewFluidHelper.containsDwarvenBrew(
                usedItem
        )) {
            return;
        }

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Converting barrel -> fermenting barrel player={} pos={} item={}",
                event.getEntity().getUUID(),
                JolCraftLogs.roundedPos(
                        event.getPos()
                ),
                usedItem.getItem()
                        .builtInRegistryHolder()
                        .key()
                        .location()
        );

        BlockState fermentingBarrel =
                JolCraftBlocks.FERMENTING_BARREL.get()
                        .defaultBlockState()
                        .setValue(
                                FermentingBarrelBlock.FACING,
                                state.getValue(
                                        BarrelBlock.FACING
                                )
                        );

        level.setBlock(
                event.getPos(),
                fermentingBarrel,
                Block.UPDATE_ALL
        );

        if (level.getBlockEntity(event.getPos()) instanceof
                FermentingBarrelBlockEntity barrel) {
            ItemInteractionResult result =
                    barrel.handleInteraction(
                            event.getEntity(),
                            event.getHand(),
                            usedItem
                    );

            JolCraftLogs.debug(
                    JolCraftLogTags.PLAYER,
                    "Fermenting barrel interaction handled player={} pos={} result={}",
                    event.getEntity().getUUID(),
                    JolCraftLogs.roundedPos(
                            event.getPos()
                    ),
                    result
            );

            if (result.consumesAction()) {
                event.setCancellationResult(
                        result.result()
                );

                event.setCanceled(
                        true
                );

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

    private static void revertConversion(
            PlayerInteractEvent.RightClickBlock event,
            ServerLevel level,
            BlockState originalState,
            String target
    ) {
        JolCraftLogs.warn(
                JolCraftLogTags.PLAYER,
                "{} conversion failed, reverting player={} pos={}",
                target,
                event.getEntity().getUUID(),
                JolCraftLogs.roundedPos(
                        event.getPos()
                )
        );

        level.setBlock(
                event.getPos(),
                originalState,
                Block.UPDATE_ALL
        );
    }
}