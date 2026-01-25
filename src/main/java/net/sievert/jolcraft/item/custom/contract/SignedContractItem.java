package net.sievert.jolcraft.item.custom.contract;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.custom.attachment.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.item.util.tooltip.TooltipHelper;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SignedContractItem extends Item {

    public SignedContractItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();

        if (DwarvenLanguageHelper.knowsDwarvish(player)) {
            if (JolCraftProxy.access().isAltDown()) {
                tooltip.add(Component.translatable("tooltip.jolcraft.signed_contract")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", TooltipHelper.altKey())
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.jolcraft.paper.locked")
                    .withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

}
