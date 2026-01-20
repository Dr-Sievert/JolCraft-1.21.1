package net.sievert.jolcraft.gui.custom.lapidary_bench;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class LapidaryBenchScreen extends AbstractContainerScreen<LapidaryBenchMenu> {

    private static final ResourceLocation TEXTURE   = JolCraft.location("textures/gui/container/lapidary_bench.png");
    private static final ResourceLocation HIGHLIGHT = JolCraft.location("textures/gui/sprites/widget/slot_highlighted.png");

    private static final int TOOL_BTN_X = 80;
    private static final int TOOL_BTN_Y = 32;

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
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        super.render(gg, mouseX, mouseY, partialTicks);
        this.renderTooltip(gg, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics gg, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        gg.blit(RenderType.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 150);
        if (!menu.hasTool()) return;

        ItemStack toolStack = menu.getToolStack();
        boolean active = menu.isButtonActive();

        int bx = x + TOOL_BTN_X;
        int by = y + TOOL_BTN_Y;

        boolean hovered = active
                && mouseX >= bx && mouseY >= by
                && mouseX < bx + BUTTON_SIZE && mouseY < by + BUTTON_SIZE;

        float alpha = active ? 1.0f : 0.4f;

        gg.flush();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        if (hovered) {
            gg.blit(RenderType.GUI_TEXTURED, HIGHLIGHT, bx, by, 0, 0, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
        }

        gg.renderItem(toolStack, bx, by);
        gg.renderItemDecorations(this.font, toolStack, bx, by);
        gg.flush();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics gg, int mouseX, int mouseY) {
        gg.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xDDDDDD, false);
        gg.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xDDDDDD, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && menu.hasTool()) {
            int x = (this.width - this.imageWidth) / 2;
            int y = (this.height - this.imageHeight) / 2;

            if (menu.isButtonActive() && isOverButton(mouseX, mouseY, x + TOOL_BTN_X, y + TOOL_BTN_Y)) {
                int actionId = menu.getActionIdForTool();
                if (actionId >= 0) {
                    assert Objects.requireNonNull(this.minecraft).gameMode != null;
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, actionId);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isOverButton(double mouseX, double mouseY, int bx, int by) {
        return mouseX >= bx && mouseY >= by && mouseX < bx + BUTTON_SIZE && mouseY < by + BUTTON_SIZE;
    }
}
