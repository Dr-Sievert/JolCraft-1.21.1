package net.sievert.jolcraft.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyArg(
            method = "igniteForTicks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setRemainingFireTicks(I)V"
            ),
            index = 0
    )
    private int jolcraft$reduceBurnDurationWithFireResistance(int fireTicks) {
        Entity entity = (Entity) (Object) this;

        if (!(entity instanceof LivingEntity livingEntity)) {
            return fireTicks;
        }

        double resistance = livingEntity.getAttributeValue(JolCraftAttributes.FIRE_RESISTANCE);

        if (resistance <= 0.0D) {
            return fireTicks;
        }

        if (resistance >= 1.0D) {
            return 0;
        }

        return Math.max(0, Mth.ceil(fireTicks * (1.0D - resistance)));
    }
}