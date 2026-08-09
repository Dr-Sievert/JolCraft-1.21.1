package net.sievert.jolcraft.world.item.custom.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.entity.attachment.player.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ChiselItem extends ToolItem {

    public ChiselItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();
        if (!DwarfLoreAttachmentHelper.hasUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT)) {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_CUT_LOCKED).withStyle(ChatFormatting.RED));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}

