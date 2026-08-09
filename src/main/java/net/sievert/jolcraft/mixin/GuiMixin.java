package net.sievert.jolcraft.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.world.entity.attachment.custom.overheal.OverhealAttachmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    private void renderHeart(
            GuiGraphics guiGraphics,
            Gui.HeartType heartType,
            int x,
            int y,
            boolean hardcore,
            boolean halfHeart,
            boolean blinking
    ) {}

    @Inject(
            method = "renderHearts",
            at = @At("TAIL")
    )
    private void jolcraft$renderOverhealHearts(
            GuiGraphics guiGraphics,
            Player player,
            int x,
            int y,
            int height,
            int offsetHeartIndex,
            float maxHealth,
            int currentHealth,
            int displayHealth,
            int absorptionAmount,
            boolean renderHighlight,
            CallbackInfo ci
    ) {
        int overhealAmount = Mth.ceil(OverhealAttachmentHelper.getAmount(player));
        if (overhealAmount <= 0) return;

        int healthHearts = Mth.ceil(maxHealth / 2.0F);
        int absorptionHearts = Mth.ceil(absorptionAmount / 2.0F);
        int overhealHearts = Mth.ceil(overhealAmount / 2.0F);

        int firstOverhealHeart = healthHearts + absorptionHearts;
        boolean hardcore = player.level().getLevelData().isHardcore();
        Gui.HeartType overhealHeartType = JolCraftEnumExtensions.HeartType.OVERHEAL.getValue();

        for (int index = overhealHearts - 1; index >= 0; index--) {
            int heartIndex = firstOverhealHeart + index;
            int row = heartIndex / 10;
            int column = heartIndex % 10;

            int heartX = x + column * 8;
            int heartY = y - row * height;

            renderHeart(
                    guiGraphics,
                    Gui.HeartType.CONTAINER,
                    heartX,
                    heartY,
                    hardcore,
                    false,
                    false
            );

            int overhealIndex = index * 2;
            boolean halfHeart = overhealIndex + 1 == overhealAmount;

            renderHeart(
                    guiGraphics,
                    overhealHeartType,
                    heartX,
                    heartY,
                    hardcore,
                    false,
                    halfHeart
            );
        }
    }
}