package net.sievert.jolcraft.item.custom.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.item.util.TooltipHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SimpleTooltipItem extends Item {

    protected final String tooltipKey;

    public SimpleTooltipItem(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        TooltipHelper.addAltTooltip(
                tooltip,
                Component.translatable("tooltip.jolcraft." + tooltipKey).withStyle(ChatFormatting.GRAY),
                List.of()
        );
        super.appendHoverText(stack, context, tooltip, flag);
    }

}
