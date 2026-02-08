package net.sievert.jolcraft.world.item.custom.compass;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.language.AbstractLanguageKeys;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class DeepslateCompassDialItem extends Item {
    public DeepslateCompassDialItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String structureId = stack.get(JolCraftDataComponents.STRUCTURE_GROUP);
        if (structureId != null && !structureId.isEmpty()) {
            tooltip.add(
                    Component.translatable(AbstractLanguageKeys.tooltip(JolCraftLanguageKeys.DEEPSLATE_COMPASS_DIAL, structureId)).withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_COMPASS_DIAL_UNKNOWN).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
