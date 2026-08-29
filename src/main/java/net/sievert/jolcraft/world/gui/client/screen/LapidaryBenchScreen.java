package net.sievert.jolcraft.world.gui.client.screen;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.block.entity.custom.LapidaryBenchBlockEntity;
import net.sievert.jolcraft.world.gui.menu.LapidaryBenchMenu;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class LapidaryBenchScreen
        extends JolCraftScreen<LapidaryBenchMenu> {

    private static final int TOOL_BUTTON_TILE_X = 6;
    private static final int TOOL_BUTTON_TILE_Y = 2;
    private static final ItemLike TOOL_TEXTURE = JolCraftItems.DEEPSLATE_ARTISAN_HAMMER;

    public LapidaryBenchScreen(
            LapidaryBenchMenu menu,
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
                        LapidaryBenchBlockEntity.SLOT_INPUT
                )
        );

        renderToolSlot(
                guiGraphics,
                this.menu.getSlot(
                        LapidaryBenchBlockEntity.SLOT_TOOL
                ),
                TOOL_TEXTURE
        );

        renderSlotBackground(
                guiGraphics,
                this.menu.getSlot(
                        LapidaryBenchBlockEntity.SLOT_OUTPUT
                )
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
            int actionId =
                    this.menu.getActionIdForTool();

            if (actionId >= 0
                    && this.minecraft != null
                    && this.minecraft.gameMode != null) {
                this.minecraft.gameMode
                        .handleInventoryButtonClick(
                                this.menu.containerId,
                                actionId
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