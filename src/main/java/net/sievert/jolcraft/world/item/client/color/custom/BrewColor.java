package net.sievert.jolcraft.world.item.client.color.custom;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.BrewingColors;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class BrewColor {

    private static final int DEFAULT = BrewingColors.DWARVEN_BREW;

    private BrewColor() {}

    public static int color(
            @NotNull ItemStack stack
    ) {
        FluidStack brew =
                FluidUtil.getFluidContained(
                                stack
                        )
                        .orElse(
                                FluidStack.EMPTY
                        );

        if (brew.isEmpty()) {
            return DEFAULT;
        }

        Integer argb =
                brew.get(
                        JolCraftDataComponents.BREW_COLOR.get()
                );

        return argb != null
                ? 0xFF000000 | argb
                : DEFAULT;
    }
}