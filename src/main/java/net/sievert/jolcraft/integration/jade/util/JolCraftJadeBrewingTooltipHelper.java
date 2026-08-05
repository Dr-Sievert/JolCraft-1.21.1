package net.sievert.jolcraft.integration.jade.util;

import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.event.game.world.time.JolCraftTimeHelper;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingTooltipHelper;
import snownee.jade.api.ITooltip;

/**
 * Shared helper for displaying brewing information in Jade tooltips.
 */
public final class JolCraftJadeBrewingTooltipHelper {

    private JolCraftJadeBrewingTooltipHelper() {}

    /**
     * Adds the fluid's age, brewing properties and potion effects.
     */
    public static void addBrewInfo(
            ITooltip tooltip,
            FluidStack brew
    ) {
        BrewingTooltipHelper.appendFluidTooltip(
                brew,
                tooltip::add,
                1.0F,
                (float) JolCraftTimeHelper.TICKS_PER_SECOND
        );
    }
}
