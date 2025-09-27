package net.sievert.jolcraft.gui.custom.lapidary_bench;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class LapidaryBenchScreen extends AbstractContainerScreen<LapidaryBenchMenu> {

    // Texture resources
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/gui/container/lapidary_bench.png");
    private static final ResourceLocation HIGHLIGHT = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/gui/container/sprites/button_highlighted.png");
    private static final ResourceLocation HAMMER = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/gui/container/sprites/lapidary_bench/deepslate_artisan_hammer.png");
    private static final ResourceLocation CHISEL = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/gui/container/sprites/lapidary_bench/deepslate_chisel.png");

    // Button positions
    private static final int HAMMER_X = 80;
    private static final int CHISEL_X = 128;
    private static final int BUTTON_Y = 32;
    private static final int BUTTON_SIZE = 16;
    private static final int HIGHLIGHT_SIZE = 17;

    public LapidaryBenchScreen(LapidaryBenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 150;
        this.titleLabelY = 6;
        this.inventoryLabelY = 56;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(RenderType.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 150);

        if (menu.hasGem() || menu.hasGeode()) {
            boolean hammerActive = menu.hasHammer();
            renderToolButton(guiGraphics, x, y, mouseX, mouseY, HAMMER_X, HAMMER, 0, hammerActive);
        }

        if (menu.hasGem()) {
            boolean chiselActive = menu.hasChisel();
            renderToolButton(guiGraphics, x, y, mouseX, mouseY, CHISEL_X, CHISEL, 1, chiselActive);
        }
    }


    /**
     * Renders one tool button (hammer or chisel) with highlight on hover.
     */
    private void renderToolButton(
            GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY,
            int btnRelX, ResourceLocation icon, int btnIndex, boolean active
    ) {
        int bx = x + btnRelX;
        int by = y + LapidaryBenchScreen.BUTTON_Y;
        boolean hovered = active && mouseX >= bx && mouseY >= by && mouseX < bx + BUTTON_SIZE && mouseY < by + BUTTON_SIZE;

        float alpha = active ? 1.0f : 0.4f;

        guiGraphics.flush();

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        if (hovered) {
            guiGraphics.blit(RenderType.GUI_TEXTURED, HIGHLIGHT, bx, by, 0, 0, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
        }

        guiGraphics.blit(RenderType.GUI_TEXTURED, icon, bx, by, 0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
        guiGraphics.flush();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xDDDDDD, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xDDDDDD, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (button == 0) {

            if ((menu.hasGem() || menu.hasGeode()) && menu.hasHammer() && isOverButton(mouseX, mouseY, x + HAMMER_X, y + BUTTON_Y)) {
                assert Objects.requireNonNull(this.minecraft).gameMode != null;
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                return true;
            }

            if (menu.hasGem() && menu.hasChisel() && isOverButton(mouseX, mouseY, x + CHISEL_X, y + BUTTON_Y)) {
                assert Objects.requireNonNull(this.minecraft).gameMode != null;
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isOverButton(double mouseX, double mouseY, int bx, int by) {
        return mouseX >= bx && mouseY >= by && mouseX < bx + BUTTON_SIZE && mouseY < by + BUTTON_SIZE;
    }

}
