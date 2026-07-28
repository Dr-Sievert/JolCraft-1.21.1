package net.sievert.jolcraft.world.capability;

import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewFluidHandler;

public final class JolCraftCapabilities {

    private JolCraftCapabilities() {}

    public static void register(
            RegisterCapabilitiesEvent event
    ) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (
                        stack,
                        context
                ) -> new DwarvenBrewFluidHandler(
                        stack,
                        JolCraftItems.GLASS_MUG.get()
                ),
                JolCraftItems.DWARVEN_BREW.get()
        );

        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (
                        stack,
                        context
                ) -> new DwarvenBrewFluidHandler(
                        stack,
                        Items.BUCKET
                ),
                JolCraftItems.DWARVEN_BREW_BUCKET.get()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                JolCraftBlockEntities.FERMENTING_CAULDRON.get(),
                (
                        cauldron,
                        direction
                ) -> cauldron.getBrewFluidHandler()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                JolCraftBlockEntities.FERMENTING_BARREL.get(),
                (
                        barrel,
                        direction
                ) -> barrel.getBrewFluidHandler()
        );
    }
}