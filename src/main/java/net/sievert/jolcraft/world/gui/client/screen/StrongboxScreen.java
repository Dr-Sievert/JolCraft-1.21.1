package net.sievert.jolcraft.world.gui.client.screen;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.gui.menu.StrongboxMenu;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class StrongboxScreen
        extends JolCraftScreen<StrongboxMenu> {

    private static final int STRONGBOX_SIZE = 18;

    public StrongboxScreen(
            StrongboxMenu menu,
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

        for (int slotIndex = 0;
             slotIndex < STRONGBOX_SIZE;
             slotIndex++) {
            renderSlotBackground(
                    guiGraphics,
                    this.menu.getSlot(
                            slotIndex
                    )
            );
        }
    }
}