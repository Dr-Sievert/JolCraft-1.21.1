package net.sievert.jolcraft.world.item.custom.scrapper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.client.tooltip.util.JolCraftTooltipHelper;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SalvageItem extends Item {

    public SalvageItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        JolCraftTooltipHelper.addAltTooltipCustom(
                tooltip,
                () -> Component.translatable(JolCraftLanguageKeys.TOOLTIP_SALVAGE).withStyle(ChatFormatting.GRAY),
                () -> List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_SALVAGEABLE).withStyle(ChatFormatting.GRAY)),
                () -> JolCraftProxy.access().isAltDown(),
                () -> Component.translatable(JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, JolCraftTooltipHelper.altKey())
                        .withStyle(ChatFormatting.DARK_GRAY)
        );
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
