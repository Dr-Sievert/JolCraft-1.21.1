package net.sievert.jolcraft.client.item.coin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.item.JolCraftItems;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.NotNull;

public class CoinPouchTooltipRenderer implements ClientTooltipComponent {
    private final int coinCount;

    public CoinPouchTooltipRenderer(CoinPouchTooltip tooltip) {
        this.coinCount = tooltip.coinCount();
    }

    @Override
    public int getHeight(@NotNull Font font) {
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        // 16 for the icon + 2 for spacing + width of the count text (right side)
        return 16 + 2 + font.width(String.valueOf(coinCount));
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        // Draw the coin at the given position
        ItemStack stack = new ItemStack(JolCraftItems.GOLD_COIN.get());
        guiGraphics.renderItem(stack, x, y, 0); // 0 = seed for consistent render

        // Draw the count as a vanilla-style stack count (bottom right corner of the coin)
        guiGraphics.renderItemDecorations(font, stack, x, y, String.valueOf(coinCount));
    }
}
