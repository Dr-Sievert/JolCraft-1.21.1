package net.sievert.jolcraft.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {

    @ModifyConstant(
            method = "brush",
            constant = @Constant(longValue = 10L)
    )
    private long jolcraft$adjustBrushCooldown(long original, long startTick, Player player, net.minecraft.core.Direction hitDirection) {
        double speed = Mth.clamp(player.getAttributeValue(JolCraftAttributes.ITEM_USE_SPEED), 0.0D, 1.0D);
        if (speed <= 0.0D) {
            return original;
        }

        return Math.max(1L, Math.round(original / (1.0D + speed)));
    }
}