package net.sievert.jolcraft.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BrushItem.class)
public abstract class BrushItemMixin {

    @ModifyConstant(
            method = "onUseTick",
            constant = @Constant(intValue = 10)
    )
    private int jolcraft$adjustBrushInterval(
            int original,
            Level level,
            LivingEntity livingEntity,
            ItemStack stack,
            int remainingUseDuration
    ) {
        return jolcraft$getAdjustedBrushInterval(original, livingEntity);
    }

    @ModifyConstant(
            method = "onUseTick",
            constant = @Constant(intValue = 5)
    )
    private int jolcraft$adjustBrushOffset(
            int original,
            Level level,
            LivingEntity livingEntity,
            ItemStack stack,
            int remainingUseDuration
    ) {
        int interval = jolcraft$getAdjustedBrushInterval(10, livingEntity);
        return interval / 2;
    }

    @Unique
    private static int jolcraft$getAdjustedBrushInterval(int original, LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player)) {
            return original;
        }

        double speed = Mth.clamp(player.getAttributeValue(JolCraftAttributes.ITEM_USE_SPEED), 0.0D, 1.0D);
        if (speed <= 0.0D) {
            return original;
        }

        return Math.max(1, (int) Math.round(original / (1.0D + speed)));
    }
}