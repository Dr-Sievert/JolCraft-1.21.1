package net.sievert.jolcraft.event.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.util.vanilla.JolCraftAnvilHelper;

public class JolCraftUtilityEvents {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        String rename = event.getName();

        if (!left.isEmpty() && left.is(JolCraftTags.Items.LEGENDARY_ITEMS)) {
            var vanilla = JolCraftAnvilHelper.vanillaResult(left, right, rename, event.getPlayer());
            ItemStack result = vanilla.result();

            if (!result.isEmpty()) {
                String baseName;
                if (rename != null && !rename.isEmpty()) {
                    baseName = net.minecraft.util.StringUtil.filterText(rename);
                } else {
                    baseName = left.getHoverName().getString();
                }

                result.remove(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
                result.remove(net.minecraft.core.component.DataComponents.ITEM_NAME);

                result.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                        net.minecraft.network.chat.Component.literal(baseName).withStyle(net.minecraft.ChatFormatting.GOLD));
            }

            event.setOutput(result);
            event.setCost(vanilla.cost());
            event.setMaterialCost(vanilla.materialCost());
        }

        if (!left.isEmpty() && left.is(JolCraftTags.Items.MITHRIL_ITEMS)) {
            var vanilla = JolCraftAnvilHelper.vanillaResult(left, right, rename, event.getPlayer());
            ItemStack result = vanilla.result();

            if (!result.isEmpty()) {
                String baseName;
                if (rename != null && !rename.isEmpty()) {
                    baseName = net.minecraft.util.StringUtil.filterText(rename);
                } else {
                    baseName = left.getHoverName().getString();
                }

                result.remove(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
                result.remove(net.minecraft.core.component.DataComponents.ITEM_NAME);

                result.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                        net.minecraft.network.chat.Component.literal(baseName).withStyle(ChatFormatting.AQUA));
            }

            event.setOutput(result);
            event.setCost(vanilla.cost());
            event.setMaterialCost(vanilla.materialCost());
        }
    }


    @SubscribeEvent
    public static void onEnchantItem(PlayerEnchantItemEvent event) {
        ItemStack stack = event.getEnchantedItem();

        if (!stack.isEmpty() && stack.is(JolCraftTags.Items.LEGENDARY_ITEMS)) {
            String baseName = stack.getHoverName().getString();
            stack.remove(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
            stack.remove(net.minecraft.core.component.DataComponents.ITEM_NAME);
            stack.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                    Component.literal(baseName).withStyle(ChatFormatting.GOLD));
        }

        if (!stack.isEmpty() && stack.is(JolCraftTags.Items.MITHRIL_ITEMS)) {
            String baseName = stack.getHoverName().getString();
            stack.remove(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
            stack.remove(net.minecraft.core.component.DataComponents.ITEM_NAME);
            stack.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                    Component.literal(baseName).withStyle(ChatFormatting.AQUA));
        }
    }

}
