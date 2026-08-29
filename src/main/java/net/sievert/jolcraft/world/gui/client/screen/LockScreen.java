package net.sievert.jolcraft.world.gui.client.screen;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.gui.menu.JolCraftMenu;
import net.sievert.jolcraft.world.gui.menu.LockMenu;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class LockScreen extends JolCraftScreen<LockMenu> {

    private static ResourceLocation modLockpickSprite(
            String sprite
    ) {
        return JolCraftTextures.modSprite(
                JolCraftItemIds.LOCKPICK,
                sprite
        );
    }

    private static final ResourceLocation HIGHLIGHT_SPRITE =
            JolCraftTextures.modWidget(
                    JolCraftStrings.underscored(
                            JolCraftDictionary.SLOT,
                            JolCraftDictionary.HIGHLIGHTED
                    )
            );

    private static final ResourceLocation UNLOCK_SPRITE =
            modLockpickSprite(
                    JolCraftDictionary.UNLOCK
            );

    private static final String LOCKPICK_PROGRESS =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LOCKPICK,
                    JolCraftDictionary.PROGRESS
            );

    private static final String LOCKPICK_BROKEN =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LOCKPICK,
                    JolCraftDictionary.BROKEN
            );

    private static final ResourceLocation[] PROGRESS_SPRITES = {
            modLockpickSprite(LOCKPICK_PROGRESS + "0"),
            modLockpickSprite(LOCKPICK_PROGRESS + "1"),
            modLockpickSprite(LOCKPICK_PROGRESS + "2"),
            modLockpickSprite(LOCKPICK_PROGRESS + "3"),
            modLockpickSprite(LOCKPICK_PROGRESS + "4"),
            modLockpickSprite(LOCKPICK_PROGRESS + "5"),
            modLockpickSprite(LOCKPICK_PROGRESS + "6"),
            modLockpickSprite(LOCKPICK_PROGRESS + "7"),
            modLockpickSprite(LOCKPICK_PROGRESS + "8"),
            modLockpickSprite(LOCKPICK_PROGRESS + "9"),
            modLockpickSprite(LOCKPICK_PROGRESS + "10"),
            modLockpickSprite(LOCKPICK_PROGRESS + "11"),
            modLockpickSprite(LOCKPICK_PROGRESS + "12"),
            modLockpickSprite(LOCKPICK_PROGRESS + "13")
    };

    private static final List<ResourceLocation> BROKEN_LOCKPICK_SPRITES =
            List.of(
                    modLockpickSprite(LOCKPICK_BROKEN + "1"),
                    modLockpickSprite(LOCKPICK_BROKEN + "2"),
                    modLockpickSprite(LOCKPICK_BROKEN + "3"),
                    modLockpickSprite(LOCKPICK_BROKEN + "4")
            );

    private static final int PROGRESS_X = 34;
    private static final int PROGRESS_Y = 32;
    private static final int PROGRESS_WIDTH = 108;
    private static final int PROGRESS_HEIGHT = 15;

    private static final int BUTTON_TILE_Y = 3;
    private static final int BUTTON_Y_PADDING = 15;

    private static final int BUTTON_WIDTH = 16;
    private static final int BUTTON_HEIGHT = 16;

    private static final int HIGHLIGHT_WIDTH = 17;
    private static final int HIGHLIGHT_HEIGHT = 17;

    private static final int BUTTON_START_TILE_X = 4;
    private static final int BUTTON_SPACING = 32;

    private final Random guiRandom =
            new Random();

    private int lastSeenPulse = -1;

    private ResourceLocation brokenTexA =
            BROKEN_LOCKPICK_SPRITES.getFirst();

    private ResourceLocation brokenTexB =
            BROKEN_LOCKPICK_SPRITES.getFirst();

    public LockScreen(
            LockMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(
                menu,
                playerInventory,
                title
        );
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTicks,
            int mouseX,
            int mouseY
    ) {
        super.renderBg(
                guiGraphics,
                partialTicks,
                mouseX,
                mouseY
        );

        renderSlotBackgroundAt(
                guiGraphics,
                JolCraftMenu.tile(10),
                JolCraftMenu.tile(3)
        );

        guiGraphics.blitSprite(
                UNLOCK_SPRITE,
                this.leftPos + JolCraftMenu.tile(10),
                this.topPos + JolCraftMenu.tile(3),
                16,
                16
        );

        renderToolSlot(
                guiGraphics,
                this.menu.getSlot(0),
                JolCraftItems.LOCKPICK
        );

        int pulse =
                this.menu.getButtonLayerUpdatePulse();

        if (pulse != this.lastSeenPulse) {
            this.lastSeenPulse =
                    pulse;

            rerollBrokenTextures();
        }

        if (!isLockpickInserted()) {
            return;
        }

        renderLockpickProgress(
                guiGraphics
        );

        renderButtons(
                guiGraphics,
                mouseX,
                mouseY
        );
    }

    private void renderLockpickProgress(
            GuiGraphics guiGraphics
    ) {
        int progress =
                this.menu.getLockpickProgress();

        int step =
                progress <= 0
                        ? 0
                        : Math.min(
                        PROGRESS_SPRITES.length - 1,
                        (int) Math.ceil(
                                progress / 10.0
                        )
                );

        ResourceLocation polishedDeepslate =
                ResourceLocation.withDefaultNamespace(
                        "textures/block/polished_deepslate.png"
                );

        for (int x = 0; x < PROGRESS_WIDTH; x += 16) {
            int width =
                    Math.min(
                            16,
                            PROGRESS_WIDTH - x
                    );

            guiGraphics.blit(
                    polishedDeepslate,
                    this.leftPos + PROGRESS_X + x,
                    this.topPos + PROGRESS_Y,
                    0.0F,
                    0.0F,
                    width,
                    PROGRESS_HEIGHT,
                    16,
                    16
            );
        }

        guiGraphics.blitSprite(
                PROGRESS_SPRITES[step],
                this.leftPos + PROGRESS_X,
                this.topPos + PROGRESS_Y,
                PROGRESS_WIDTH,
                PROGRESS_HEIGHT
        );
    }

    private void renderButtons(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        int correctButtonId =
                this.menu.getCorrectButtonId();

        int unlockSlot =
                this.menu.getUnlockSlotId();

        boolean unlockMode =
                correctButtonId == 3
                        && unlockSlot >= 0
                        && unlockSlot < 3;

        ResourceLocation[] wrongs = {
                this.brokenTexA,
                this.brokenTexB
        };

        int wrongIndex = 0;

        int localButtonY =
                JolCraftMenu.tile(
                        BUTTON_TILE_Y
                )
                        + BUTTON_Y_PADDING;

        int buttonY =
                this.topPos
                        + localButtonY;

        for (int index = 0;
             index < 3;
             index++) {
            int localButtonX =
                    JolCraftMenu.tile(
                            BUTTON_START_TILE_X
                    )
                            + index * BUTTON_SPACING;

            int buttonX =
                    this.leftPos
                            + localButtonX;

            renderSlotBackgroundAt(
                    guiGraphics,
                    localButtonX,
                    localButtonY
            );

            if (isHovered(
                    mouseX,
                    mouseY,
                    buttonX,
                    buttonY
            )) {
                guiGraphics.blitSprite(
                        HIGHLIGHT_SPRITE,
                        buttonX,
                        buttonY,
                        HIGHLIGHT_WIDTH,
                        HIGHLIGHT_HEIGHT
                );
            }

            ResourceLocation sprite = null;
            boolean drawItem = false;

            if (unlockMode) {
                if (index == unlockSlot) {
                    sprite =
                            UNLOCK_SPRITE;
                } else {
                    sprite =
                            wrongs[
                                    wrongIndex++
                                    ];
                }
            } else if (index == correctButtonId) {
                drawItem = true;
            } else {
                sprite =
                        wrongs[
                                wrongIndex++
                                ];
            }

            if (drawItem) {
                ItemStack lockpick =
                        this.menu.getLockpickSlotItem();

                guiGraphics.renderFakeItem(
                        lockpick,
                        buttonX,
                        buttonY
                );

                guiGraphics.renderItemDecorations(
                        this.font,
                        lockpick,
                        buttonX,
                        buttonY
                );
            } else {
                guiGraphics.blitSprite(
                        sprite,
                        buttonX,
                        buttonY,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                );
            }
        }
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button == 0
                && isLockpickInserted()
                && this.minecraft != null
                && this.minecraft.gameMode != null) {
            int buttonY =
                    this.topPos
                            + JolCraftMenu.tile(
                            BUTTON_TILE_Y
                    )
                            + BUTTON_Y_PADDING;

            for (int index = 0;
                 index < 3;
                 index++) {
                int buttonX =
                        this.leftPos
                                + JolCraftMenu.tile(
                                BUTTON_START_TILE_X
                        )
                                + index * BUTTON_SPACING;

                if (isHovered(
                        mouseX,
                        mouseY,
                        buttonX,
                        buttonY
                )) {
                    this.minecraft.gameMode
                            .handleInventoryButtonClick(
                                    this.menu.containerId,
                                    index
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

    private boolean isLockpickInserted() {
        return !this.menu
                .getLockpickSlotItem()
                .isEmpty();
    }

    private void rerollBrokenTextures() {
        this.brokenTexA =
                BROKEN_LOCKPICK_SPRITES.get(
                        this.guiRandom.nextInt(
                                BROKEN_LOCKPICK_SPRITES.size()
                        )
                );

        this.brokenTexB =
                BROKEN_LOCKPICK_SPRITES.get(
                        this.guiRandom.nextInt(
                                BROKEN_LOCKPICK_SPRITES.size()
                        )
                );
    }

    private static boolean isHovered(
            double mouseX,
            double mouseY,
            int x,
            int y
    ) {
        return mouseX >= x
                && mouseY >= y
                && mouseX < x + BUTTON_WIDTH
                && mouseY < y + BUTTON_HEIGHT;
    }
}