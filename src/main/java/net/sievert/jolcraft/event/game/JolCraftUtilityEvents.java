package net.sievert.jolcraft.event.game;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.item.util.rarity.JolCraftEnumParams;
import net.sievert.jolcraft.event.util.JolCraftAnvilHelper;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftUtilityEvents {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        String rename = event.getName();

        if (!left.isEmpty() && left.getRarity() == JolCraftEnumParams.LEGENDARY_RARITY.getValue()) {
            var vanilla = JolCraftAnvilHelper.vanillaResult(left, right, rename, event.getPlayer());
            ItemStack result = vanilla.result();

            if (!result.isEmpty()) {
                String baseName;
                if (rename != null && !rename.isEmpty()) {
                    baseName = StringUtil.filterText(rename);
                } else {
                    baseName = left.getHoverName().getString();
                }

                result.remove(DataComponents.CUSTOM_NAME);
                result.remove(DataComponents.ITEM_NAME);

                result.set(DataComponents.ITEM_NAME, Component.literal(baseName).withStyle(JolCraftEnumParams.LEGENDARY_RARITY.getValue().getStyleModifier()));
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
                    baseName = StringUtil.filterText(rename);
                } else {
                    baseName = left.getHoverName().getString();
                }

                result.remove(DataComponents.CUSTOM_NAME);
                result.remove(DataComponents.ITEM_NAME);

                result.set(DataComponents.ITEM_NAME, Component.literal(baseName).withStyle(ChatFormatting.AQUA));
            }

            event.setOutput(result);
            event.setCost(vanilla.cost());
            event.setMaterialCost(vanilla.materialCost());
        }
    }

    @SubscribeEvent
    public static void onEnchantItem(PlayerEnchantItemEvent event) {
        ItemStack stack = event.getEnchantedItem();
        if (stack.isEmpty()) return;

        if (stack.getRarity() == JolCraftEnumParams.LEGENDARY_RARITY.getValue()) {
            String baseName = stack.getHoverName().getString();
            stack.remove(DataComponents.CUSTOM_NAME);
            stack.remove(DataComponents.ITEM_NAME);
            stack.set(DataComponents.ITEM_NAME,
                    Component.literal(baseName).withStyle(JolCraftEnumParams.LEGENDARY_RARITY.getValue().getStyleModifier()));
        }

        if (stack.is(JolCraftTags.Items.MITHRIL_ITEMS)) {
            String baseName = stack.getHoverName().getString();
            stack.remove(DataComponents.CUSTOM_NAME);
            stack.remove(DataComponents.ITEM_NAME);
            stack.set(DataComponents.ITEM_NAME,
                    Component.literal(baseName).withStyle(ChatFormatting.AQUA));
        }
    }
}