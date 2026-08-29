package net.sievert.jolcraft.world.gui.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.gui.menu.JolCraftMenu;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("SameParameterValue")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public abstract class JolCraftScreen<M extends JolCraftMenu>
        extends AbstractContainerScreen<M> {

    private static final ResourceLocation MENU_BASE =
            ResourceLocation.fromNamespaceAndPath(
                    JolCraft.MOD_ID,
                    "textures/gui/base/menu_base.png"
            );

    private static final ResourceLocation SLOT_BASE =
            JolCraftTextures.modWidget(
                    "slot_base"
            );

    private static final ResourceLocation SLOT_HIGHLIGHTED =
            JolCraftTextures.modWidget(
                    JolCraftStrings.underscored(
                            JolCraftDictionary.SLOT,
                            JolCraftDictionary.HIGHLIGHTED
                    )
            );

    private static final ResourceLocation DEFAULT_THEME =
            ResourceLocation.withDefaultNamespace(
                    "textures/block/deepslate_bricks.png"
            );

    private static final ResourceLocation DEFAULT_OUTLINE =
            ResourceLocation.fromNamespaceAndPath(
                    JolCraft.MOD_ID,
                    "textures/gui/base/menu_outline_stone.png"
            );

    private static final ResourceLocation DEFAULT_SLOT_TEXTURE =
            ResourceLocation.withDefaultNamespace(
                    "textures/block/polished_deepslate.png"
            );

    private static final ResourceLocation VANILLA_CONTAINER =
            ResourceLocation.withDefaultNamespace(
                    "textures/gui/container/generic_54.png"
            );

    private static final int BASE_TEXTURE_SIZE = 48;

    private static final int SLOT_TEXTURE_SIZE = 16;
    private static final int SLOT_BASE_SIZE = 18;
    private static final int SLOT_HIGHLIGHT_SIZE = 17;

    private static final int VANILLA_CONTAINER_TEXTURE_SIZE = 256;
    private static final int VANILLA_PLAYER_INVENTORY_SOURCE_Y = 126;

    private static final float DEFAULT_THEME_ALPHA = 1.0F;

    private static final float TOOL_HINT_RED = 0.22F;
    private static final float TOOL_HINT_GREEN = 0.22F;
    private static final float TOOL_HINT_BLUE = 0.22F;
    private static final float TOOL_HINT_ALPHA = 0.65F;

    private static final float DISABLED_BUTTON_ALPHA = 0.4F;

    protected JolCraftScreen(
            M menu,
            Inventory playerInventory,
            Component title
    ) {
        super(
                menu,
                playerInventory,
                title
        );

        this.imageWidth =
                menu.getWidth();

        this.imageHeight =
                menu.getHeight();

        if (menu.hasPlayerInventory()) {
            this.inventoryLabelX =
                    menu.getPlayerInventoryXOffset() + 8;

            this.inventoryLabelY =
                    menu.getMenuHeight() + 3;
        }
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        super.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTicks
        );

        this.renderTooltip(
                guiGraphics,
                mouseX,
                mouseY
        );
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTicks,
            int mouseX,
            int mouseY
    ) {
        renderNineSlice(
                guiGraphics,
                MENU_BASE
        );

        renderTheme(
                guiGraphics,
                getThemeTexture()
        );

        renderNineSlice(
                guiGraphics,
                getOutlineTexture()
        );

        if (this.menu.hasPlayerInventory()) {
            renderPlayerInventory(
                    guiGraphics
            );
        }
    }

    @Override
    protected void renderLabels(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        guiGraphics.drawString(
                this.font,
                this.title,
                this.titleLabelX,
                this.titleLabelY,
                JolCraftColors.rgb("D0D0D0"),
                false
        );

        if (this.menu.hasPlayerInventory()) {
            guiGraphics.drawString(
                    this.font,
                    this.playerInventoryTitle,
                    this.inventoryLabelX,
                    this.inventoryLabelY,
                    0x404040,
                    false
            );
        }
    }

    protected ResourceLocation getThemeTexture() {
        return DEFAULT_THEME;
    }

    protected float getThemeAlpha() {
        return DEFAULT_THEME_ALPHA;
    }

    protected ResourceLocation getOutlineTexture() {
        return DEFAULT_OUTLINE;
    }

    protected ResourceLocation getDefaultSlotTexture() {
        return DEFAULT_SLOT_TEXTURE;
    }

    protected final void renderSlotBackground(
            GuiGraphics guiGraphics,
            Slot slot
    ) {
        renderSlotBackgroundAt(
                guiGraphics,
                slot.x,
                slot.y
        );
    }

    protected final void renderSlotBackground(
            GuiGraphics guiGraphics,
            Slot slot,
            ResourceLocation texture
    ) {
        renderSlotBackgroundAt(
                guiGraphics,
                slot.x,
                slot.y,
                texture
        );
    }

    protected final void renderSlotBackgroundAt(
            GuiGraphics guiGraphics,
            int x,
            int y
    ) {
        renderSlotBackgroundAt(
                guiGraphics,
                x,
                y,
                getDefaultSlotTexture()
        );
    }

    protected final void renderSlotBackgroundAt(
            GuiGraphics guiGraphics,
            int x,
            int y,
            ResourceLocation texture
    ) {
        int screenX =
                this.leftPos + x;

        int screenY =
                this.topPos + y;

        guiGraphics.blit(
                texture,
                screenX,
                screenY,
                0,
                0.0F,
                0.0F,
                SLOT_TEXTURE_SIZE,
                SLOT_TEXTURE_SIZE,
                SLOT_TEXTURE_SIZE,
                SLOT_TEXTURE_SIZE
        );

        guiGraphics.blitSprite(
                SLOT_BASE,
                screenX - 1,
                screenY - 1,
                SLOT_BASE_SIZE,
                SLOT_BASE_SIZE
        );
    }

    protected final void renderToolSlot(
            GuiGraphics guiGraphics,
            Slot slot,
            ItemLike itemLike
    ) {
        renderToolSlot(
                guiGraphics,
                slot,
                itemLike,
                getDefaultSlotTexture()
        );
    }

    protected final void renderToolSlot(
            GuiGraphics guiGraphics,
            Slot slot,
            ItemLike itemLike,
            ResourceLocation texture
    ) {
        renderSlotBackground(
                guiGraphics,
                slot,
                texture
        );

        if (!slot.getItem().isEmpty()) {
            return;
        }

        renderToolHint(
                guiGraphics,
                slot.x,
                slot.y,
                itemLike
        );
    }

    protected final void renderItemButton(
            GuiGraphics guiGraphics,
            int tileX,
            int tileY,
            ItemStack stack,
            boolean active,
            int mouseX,
            int mouseY
    ) {
        int x =
                JolCraftMenu.tile(tileX);

        int y =
                JolCraftMenu.tile(tileY);

        int screenX =
                this.leftPos + x;

        int screenY =
                this.topPos + y;

        boolean hovered =
                active
                        && isOverButton(
                        mouseX,
                        mouseY,
                        tileX,
                        tileY
                );

        if (active) {
            renderSlotBackgroundAt(
                    guiGraphics,
                    x,
                    y
            );

            if (hovered) {
                guiGraphics.blitSprite(
                        SLOT_HIGHLIGHTED,
                        screenX,
                        screenY,
                        SLOT_HIGHLIGHT_SIZE,
                        SLOT_HIGHLIGHT_SIZE
                );
            }
        }

        float alpha =
                active
                        ? 1.0F
                        : DISABLED_BUTTON_ALPHA;

        guiGraphics.flush();

        RenderSystem.enableBlend();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                alpha
        );

        guiGraphics.renderItem(
                stack,
                screenX,
                screenY
        );

        guiGraphics.renderItemDecorations(
                this.font,
                stack,
                screenX,
                screenY
        );

        guiGraphics.flush();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        RenderSystem.disableBlend();
    }

    protected final boolean isOverButton(
            double mouseX,
            double mouseY,
            int tileX,
            int tileY
    ) {
        int x =
                this.leftPos
                        + JolCraftMenu.tile(tileX);

        int y =
                this.topPos
                        + JolCraftMenu.tile(tileY);

        return mouseX >= x
                && mouseY >= y
                && mouseX < x + SLOT_TEXTURE_SIZE
                && mouseY < y + SLOT_TEXTURE_SIZE;
    }

    private void renderToolHint(
            GuiGraphics guiGraphics,
            int x,
            int y,
            ItemLike itemLike
    ) {
        ItemStack hintStack =
                new ItemStack(
                        itemLike.asItem()
                );

        int screenX =
                this.leftPos + x;

        int screenY =
                this.topPos + y;

        guiGraphics.flush();

        RenderSystem.enableBlend();

        RenderSystem.setShaderColor(
                TOOL_HINT_RED,
                TOOL_HINT_GREEN,
                TOOL_HINT_BLUE,
                TOOL_HINT_ALPHA
        );

        guiGraphics.renderItem(
                hintStack,
                screenX,
                screenY
        );

        guiGraphics.flush();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        RenderSystem.disableBlend();
    }

    private void renderNineSlice(
            GuiGraphics guiGraphics,
            ResourceLocation texture
    ) {
        int widthTiles =
                this.menu.getWidthTiles();

        int heightTiles =
                this.menu.getMenuHeightTiles();

        for (int tileY = 0; tileY < heightTiles; tileY++) {
            for (int tileX = 0; tileX < widthTiles; tileX++) {
                guiGraphics.blit(
                        texture,
                        this.leftPos
                                + JolCraftMenu.tileIndex(tileX),
                        this.topPos
                                + JolCraftMenu.tileIndex(tileY),
                        0,
                        getSourceCoordinate(
                                tileX,
                                widthTiles
                        ),
                        getSourceCoordinate(
                                tileY,
                                heightTiles
                        ),
                        JolCraftMenu.TILE_SIZE,
                        JolCraftMenu.TILE_SIZE,
                        BASE_TEXTURE_SIZE,
                        BASE_TEXTURE_SIZE
                );
            }
        }
    }

    private void renderTheme(
            GuiGraphics guiGraphics,
            ResourceLocation themeTexture
    ) {
        guiGraphics.flush();

        RenderSystem.enableBlend();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                getThemeAlpha()
        );

        int widthTiles =
                this.menu.getWidthTiles();

        int heightTiles =
                this.menu.getMenuHeightTiles();

        for (int tileY = 0; tileY < heightTiles; tileY++) {
            for (int tileX = 0; tileX < widthTiles; tileX++) {
                int x =
                        this.leftPos
                                + JolCraftMenu.tileIndex(tileX);

                int y =
                        this.topPos
                                + JolCraftMenu.tileIndex(tileY);

                boolean left =
                        tileX == 0;

                boolean right =
                        tileX == widthTiles - 1;

                boolean top =
                        tileY == 0;

                boolean bottom =
                        tileY == heightTiles - 1;

                if (top && left) {
                    renderTopLeftThemeCorner(
                            guiGraphics,
                            themeTexture,
                            x,
                            y
                    );
                } else if (top && right) {
                    renderTopRightThemeCorner(
                            guiGraphics,
                            themeTexture,
                            x,
                            y
                    );
                } else if (bottom && left) {
                    renderBottomLeftThemeCorner(
                            guiGraphics,
                            themeTexture,
                            x,
                            y
                    );
                } else if (bottom && right) {
                    renderBottomRightThemeCorner(
                            guiGraphics,
                            themeTexture,
                            x,
                            y
                    );
                } else {
                    renderThemeRegion(
                            guiGraphics,
                            themeTexture,
                            x,
                            y,
                            0,
                            0,
                            JolCraftMenu.TILE_SIZE,
                            JolCraftMenu.TILE_SIZE
                    );
                }
            }
        }

        guiGraphics.flush();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        RenderSystem.disableBlend();
    }

    private void renderTopLeftThemeCorner(
            GuiGraphics guiGraphics,
            ResourceLocation themeTexture,
            int x,
            int y
    ) {
        renderThemeRegion(guiGraphics, themeTexture, x + 2, y, 2, 0, 14, 1);
        renderThemeRegion(guiGraphics, themeTexture, x + 1, y + 1, 1, 1, 15, 1);
        renderThemeRegion(guiGraphics, themeTexture, x, y + 2, 0, 2, 16, 14);
    }

    private void renderTopRightThemeCorner(
            GuiGraphics guiGraphics,
            ResourceLocation themeTexture,
            int x,
            int y
    ) {
        renderThemeRegion(guiGraphics, themeTexture, x, y, 0, 0, 14, 1);
        renderThemeRegion(guiGraphics, themeTexture, x, y + 1, 0, 1, 15, 1);
        renderThemeRegion(guiGraphics, themeTexture, x, y + 2, 0, 2, 16, 14);
    }

    private void renderBottomLeftThemeCorner(
            GuiGraphics guiGraphics,
            ResourceLocation themeTexture,
            int x,
            int y
    ) {
        renderThemeRegion(guiGraphics, themeTexture, x, y, 0, 0, 16, 14);
        renderThemeRegion(guiGraphics, themeTexture, x + 1, y + 14, 1, 14, 15, 1);
        renderThemeRegion(guiGraphics, themeTexture, x + 2, y + 15, 2, 15, 14, 1);
    }

    private void renderBottomRightThemeCorner(
            GuiGraphics guiGraphics,
            ResourceLocation themeTexture,
            int x,
            int y
    ) {
        renderThemeRegion(guiGraphics, themeTexture, x, y, 0, 0, 16, 14);
        renderThemeRegion(guiGraphics, themeTexture, x, y + 14, 0, 14, 15, 1);
        renderThemeRegion(guiGraphics, themeTexture, x, y + 15, 0, 15, 14, 1);
    }

    private void renderThemeRegion(
            GuiGraphics guiGraphics,
            ResourceLocation themeTexture,
            int x,
            int y,
            int sourceX,
            int sourceY,
            int width,
            int height
    ) {
        guiGraphics.blit(
                themeTexture,
                x,
                y,
                0,
                sourceX,
                sourceY,
                width,
                height,
                JolCraftMenu.TILE_SIZE,
                JolCraftMenu.TILE_SIZE
        );
    }

    private void renderPlayerInventory(
            GuiGraphics guiGraphics
    ) {
        guiGraphics.blit(
                VANILLA_CONTAINER,
                this.leftPos
                        + this.menu.getPlayerInventoryXOffset(),
                this.topPos
                        + this.menu.getMenuHeight(),
                0,
                0.0F,
                VANILLA_PLAYER_INVENTORY_SOURCE_Y,
                JolCraftMenu.PLAYER_INVENTORY_WIDTH,
                JolCraftMenu.PLAYER_INVENTORY_HEIGHT,
                VANILLA_CONTAINER_TEXTURE_SIZE,
                VANILLA_CONTAINER_TEXTURE_SIZE
        );
    }

    private static int getSourceCoordinate(
            int tile,
            int tileCount
    ) {
        if (tile == 0) {
            return 0;
        }

        if (tile == tileCount - 1) {
            return 32;
        }

        return 16;
    }
}