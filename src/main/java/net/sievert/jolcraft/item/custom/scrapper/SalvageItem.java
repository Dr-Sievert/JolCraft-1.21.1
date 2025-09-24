package net.sievert.jolcraft.item.custom.scrapper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.sievert.jolcraft.util.item.tooltip.TooltipHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SalvageItem extends Item {
    public SalvageItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        TooltipHelper.addAltTooltipCustom(
                tooltip,
                () -> Component.translatable("tooltip.jolcraft.salvage").withStyle(ChatFormatting.GRAY),
                () -> List.of(Component.translatable("tooltip.jolcraft.salvage_tag").withStyle(ChatFormatting.GRAY)),
                Screen::hasAltDown,
                () -> Component.translatable(
                        "tooltip.jolcraft.hold_key",
                        TooltipHelper.ALT_KEY
                ).withStyle(ChatFormatting.DARK_GRAY)
        );
        super.appendHoverText(stack, context, tooltip, flag);
    }

}
