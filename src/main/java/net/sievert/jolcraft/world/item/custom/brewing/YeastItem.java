package net.sievert.jolcraft.world.item.custom.brewing;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingTooltipHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class YeastItem extends Item {

    public YeastItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        BrewingTooltipHelper.appendItemFluidTooltip(
                stack,
                tooltip::add
        );

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }
}
