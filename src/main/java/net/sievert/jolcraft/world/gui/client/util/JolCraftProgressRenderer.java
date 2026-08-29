package net.sievert.jolcraft.world.gui.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.gui.menu.JolCraftMenu;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class JolCraftProgressRenderer {

    private static final ResourceLocation DEFAULT_ARROW =
            JolCraft.location(
                    "textures/gui/sprites/widget/arrow.png"
            );

    private static final ResourceLocation DEFAULT_ARROW_FILLED =
            JolCraft.location(
                    "textures/gui/sprites/widget/arrow_filled.png"
            );

    private static final ResourceLocation DEFAULT_ARROW_EXTENSION =
            JolCraft.location(
                    "textures/gui/sprites/widget/arrow_extension.png"
            );

    private static final ResourceLocation DEFAULT_ARROW_EXTENSION_FILLED =
            JolCraft.location(
                    "textures/gui/sprites/widget/arrow_extension_filled.png"
            );

    private static final ResourceLocation DEFAULT_THEME =
            ResourceLocation.withDefaultNamespace(
                    "textures/block/polished_deepslate.png"
            );

    private static final int SEGMENT_SIZE = 16;
    private static final int MINIMUM_WIDTH = 16;

    private static final float DEFAULT_THEME_ALPHA = 0.3F;

    private static final Map<ResourceLocation, NativeImage> IMAGE_CACHE =
            new HashMap<>();

    private JolCraftProgressRenderer() {
    }

    public static void render(
            GuiGraphics guiGraphics,
            int leftPos,
            int topPos,
            int startTileX,
            int startTileY,
            int lengthTiles,
            float progress
    ) {
        render(
                guiGraphics,
                leftPos,
                topPos,
                startTileX,
                startTileY,
                lengthTiles,
                0,
                0,
                progress,
                DEFAULT_THEME_ALPHA,
                DEFAULT_THEME
        );
    }

    public static void render(
            GuiGraphics guiGraphics,
            int leftPos,
            int topPos,
            int startTileX,
            int startTileY,
            int lengthTiles,
            int startPadding,
            int endPadding,
            float progress
    ) {
        render(
                guiGraphics,
                leftPos,
                topPos,
                startTileX,
                startTileY,
                lengthTiles,
                startPadding,
                endPadding,
                progress,
                DEFAULT_THEME_ALPHA,
                DEFAULT_THEME
        );
    }

    public static void render(
            GuiGraphics guiGraphics,
            int leftPos,
            int topPos,
            int startTileX,
            int startTileY,
            int lengthTiles,
            int startPadding,
            int endPadding,
            float progress,
            float themeAlpha,
            ResourceLocation themeTexture
    ) {
        render(
                guiGraphics,
                leftPos,
                topPos,
                startTileX,
                startTileY,
                lengthTiles,
                startPadding,
                endPadding,
                progress,
                themeAlpha,
                themeTexture,
                DEFAULT_ARROW,
                DEFAULT_ARROW_FILLED,
                DEFAULT_ARROW_EXTENSION,
                DEFAULT_ARROW_EXTENSION_FILLED
        );
    }

    public static void render(
            GuiGraphics guiGraphics,
            int leftPos,
            int topPos,
            int startTileX,
            int startTileY,
            int lengthTiles,
            int startPadding,
            int endPadding,
            float progress,
            float themeAlpha,
            ResourceLocation themeTexture,
            ResourceLocation arrowTexture,
            ResourceLocation filledArrowTexture,
            ResourceLocation extensionTexture,
            ResourceLocation filledExtensionTexture
    ) {
        renderInternal(
                guiGraphics,
                leftPos,
                topPos,
                startTileX,
                startTileY,
                lengthTiles,
                startPadding,
                endPadding,
                progress,
                themeAlpha,
                themeTexture,
                arrowTexture,
                filledArrowTexture,
                extensionTexture,
                filledExtensionTexture
        );
    }

    public static void renderUntextured(
            GuiGraphics guiGraphics,
            int leftPos,
            int topPos,
            int startTileX,
            int startTileY,
            int lengthTiles,
            int startPadding,
            int endPadding,
            float progress
    ) {
        renderInternal(
                guiGraphics,
                leftPos,
                topPos,
                startTileX,
                startTileY,
                lengthTiles,
                startPadding,
                endPadding,
                progress,
                1.0F,
                null,
                DEFAULT_ARROW,
                DEFAULT_ARROW_FILLED,
                DEFAULT_ARROW_EXTENSION,
                DEFAULT_ARROW_EXTENSION_FILLED
        );
    }

    private static void renderInternal(
            GuiGraphics guiGraphics,
            int leftPos,
            int topPos,
            int startTileX,
            int startTileY,
            int lengthTiles,
            int startPadding,
            int endPadding,
            float progress,
            float themeAlpha,
            ResourceLocation themeTexture,
            ResourceLocation arrowTexture,
            ResourceLocation filledArrowTexture,
            ResourceLocation extensionTexture,
            ResourceLocation filledExtensionTexture
    ) {
        int effectiveWidth =
                getEffectiveWidth(
                        lengthTiles,
                        startPadding,
                        endPadding
                );

        float clampedProgress =
                Math.max(
                        0.0F,
                        Math.min(
                                1.0F,
                                progress
                        )
                );

        float clampedThemeAlpha =
                Math.max(
                        0.0F,
                        Math.min(
                                1.0F,
                                themeAlpha
                        )
                );

        int screenX =
                leftPos
                        + JolCraftMenu.tile(startTileX)
                        + startPadding;

        int screenY =
                topPos
                        + JolCraftMenu.tile(startTileY);

        renderArrow(
                guiGraphics,
                screenX,
                screenY,
                effectiveWidth,
                clampedProgress,
                clampedThemeAlpha,
                themeTexture,
                arrowTexture,
                filledArrowTexture,
                extensionTexture,
                filledExtensionTexture
        );
    }

    private static int getEffectiveWidth(
            int lengthTiles,
            int startPadding,
            int endPadding
    ) {
        if (lengthTiles <= 0) {
            throw new IllegalArgumentException(
                    "Progress length must be at least 1 tile"
            );
        }

        if (startPadding < 0
                || endPadding < 0) {
            throw new IllegalArgumentException(
                    "Progress padding cannot be negative"
            );
        }

        int effectiveWidth =
                lengthTiles * SEGMENT_SIZE
                        - startPadding
                        - endPadding;

        if (effectiveWidth < MINIMUM_WIDTH) {
            throw new IllegalArgumentException(
                    "Progress width after padding must be at least "
                            + MINIMUM_WIDTH
                            + " pixels"
            );
        }

        return effectiveWidth;
    }

    private static void renderArrow(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int width,
            float progress,
            float themeAlpha,
            ResourceLocation themeTexture,
            ResourceLocation arrowTexture,
            ResourceLocation filledArrowTexture,
            ResourceLocation extensionTexture,
            ResourceLocation filledExtensionTexture
    ) {
        int filledWidth =
                Math.round(
                        width * progress
                );

        int extensionWidth =
                width - SEGMENT_SIZE;

        renderExtension(
                guiGraphics,
                x,
                y,
                extensionWidth,
                filledWidth,
                themeAlpha,
                themeTexture,
                extensionTexture,
                filledExtensionTexture
        );

        int headX =
                x + extensionWidth;

        int headFilledWidth =
                Math.max(
                        0,
                        Math.min(
                                SEGMENT_SIZE,
                                filledWidth - extensionWidth
                        )
                );

        renderSegment(
                guiGraphics,
                headX,
                y,
                SEGMENT_SIZE,
                arrowTexture,
                filledArrowTexture,
                themeTexture,
                headFilledWidth,
                themeAlpha,
                extensionWidth
        );
    }

    private static void renderExtension(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int width,
            int filledWidth,
            float themeAlpha,
            ResourceLocation themeTexture,
            ResourceLocation extensionTexture,
            ResourceLocation filledExtensionTexture
    ) {
        int offset = 0;

        while (offset < width) {
            int segmentWidth =
                    Math.min(
                            SEGMENT_SIZE,
                            width - offset
                    );

            int segmentFilledWidth =
                    Math.max(
                            0,
                            Math.min(
                                    segmentWidth,
                                    filledWidth - offset
                            )
                    );

            renderSegment(
                    guiGraphics,
                    x + offset,
                    y,
                    segmentWidth,
                    extensionTexture,
                    filledExtensionTexture,
                    themeTexture,
                    segmentFilledWidth,
                    themeAlpha,
                    offset
            );

            offset += segmentWidth;
        }
    }

    private static void renderSegment(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int width,
            ResourceLocation emptyTexture,
            ResourceLocation filledTexture,
            ResourceLocation themeTexture,
            int filledWidth,
            float themeAlpha,
            int themeOffset
    ) {
        NativeImage emptyImage =
                getImage(
                        emptyTexture
                );

        NativeImage filledImage =
                getImage(
                        filledTexture
                );

        NativeImage theme =
                themeTexture != null
                        ? getImage(themeTexture)
                        : null;

        for (int pixelY = 0;
             pixelY < SEGMENT_SIZE;
             pixelY++) {
            for (int pixelX = 0;
                 pixelX < width;
                 pixelX++) {
                NativeImage source =
                        pixelX < filledWidth
                                ? filledImage
                                : emptyImage;

                int sourceColor =
                        source.getPixelRGBA(
                                pixelX,
                                pixelY
                        );

                int sourceAlpha =
                        sourceColor >>> 24 & 0xFF;

                if (sourceAlpha == 0) {
                    continue;
                }

                int sourceRed =
                        sourceColor & 0xFF;

                int sourceGreen =
                        sourceColor >>> 8 & 0xFF;

                int sourceBlue =
                        sourceColor >>> 16 & 0xFF;

                int red =
                        sourceRed;

                int green =
                        sourceGreen;

                int blue =
                        sourceBlue;

                if (theme != null
                        && themeAlpha > 0.0F) {
                    int themeX =
                            Math.floorMod(
                                    themeOffset + pixelX,
                                    theme.getWidth()
                            );

                    int themeY =
                            Math.floorMod(
                                    pixelY,
                                    theme.getHeight()
                            );

                    int themeColor =
                            theme.getPixelRGBA(
                                    themeX,
                                    themeY
                            );

                    int themeTextureAlpha =
                            themeColor >>> 24 & 0xFF;

                    float blend =
                            themeAlpha
                                    * (themeTextureAlpha / 255.0F);

                    int themeRed =
                            themeColor & 0xFF;

                    int themeGreen =
                            themeColor >>> 8 & 0xFF;

                    int themeBlue =
                            themeColor >>> 16 & 0xFF;

                    red =
                            Math.round(
                                    sourceRed
                                            + (themeRed - sourceRed) * blend
                            );

                    green =
                            Math.round(
                                    sourceGreen
                                            + (themeGreen - sourceGreen) * blend
                            );

                    blue =
                            Math.round(
                                    sourceBlue
                                            + (themeBlue - sourceBlue) * blend
                            );
                }

                int color =
                        sourceAlpha << 24
                                | red << 16
                                | green << 8
                                | blue;

                guiGraphics.fill(
                        x + pixelX,
                        y + pixelY,
                        x + pixelX + 1,
                        y + pixelY + 1,
                        color
                );
            }
        }
    }

    private static int getFinalAlpha(float themeAlpha, int themeColor, int sourceAlpha) {
        int themeTextureAlpha =
                themeColor >>> 24 & 0xFF;

        return Math.round(
                sourceAlpha
                        / 255.0F
                        * themeTextureAlpha
                        / 255.0F
                        * themeAlpha
                        * 255.0F
        );
    }

    private static int toArgb(
            int nativeColor
    ) {
        int alpha =
                nativeColor >>> 24 & 0xFF;

        int red =
                nativeColor & 0xFF;

        int green =
                nativeColor >>> 8 & 0xFF;

        int blue =
                nativeColor >>> 16 & 0xFF;

        return alpha << 24
                | red << 16
                | green << 8
                | blue;
    }

    private static NativeImage getImage(
            ResourceLocation texture
    ) {
        return IMAGE_CACHE.computeIfAbsent(
                texture,
                JolCraftProgressRenderer::loadImage
        );
    }

    private static NativeImage loadImage(
            ResourceLocation texture
    ) {
        Resource resource =
                Minecraft.getInstance()
                        .getResourceManager()
                        .getResource(texture)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Missing progress texture: "
                                                + texture
                                )
                        );

        try (InputStream inputStream =
                     resource.open()) {
            return NativeImage.read(
                    inputStream
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to load progress texture: "
                            + texture,
                    exception
            );
        }
    }

    public static void clearTextureCache() {
        for (NativeImage image
                : IMAGE_CACHE.values()) {
            image.close();
        }

        IMAGE_CACHE.clear();
    }
}