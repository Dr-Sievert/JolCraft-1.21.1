package net.sievert.jolcraft.world.item.custom.merchant;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.client.tooltip.util.JolCraftTooltipHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class RestockCrateItem extends Item {
    public RestockCrateItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        JolCraftTooltipHelper.addAltTooltip(
                tooltip,
                Component.translatable(JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE).withStyle(ChatFormatting.GRAY),
                List.of()
        );
        super.appendHoverText(stack, context, tooltip, flag);
    }

}
