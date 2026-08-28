package net.sievert.jolcraft.world.gui.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.gui.menu.LockMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class LockScreen extends AbstractContainerScreen<LockMenu> {

    // ---------------------------------------------------------------------
    // Textures
    // ---------------------------------------------------------------------

    private static ResourceLocation modTradeSprite(String sprite) {
        return JolCraftTextures.modSprite(JolCraftItemIds.LOCKPICK, sprite);
    }

    private static final ResourceLocation TEXTURE = JolCraftTextures.mod(JolCraftTextures.container(
            JolCraftStrings.underscored(JolCraftBlockIds.STRONGBOX, JolCraftDictionary.LOCK)));

    private static final ResourceLocation HIGHLIGHT_SPRITE =
            JolCraftTextures.modWidget(JolCraftStrings.underscored(
                    JolCraftDictionary.SLOT,
                    JolCraftDictionary.HIGHLIGHTED
            ));

    private static final ResourceLocation UNLOCK_SPRITE = modTradeSprite(JolCraftDictionary.UNLOCK);

    private static final String LOCKPICK_PROGRESS = JolCraftStrings.underscored(JolCraftDictionary.LOCKPICK, JolCraftDictionary.PROGRESS);
    private static final String LOCKPICK_BROKEN = JolCraftStrings.underscored(JolCraftDictionary.LOCKPICK, JolCraftDictionary.BROKEN);

    private static final ResourceLocation[] PROGRESS_SPRITES = {
            modTradeSprite(LOCKPICK_PROGRESS + "1"),
            modTradeSprite(LOCKPICK_PROGRESS + "2"),
            modTradeSprite(LOCKPICK_PROGRESS + "3"),
            modTradeSprite(LOCKPICK_PROGRESS + "4"),
            modTradeSprite(LOCKPICK_PROGRESS + "5"),
            modTradeSprite(LOCKPICK_PROGRESS + "6"),
            modTradeSprite(LOCKPICK_PROGRESS + "7"),
            modTradeSprite(LOCKPICK_PROGRESS + "8"),
            modTradeSprite(LOCKPICK_PROGRESS + "9"),
            modTradeSprite(LOCKPICK_PROGRESS + "10"),
            modTradeSprite(LOCKPICK_PROGRESS + "11"),
            modTradeSprite(LOCKPICK_PROGRESS + "12"),
            modTradeSprite(LOCKPICK_PROGRESS + "13")
    };

    private static final List<ResourceLocation> BROKEN_LOCKPICK_SPRITES = List.of(
            modTradeSprite(LOCKPICK_BROKEN + "1"),
            modTradeSprite(LOCKPICK_BROKEN + "2"),
            modTradeSprite(LOCKPICK_BROKEN + "3"),
            modTradeSprite(LOCKPICK_BROKEN + "4")
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
    private ResourceLocation brokenTexA = BROKEN_LOCKPICK_SPRITES.getFirst();
    private ResourceLocation brokenTexB = BROKEN_LOCKPICK_SPRITES.getFirst();

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
        gg.blit(TEXTURE, x, y, 0, 0.0F, 0.0F, imageWidth, imageHeight, TEX_W, TEX_H);

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
        gg.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, JolCraftColors.rgb("DDDDDD"), false);
        gg.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, JolCraftColors.rgb("DDDDDD"), false);
    }

    private void renderLockpickProgress(GuiGraphics gg, int x, int y) {
        int progress = menu.getLockpickProgress();
        if (progress <= 0) return;

        int step = Math.max(1, Math.min(PROGRESS_SPRITES.length, (int) Math.ceil(progress / 10.0)));
        ResourceLocation texture = PROGRESS_SPRITES[step - 1];

        gg.blitSprite(
                texture,
                x + PROGRESS_X,
                y + PROGRESS_Y,
                PROGRESS_W,
                PROGRESS_H
        );
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
                gg.blitSprite(HIGHLIGHT_SPRITE, bx, by, HIGHLIGHT_W, HIGHLIGHT_H);
            }

            ResourceLocation sprite = null;
            boolean drawItem = false;

            if (unlockMode) {
                sprite = (idx == unlockSlot) ? UNLOCK_SPRITE : wrongs[wrongIdx++];
            } else {
                if (idx == correctButtonId) {
                    drawItem = true;
                } else {
                    sprite = wrongs[wrongIdx++];
                }
            }

            if (drawItem) {
                ItemStack lockpick = menu.getLockpickSlotItem();
                gg.renderFakeItem(lockpick, bx, by);
                gg.renderItemDecorations(this.font, lockpick, bx, by);
            } else {
                gg.blitSprite(sprite, bx, by, BUTTON_W, BUTTON_H);
            }
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
        brokenTexA = BROKEN_LOCKPICK_SPRITES.get(guiRandom.nextInt(BROKEN_LOCKPICK_SPRITES.size()));
        brokenTexB = BROKEN_LOCKPICK_SPRITES.get(guiRandom.nextInt(BROKEN_LOCKPICK_SPRITES.size()));
    }

    private static boolean isHovered(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseY >= y && mouseX < x + LockScreen.BUTTON_W && mouseY < y + LockScreen.BUTTON_H;
    }
}
