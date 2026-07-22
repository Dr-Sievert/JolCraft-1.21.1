package net.sievert.jolcraft.event.client.game.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.data.JolCraftTags;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class JolCraftTooltipEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty() || event.getToolTip().isEmpty()) return;

        Component originalName = event.getToolTip().getFirst();
        MutableComponent styledName = originalName.copy();

        if (stack.getRarity() == JolCraftEnumExtensions.Rarity.LEGENDARY.getValue()) {
            styledName.withStyle(
                    JolCraftEnumExtensions.Rarity.LEGENDARY
                            .getValue()
                            .getStyleModifier()
            );
        } else if (stack.is(JolCraftTags.Items.MITHRIL_ITEMS)) {
            styledName.withStyle(ChatFormatting.AQUA);
        } else {
            return;
        }

        event.getToolTip().set(0, styledName);
    }
}
