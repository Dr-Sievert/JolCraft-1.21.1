package net.sievert.jolcraft.world.item.custom.gem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.player.attachment.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.client.tooltip.util.JolCraftTooltipHelper;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

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
        var client = JolCraftProxy.access();
        Player player = client.getLocalPlayer();

        if (client.isAltDown()) {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_UNCUT_GEM)
                    .withStyle(ChatFormatting.GRAY));

            if (player != null && !DwarfLoreAttachmentHelper.hasUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_CUT_LOCKED)
                        .withStyle(ChatFormatting.RED));
            }
        } else {
            tooltip.add(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, JolCraftTooltipHelper.altKey())
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

}
