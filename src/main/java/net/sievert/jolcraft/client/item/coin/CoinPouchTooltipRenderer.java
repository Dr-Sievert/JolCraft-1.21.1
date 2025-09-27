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
        return 16 + 2 + font.width(String.valueOf(coinCount));
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        ItemStack stack = new ItemStack(JolCraftItems.GOLD_COIN.get());
        guiGraphics.renderItem(stack, x, y, 0);
        guiGraphics.renderItemDecorations(font, stack, x, y, String.valueOf(coinCount));
    }
}
