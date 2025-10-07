package net.sievert.jolcraft.gui.custom.strongbox;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LockScreen extends AbstractContainerScreen<LockMenu> {
    private final Random guiRandom = new Random();
    private int lastSeenPulse = 0;

    private static final ResourceLocation TEXTURE = JolCraft.location("textures/gui/container/strongbox_lock.png");

    private static final ResourceLocation HIGHLIGHT = JolCraft.location("textures/gui/sprites/widget/slot_highlighted.png");

    private static final ResourceLocation PROGRESS_TEXTURE1 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress1.png");
    private static final ResourceLocation PROGRESS_TEXTURE2 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress2.png");
    private static final ResourceLocation PROGRESS_TEXTURE3 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress3.png");
    private static final ResourceLocation PROGRESS_TEXTURE4 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress4.png");
    private static final ResourceLocation PROGRESS_TEXTURE5 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress5.png");
    private static final ResourceLocation PROGRESS_TEXTURE6 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress6.png");
    private static final ResourceLocation PROGRESS_TEXTURE7 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress7.png");
    private static final ResourceLocation PROGRESS_TEXTURE8 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress8.png");
    private static final ResourceLocation PROGRESS_TEXTURE9 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress9.png");
    private static final ResourceLocation PROGRESS_TEXTURE10 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress10.png");
    private static final ResourceLocation PROGRESS_TEXTURE11 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress11.png");
    private static final ResourceLocation PROGRESS_TEXTURE12 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress12.png");
    private static final ResourceLocation PROGRESS_TEXTURE13 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress13.png");

    private static final ResourceLocation LOCKPICK_TEXTURE = JolCraft.location("textures/item/lockpick.png");

    private static final ResourceLocation BROKEN_LOCKPICK_TEXTURE1 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_broken1.png");
    private static final ResourceLocation BROKEN_LOCKPICK_TEXTURE2 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_broken2.png");
    private static final ResourceLocation BROKEN_LOCKPICK_TEXTURE3 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_broken3.png");
    private static final ResourceLocation BROKEN_LOCKPICK_TEXTURE4 = JolCraft.location("textures/gui/sprites/lockpick/lockpick_broken4.png");

    private static final ResourceLocation UNLOCK_TEXTURE = JolCraft.location("textures/gui/sprites/lockpick/unlock.png");

    private static final ResourceLocation[] PROGRESS_TEXTURES = {
            PROGRESS_TEXTURE1,
            PROGRESS_TEXTURE2,
            PROGRESS_TEXTURE3,
            PROGRESS_TEXTURE4,
            PROGRESS_TEXTURE5,
            PROGRESS_TEXTURE6,
            PROGRESS_TEXTURE7,
            PROGRESS_TEXTURE8,
            PROGRESS_TEXTURE9,
            PROGRESS_TEXTURE10,
            PROGRESS_TEXTURE11,
            PROGRESS_TEXTURE12,
            PROGRESS_TEXTURE13
    };

    private static final List<ResourceLocation> BROKEN_BUTTON_TEXTURES = List.of(
            BROKEN_LOCKPICK_TEXTURE1, BROKEN_LOCKPICK_TEXTURE2, BROKEN_LOCKPICK_TEXTURE3, BROKEN_LOCKPICK_TEXTURE4
    );

    private ResourceLocation lockpick_broken1 = BROKEN_LOCKPICK_TEXTURE1;
    private ResourceLocation lockpick_broken2 = BROKEN_LOCKPICK_TEXTURE1;


    public LockScreen(LockMenu menu, Inventory playerInventory, Component title) {
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
        int pulse = this.menu.getButtonLayerUpdatePulse();

        guiGraphics.blit(RenderType.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 150);

        if (pulse != lastSeenPulse) {
            lastSeenPulse = pulse;
            guiGraphics.blit(RenderType.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 150);
            updateBrokenLockpickTextures();
            renderLockpickProgress(guiGraphics, x, y);
        }

        if (this.menu.isActive()) {
            renderLockpickProgress(guiGraphics, x, y);
            renderButtonTextures(guiGraphics, x, y, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xDDDDDD, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xDDDDDD, false);
    }

    private void renderLockpickProgress(GuiGraphics guiGraphics, int x, int y) {
        float progress = this.menu.getLockpickProgress();
        if (progress <= 0f) return;

        int step = Math.max(1, Math.min(13, (int)Math.ceil(progress / 10f)));
        ResourceLocation texture = PROGRESS_TEXTURES[step - 1];

        int progressWidth = 108;
        int progressHeight = 15;
        int progressX = x + 34;
        int progressY = y + 16;

        guiGraphics.blit(RenderType.GUI_TEXTURED, texture, progressX, progressY, 0, 0, progressWidth, progressHeight, progressWidth, progressHeight);
    }

    private void updateBrokenLockpickTextures() {
        int id1 = guiRandom.nextInt(BROKEN_BUTTON_TEXTURES.size());
        int id2 = guiRandom.nextInt(BROKEN_BUTTON_TEXTURES.size());
        this.lockpick_broken1 = BROKEN_BUTTON_TEXTURES.get(id1);
        this.lockpick_broken2 = BROKEN_BUTTON_TEXTURES.get(id2);
    }

    private void renderButtonTextures(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        int correctButtonId = this.menu.getCorrectButtonId();
        int unlockSlot = this.menu.getUnlockSlotId();
        int[] buttonXs = { x + 48, x + 80, x + 112 };
        int buttonY = y + 31;

        ResourceLocation[] wrongs = {lockpick_broken1, lockpick_broken2};
        int wrongIdx = 0;

        if (correctButtonId == 3 && unlockSlot >= 0 && unlockSlot < 3) {
            for (int idx = 0; idx < 3; idx++) {
                int bx = buttonXs[idx], bw = 16, bh = 16, hw = 17, hh = 17;
                boolean hovered = mouseX >= bx && mouseY >= buttonY && mouseX < bx + bw && mouseY < buttonY + bh;
                if (hovered) {
                    guiGraphics.blit(RenderType.GUI_TEXTURED, HIGHLIGHT, bx, buttonY, 0, 0, hw, hh, hw, hh);
                }
                ResourceLocation texture = (idx == unlockSlot)
                        ? UNLOCK_TEXTURE
                        : wrongs[wrongIdx++];
                guiGraphics.blit(RenderType.GUI_TEXTURED, texture, bx, buttonY, 0, 0, bw, bh, bw, bh);
            }
        } else {
            for (int idx = 0; idx < 3; idx++) {
                int bx = buttonXs[idx], bw = 16, bh = 16, hw = 17, hh = 17;
                boolean hovered = mouseX >= bx && mouseY >= buttonY && mouseX < bx + bw && mouseY < buttonY + bh;
                if (hovered) {
                    guiGraphics.blit(RenderType.GUI_TEXTURED, HIGHLIGHT, bx, buttonY, 0, 0, hw, hh, hw, hh);
                }
                ResourceLocation texture = (idx == correctButtonId)
                        ? LOCKPICK_TEXTURE
                        : wrongs[wrongIdx++];
                guiGraphics.blit(RenderType.GUI_TEXTURED, texture, bx, buttonY, 0, 0, bw, bh, bw, bh);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (button == 0) {
            for (int idx = 0; idx < 3; idx++) {
                int btnX = x + 48 + idx * 32;
                int btnY = y + 31;
                int btnW = 16, btnH = 16;

                if (mouseX >= btnX && mouseY >= btnY && mouseX < btnX + btnW && mouseY < btnY + btnH && this.menu.isActive()) {
                    assert Objects.requireNonNull(this.minecraft).gameMode != null;
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, idx);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

}
