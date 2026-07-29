package net.sievert.jolcraft.integration.jei.util.gui;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class JeiDrawableHelper {

    private JeiDrawableHelper() {
    }

    public static @NotNull IDrawable sprite(
            @NotNull ResourceLocation sprite,
            int width,
            int height
    ) {
        return new IDrawable() {

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public void draw(
                    @NotNull GuiGraphics graphics,
                    int xOffset,
                    int yOffset
            ) {
                graphics.blitSprite(
                        sprite,
                        xOffset,
                        yOffset,
                        width,
                        height
                );
            }
        };
    }
}
