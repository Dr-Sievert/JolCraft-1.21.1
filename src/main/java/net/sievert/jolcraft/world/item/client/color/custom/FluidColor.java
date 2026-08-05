package net.sievert.jolcraft.world.item.client.color.custom;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class FluidColor {

    private FluidColor() {}

    public static int color(
            @NotNull ItemStack stack,
            int defaultColor
    ) {
        FluidStack fluid = FluidUtil.getFluidContained(stack)
                .orElse(FluidStack.EMPTY);

        if (fluid.isEmpty()) {
            return defaultColor;
        }

        return IClientFluidTypeExtensions.of(
                fluid.getFluid()
        ).getTintColor(
                fluid
        );
    }

    public static int brewingSpeedColor(
            @NotNull ItemStack stack
    ) {
        float speed = DwarvenBrewFluidHelper.getBrewingSpeed(stack);
        float brightness = Math.max(
                0.72F,
                1.0F - (speed - 1.0F) * 0.14F
        );

        int color = BrewingColors.YEAST_CULTURE;
        int red = Math.round(((color >> 16) & 0xFF) * brightness);
        int green = Math.round(((color >> 8) & 0xFF) * brightness);
        int blue = Math.round((color & 0xFF) * brightness);

        return 0xFF000000
                | (red << 16)
                | (green << 8)
                | blue;
    }
}
