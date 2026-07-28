package net.sievert.jolcraft.world.block.fluid.custom;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the fluid behavior of dwarven brew.
 */
public final class DwarvenBrewFluidType extends FluidType {

    public DwarvenBrewFluidType(
            Properties properties
    ) {
        super(properties);
    }

    /**
     * Returns a filled dwarven brew bucket for a complete bucket of finished brew.
     */
    @Override
    public @NotNull ItemStack getBucket(
            @NotNull FluidStack brew
    ) {
        if (!DwarvenBrewFluidHelper.isFinishedBrew(brew)
                || brew.getAmount() != FluidType.BUCKET_VOLUME) {
            return ItemStack.EMPTY;
        }

        ItemStack bucket = new ItemStack(
                JolCraftItems.DWARVEN_BREW_BUCKET.get()
        );

        bucket.set(
                JolCraftDataComponents.FLUID_CONTENT.get(),
                SimpleFluidContent.copyOf(brew)
        );

        return bucket;
    }
}