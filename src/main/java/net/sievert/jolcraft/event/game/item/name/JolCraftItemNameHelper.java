package net.sievert.jolcraft.event.game.item.name;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;

import java.util.function.UnaryOperator;

public final class JolCraftItemNameHelper {

    private JolCraftItemNameHelper() {}

    public static void applySpecialNameStyle(ItemStack stack) {
        if (stack.isEmpty()) return;

        String baseName = stack.getHoverName().getString();

        if (stack.getRarity() == JolCraftEnumExtensions.Rarity.LEGENDARY.getValue()) {
            applyItemName(stack, baseName, JolCraftEnumExtensions.Rarity.LEGENDARY.getValue().getStyleModifier());
            return;
        }

        if (stack.is(JolCraftTags.Items.MITHRIL_ITEMS)) {
            applyItemName(stack, baseName, ChatFormatting.AQUA);
        }
    }

    private static void applyItemName(ItemStack stack, String name, UnaryOperator<Style> style) {
        stack.remove(DataComponents.CUSTOM_NAME);
        stack.remove(DataComponents.ITEM_NAME);
        stack.set(DataComponents.ITEM_NAME, Component.literal(name).withStyle(style));
    }

    private static void applyItemName(ItemStack stack, String name, ChatFormatting color) {
        stack.remove(DataComponents.CUSTOM_NAME);
        stack.remove(DataComponents.ITEM_NAME);
        stack.set(DataComponents.ITEM_NAME, Component.literal(name).withStyle(color));
    }
}