package net.sievert.jolcraft.world.gui.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.gui.menu.MortarMenu;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class MortarScreen extends AbstractContainerScreen<MortarMenu> {

    private static final ResourceLocation TEXTURE =
            JolCraftTextures.mod(
                    JolCraftTextures.container(
                            JolCraftBlockIds.MORTAR
                    )
            );

    private static final ResourceLocation PROGRESS_SPRITE = JolCraft.location(JolCraftStrings.slashed(JolCraftBlockIds.MORTAR, JolCraftStrings.underscored(
            JolCraftDictionary.RECIPE,
            JolCraftDictionary.ARROW,
            JolCraftDictionary.FILLED
    )));

    private static final ResourceLocation HIGHLIGHT_SPRITE =
            JolCraftTextures.modWidget(
                    JolCraftStrings.underscored(
                            JolCraftDictionary.SLOT,
                            JolCraftDictionary.HIGHLIGHTED
                    )
            );

    private static final int PROGRESS_X = 7;
    private static final int PROGRESS_Y = 15;
    private static final int PROGRESS_WIDTH = 98;
    private static final int PROGRESS_HEIGHT = 15;

    private static final int TOOL_BUTTON_X = 144;
    private static final int TOOL_BUTTON_Y = 48;

    private static final int BUTTON_SIZE = 16;
    private static final int HIGHLIGHT_SIZE = 17;
    private static final int GRIND_BUTTON_ID = 0;

    public MortarScreen(
            MortarMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(
                menu,
                playerInventory,
                title
        );

        this.imageWidth = 176;
        this.imageHeight = 150;
        this.titleLabelY = 6;
        this.inventoryLabelY = 56;
    }

    @Override
    public void render(
            GuiGraphics gg,
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        super.render(
                gg,
                mouseX,
                mouseY,
                partialTicks
        );

        this.renderTooltip(
                gg,
                mouseX,
                mouseY
        );
    }

    @Override
    protected void renderBg(
            GuiGraphics gg,
            float partialTicks,
            int mouseX,
            int mouseY
    ) {
        int x =
                (this.width - this.imageWidth) / 2;

        int y =
                (this.height - this.imageHeight) / 2;

        gg.blit(
                TEXTURE,
                x,
                y,
                0,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                176,
                150
        );

        int progressWidth =
                menu.getScaledGrindingProgress(
                        PROGRESS_WIDTH
                );

        if (progressWidth > 0) {
            gg.blitSprite(
                    PROGRESS_SPRITE,
                    PROGRESS_WIDTH,
                    PROGRESS_HEIGHT,
                    0,
                    0,
                    x + PROGRESS_X,
                    y + PROGRESS_Y,
                    progressWidth,
                    PROGRESS_HEIGHT
            );
        }

        if (!menu.hasTool()) {
            return;
        }

        ItemStack toolStack =
                menu.getToolStack();

        boolean active =
                menu.isButtonActive();

        int buttonX =
                x + TOOL_BUTTON_X;

        int buttonY =
                y + TOOL_BUTTON_Y;

        boolean hovered =
                active
                        && isOverButton(
                        mouseX,
                        mouseY,
                        buttonX,
                        buttonY
                );

        float alpha =
                active ? 1.0F : 0.4F;

        gg.flush();

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                alpha
        );

        if (hovered) {
            gg.blitSprite(
                    HIGHLIGHT_SPRITE,
                    buttonX,
                    buttonY,
                    HIGHLIGHT_SIZE,
                    HIGHLIGHT_SIZE
            );
        }

        gg.renderItem(
                toolStack,
                buttonX,
                buttonY
        );

        gg.renderItemDecorations(
                this.font,
                toolStack,
                buttonX,
                buttonY
        );

        gg.flush();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(
            GuiGraphics gg,
            int mouseX,
            int mouseY
    ) {
        gg.drawString(
                this.font,
                this.title,
                this.titleLabelX,
                this.titleLabelY,
                0xDDDDDD,
                false
        );

        gg.drawString(
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                0xDDDDDD,
                false
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button == 0
                && menu.hasTool()
                && menu.isButtonActive()) {
            int x =
                    (this.width - this.imageWidth) / 2;

            int y =
                    (this.height - this.imageHeight) / 2;

            if (isOverButton(
                    mouseX,
                    mouseY,
                    x + TOOL_BUTTON_X,
                    y + TOOL_BUTTON_Y
            )) {
                if (this.minecraft != null
                        && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode
                            .handleInventoryButtonClick(
                                    this.menu.containerId,
                                    GRIND_BUTTON_ID
                            );

                    return true;
                }
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    private boolean isOverButton(
            double mouseX,
            double mouseY,
            int buttonX,
            int buttonY
    ) {
        return mouseX >= buttonX
                && mouseY >= buttonY
                && mouseX < buttonX + BUTTON_SIZE
                && mouseY < buttonY + BUTTON_SIZE;
    }
}
