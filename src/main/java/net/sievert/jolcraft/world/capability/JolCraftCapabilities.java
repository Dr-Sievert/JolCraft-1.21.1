package net.sievert.jolcraft.world.capability;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewBucketFluidHandler;

public final class JolCraftCapabilities {

    private JolCraftCapabilities() {}

    public static void register(
            RegisterCapabilitiesEvent event
    ) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                JolCraftBlockEntities.FERMENTING_CAULDRON.get(),
                (cauldron, side) ->
                        cauldron.getBrewFluidHandler()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                JolCraftBlockEntities.FERMENTING_BARREL.get(),
                (barrel, side) -> barrel.getBrewFluidHandler()
        );

        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, context) ->
                        new DwarvenBrewBucketFluidHandler(stack),
                JolCraftItems.DWARVEN_BREW_BUCKET.get()
        );
    }
}