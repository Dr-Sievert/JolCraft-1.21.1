package net.sievert.jolcraft.world.item.client.tooltip.coin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public record CoinPouchTooltip(int coinCount) implements TooltipComponent {

    @OnlyIn(Dist.CLIENT)
    public static final class Renderer implements ClientTooltipComponent {

        private final int coinCount;

        public Renderer(CoinPouchTooltip tooltip) {
            this.coinCount = tooltip.coinCount();
        }

        @Override
        public int getHeight() {
            return 20;
        }

        @Override
        public int getWidth(Font font) {
            return 18 + font.width(Integer.toString(this.coinCount));
        }

        @Override
        public void renderImage(@NotNull Font font, int x, int y, GuiGraphics guiGraphics) {
            ItemStack coin = new ItemStack(JolCraftItems.GOLD_COIN.get());
            guiGraphics.renderItem(coin, x, y);
            guiGraphics.renderItemDecorations(font, coin, x, y, Integer.toString(this.coinCount));
        }
    }
}