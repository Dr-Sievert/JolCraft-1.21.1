package net.sievert.jolcraft.item.custom.contract;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.custom.attachment.language.DwarvenLanguageHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SignedContractItem extends Item {

    public SignedContractItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected final Player clientPlayer() {
        return net.minecraft.client.Minecraft.getInstance().player;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (DwarvenLanguageHelper.knowsDwarvish(clientPlayer())) {
            if (Screen.hasAltDown()) {
                tooltip.add(Component.translatable("tooltip.jolcraft.signed_contract")
                        .withStyle(ChatFormatting.GRAY));
            }else{
                Component altKey = InputConstants.getKey(InputConstants.KEY_LALT, -1)
                        .getDisplayName().copy().withStyle(ChatFormatting.BLUE);
                tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", altKey)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.jolcraft.paper.locked")
                    .withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
