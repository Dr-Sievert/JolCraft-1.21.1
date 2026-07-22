package net.sievert.jolcraft.world.item.custom.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.client.tooltip.util.JolCraftTooltipHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SpannerItem extends ToolItem {

    public SpannerItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        JolCraftTooltipHelper.addAltTooltip(
                tooltip,
                Component.translatable(JolCraftLanguageKeys.TOOLTIP_SPANNER).withStyle(ChatFormatting.GRAY),
                List.of()
        );
        super.appendHoverText(stack, context, tooltip, flag);
    }
}