package net.sievert.jolcraft.item.custom.contract;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WrittenContractItem extends Item {
    public WrittenContractItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("tooltip.jolcraft.written_contract")
                        .withStyle(ChatFormatting.GRAY));
            }else{
                Component shiftKey = Component.literal("Shift").withStyle(ChatFormatting.BLUE);
                tooltip.add(Component.translatable("tooltip.jolcraft.shift", shiftKey)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
