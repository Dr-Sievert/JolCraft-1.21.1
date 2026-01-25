package net.sievert.jolcraft.world.gui.custom.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.gui.custom.menu.LockMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class LockScreen extends AbstractContainerScreen<LockMenu> {

    // ---------------------------------------------------------------------
    // Textures
    // ---------------------------------------------------------------------

    private static final ResourceLocation TEXTURE =
            JolCraft.location("textures/gui/container/strongbox_lock.png");

    private static final ResourceLocation HIGHLIGHT =
            JolCraft.location("textures/gui/sprites/widget/slot_highlighted.png");

    private static final ResourceLocation LOCKPICK_TEXTURE =
            JolCraft.location("textures/item/lockpick.png");

    private static final ResourceLocation UNLOCK_TEXTURE =
            JolCraft.location("textures/gui/sprites/lockpick/unlock.png");

    private static final ResourceLocation[] PROGRESS_TEXTURES = {
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress1.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress2.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress3.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress4.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress5.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress6.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress7.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress8.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress9.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress10.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress11.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress12.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_progress13.png")
    };

    private static final List<ResourceLocation> BROKEN_BUTTON_TEXTURES = List.of(
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_broken1.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_broken2.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_broken3.png"),
            JolCraft.location("textures/gui/sprites/lockpick/lockpick_broken4.png")
    );

    // ---------------------------------------------------------------------
    // Layout
    // ---------------------------------------------------------------------

    private static final int TEX_W = 176;
    private static final int TEX_H = 150;

    private static final int PROGRESS_X = 34;
    private static final int PROGRESS_Y = 16;
    private static final int PROGRESS_W = 108;
    private static final int PROGRESS_H = 15;

    private static final int BUTTON_Y = 31;
    private static final int BUTTON_W = 16;
    private static final int BUTTON_H = 16;

    private static final int HIGHLIGHT_W = 17;
    private static final int HIGHLIGHT_H = 17;

    private static final int BUTTON_START_X = 48;
    private static final int BUTTON_SPACING_X = 32;

    // ---------------------------------------------------------------------
    // Runtime state
    // ---------------------------------------------------------------------

    private final Random guiRandom = new Random();

    private int lastSeenPulse = -1;
    private ResourceLocation brokenTexA = BROKEN_BUTTON_TEXTURES.getFirst();
    private ResourceLocation brokenTexB = BROKEN_BUTTON_TEXTURES.getFirst();

    public LockScreen(LockMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;

        this.titleLabelY = 6;
        this.inventoryLabelY = 56;
    }

    // ---------------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------------

    @Override
    public void render(@NotNull GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        super.render(gg, mouseX, mouseY, partialTicks);
        renderTooltip(gg, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics gg, float partialTicks, int mouseX, int mouseY) {
        int x = leftPos();
        int y = topPos();

        // Base panel
        gg.blit(RenderType.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, TEX_W, TEX_H);

        // Reroll the "broken" overlays only when server says "layers changed"
        int pulse = menu.getButtonLayerUpdatePulse();
        if (pulse != lastSeenPulse) {
            lastSeenPulse = pulse;
            rerollBrokenTextures();
        }

        if (!isLockpickInserted()) return;

        renderLockpickProgress(gg, x, y);
        renderButtons(gg, x, y, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gg, int mouseX, int mouseY) {
        gg.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xDDDDDD, false);
        gg.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xDDDDDD, false);
    }

    private void renderLockpickProgress(GuiGraphics gg, int x, int y) {
        int progress = menu.getLockpickProgress();
        if (progress <= 0) return;

        int step = Math.max(1, Math.min(PROGRESS_TEXTURES.length, (int) Math.ceil(progress / 10.0)));
        ResourceLocation texture = PROGRESS_TEXTURES[step - 1];

        gg.blit(RenderType.GUI_TEXTURED, texture,
                x + PROGRESS_X, y + PROGRESS_Y,
                0, 0,
                PROGRESS_W, PROGRESS_H,
                PROGRESS_W, PROGRESS_H);
    }

    private void renderButtons(GuiGraphics gg, int x, int y, int mouseX, int mouseY) {
        int correctButtonId = menu.getCorrectButtonId();
        int unlockSlot = menu.getUnlockSlotId();
        boolean unlockMode = (correctButtonId == 3 && unlockSlot >= 0 && unlockSlot < 3);

        ResourceLocation[] wrongs = { brokenTexA, brokenTexB };
        int wrongIdx = 0;

        int by = y + BUTTON_Y;

        for (int idx = 0; idx < 3; idx++) {
            int bx = x + BUTTON_START_X + idx * BUTTON_SPACING_X;

            if (isHovered(mouseX, mouseY, bx, by)) {
                gg.blit(RenderType.GUI_TEXTURED, HIGHLIGHT, bx, by, 0, 0, HIGHLIGHT_W, HIGHLIGHT_H, HIGHLIGHT_W, HIGHLIGHT_H);
            }

            ResourceLocation tex;
            if (unlockMode) {
                tex = (idx == unlockSlot) ? UNLOCK_TEXTURE : wrongs[wrongIdx++];
            } else {
                tex = (idx == correctButtonId) ? LOCKPICK_TEXTURE : wrongs[wrongIdx++];
            }

            gg.blit(RenderType.GUI_TEXTURED, tex, bx, by, 0, 0, BUTTON_W, BUTTON_H, BUTTON_W, BUTTON_H);
        }
    }

    // ---------------------------------------------------------------------
    // Click handling
    // ---------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isLockpickInserted() && this.minecraft != null && this.minecraft.gameMode != null) {
            int x = leftPos();
            int y = topPos();

            int by = y + BUTTON_Y;

            for (int idx = 0; idx < 3; idx++) {
                int bx = x + BUTTON_START_X + idx * BUTTON_SPACING_X;

                if (isHovered(mouseX, mouseY, bx, by)) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, idx);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private int leftPos() {
        return (this.width - this.imageWidth) / 2;
    }

    private int topPos() {
        return (this.height - this.imageHeight) / 2;
    }

    private boolean isLockpickInserted() {
        ItemStack stack = this.menu.getLockpickSlotItem();
        return !stack.isEmpty();
    }

    private void rerollBrokenTextures() {
        brokenTexA = BROKEN_BUTTON_TEXTURES.get(guiRandom.nextInt(BROKEN_BUTTON_TEXTURES.size()));
        brokenTexB = BROKEN_BUTTON_TEXTURES.get(guiRandom.nextInt(BROKEN_BUTTON_TEXTURES.size()));
    }

    private static boolean isHovered(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseY >= y && mouseX < x + LockScreen.BUTTON_W && mouseY < y + LockScreen.BUTTON_H;
    }
}