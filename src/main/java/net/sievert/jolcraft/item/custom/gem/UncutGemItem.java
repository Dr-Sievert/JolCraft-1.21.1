package net.sievert.jolcraft.item.custom.gem;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.util.attachment.lore.DwarfTomeHelper;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class UncutGemItem extends Item {

    public UncutGemItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasAltDown()) {
            tooltip.add(Component.translatable("tooltip.jolcraft.uncut_gem")
                    .withStyle(ChatFormatting.GRAY));

            if (!DwarfTomeHelper.hasUnlockClient(DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                tooltip.add(Component.translatable("tooltip.jolcraft.chisel.cut_locked")
                        .withStyle(ChatFormatting.RED));
            }
        } else {
            Component altKey = InputConstants.getKey(InputConstants.KEY_LALT, -1)
                    .getDisplayName().copy().withStyle(ChatFormatting.BLUE);
            tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", altKey)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
