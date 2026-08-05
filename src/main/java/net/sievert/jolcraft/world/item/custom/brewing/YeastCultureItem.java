package net.sievert.jolcraft.world.item.custom.brewing;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingTooltipHelper;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class YeastCultureItem extends Item {

    public YeastCultureItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                BrewingTooltipHelper.brewingSpeed(
                        DwarvenBrewFluidHelper.getBrewingSpeed(
                                stack
                        )
                )
        );

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }
}
