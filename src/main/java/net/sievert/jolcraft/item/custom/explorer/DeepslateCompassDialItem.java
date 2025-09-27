package net.sievert.jolcraft.item.custom.explorer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.JolCraftDataComponents;

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
            tooltip.add(Component.translatable("tooltip.jolcraft.deepslate_compass_dial." + structureId).withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Component.translatable("tooltip.jolcraft.deepslate_compass_dial.unknown").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
