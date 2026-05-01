package net.sievert.jolcraft.world.item.custom.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.client.tooltip.util.JolCraftTooltipHelper;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ArtisanHammerItem extends ToolItem {

    public ArtisanHammerItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (JolCraftProxy.access().isAltDown()) {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ARTISAN_HAMMER)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, JolCraftTooltipHelper.altKey())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
