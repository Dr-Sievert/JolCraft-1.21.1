package net.sievert.jolcraft.world.gui.custom.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.gui.custom.menu.StrongboxMenu;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class StrongboxScreen extends AbstractContainerScreen<StrongboxMenu> {

    private static final ResourceLocation TEXTURE = JolCraftTextures.mod(JolCraftTextures.container(JolCraftBlockIds.STRONGBOX));

    public StrongboxScreen(StrongboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 150;
        this.titleLabelY = 6;
        this.inventoryLabelY = 56;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderType.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 150);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xDDDDDD, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xDDDDDD, false);
    }
}
