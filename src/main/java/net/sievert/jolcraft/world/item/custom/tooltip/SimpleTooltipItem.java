package net.sievert.jolcraft.world.item.custom.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.item.util.tooltip.TooltipHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SimpleTooltipItem extends Item {

    /** Full translation key, e.g. "tooltip.jolcraft.dev_key" */
    protected final String tooltipTranslationKey;

    public SimpleTooltipItem(Properties properties, String tooltipTranslationKey) {
        super(properties);
        this.tooltipTranslationKey = tooltipTranslationKey;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        TooltipHelper.addAltTooltip(
                tooltip,
                Component.translatable(tooltipTranslationKey)
                        .withStyle(ChatFormatting.GRAY),
                List.of()
        );
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
