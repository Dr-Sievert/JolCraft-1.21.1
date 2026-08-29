package net.sievert.jolcraft.world.gui.client.screen;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.block.entity.custom.MortarBlockEntity;
import net.sievert.jolcraft.world.gui.client.util.JolCraftProgressRenderer;
import net.sievert.jolcraft.world.gui.menu.MortarMenu;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class MortarScreen
        extends JolCraftScreen<MortarMenu> {

    private static final ItemLike TOOL_TEXTURE =
            JolCraftItems.DEEPSLATE_PESTLE;

    private static final int PROGRESS_TILE_X = 2;
    private static final int PROGRESS_TILE_Y = 5;
    private static final int PROGRESS_LENGTH_TILES = 8;
    private static final int PROGRESS_START_PADDING = 7;
    private static final int PROGRESS_END_PADDING = 7;

    private static final int TOOL_BUTTON_TILE_X = 10;
    private static final int TOOL_BUTTON_TILE_Y = 3;

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

        renderSlotBackground(
                guiGraphics,
                this.menu.getSlot(
                        MortarBlockEntity.SLOT_INPUT_1
                )
        );

        renderSlotBackground(
                guiGraphics,
                this.menu.getSlot(
                        MortarBlockEntity.SLOT_INPUT_2
                )
        );

        renderSlotBackground(
                guiGraphics,
                this.menu.getSlot(
                        MortarBlockEntity.SLOT_INPUT_3
                )
        );

        renderSlotBackground(
                guiGraphics,
                this.menu.getSlot(
                        MortarBlockEntity.SLOT_OUTPUT
                )
        );

        renderToolSlot(
                guiGraphics,
                this.menu.getSlot(
                        MortarBlockEntity.SLOT_TOOL
                ),
                TOOL_TEXTURE
        );

        JolCraftProgressRenderer.render(
                guiGraphics,
                this.leftPos,
                this.topPos,
                PROGRESS_TILE_X,
                PROGRESS_TILE_Y,
                PROGRESS_LENGTH_TILES,
                PROGRESS_START_PADDING,
                PROGRESS_END_PADDING,
                this.menu.getGrindingProgress()
        );

        if (this.menu.hasTool()) {
            renderItemButton(
                    guiGraphics,
                    TOOL_BUTTON_TILE_X,
                    TOOL_BUTTON_TILE_Y,
                    this.menu.getToolStack(),
                    this.menu.isButtonActive(),
                    mouseX,
                    mouseY
            );
        }
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button == 0
                && this.menu.hasTool()
                && this.menu.isButtonActive()
                && isOverButton(
                mouseX,
                mouseY,
                TOOL_BUTTON_TILE_X,
                TOOL_BUTTON_TILE_Y
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

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }
}