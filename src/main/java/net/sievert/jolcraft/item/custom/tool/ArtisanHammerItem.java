package net.sievert.jolcraft.item.custom.tool;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ArtisanHammerItem extends ToolItem {

    public ArtisanHammerItem(ToolMaterial material, Item.Properties properties) {
        super(material, properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasAltDown()) {
            tooltip.add(Component.translatable("tooltip.jolcraft.artisan_hammer")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            Component altKey = InputConstants.getKey(InputConstants.KEY_LALT, -1)
                    .getDisplayName().copy().withStyle(ChatFormatting.BLUE);
            tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", altKey)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

}
