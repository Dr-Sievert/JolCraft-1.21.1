package net.sievert.jolcraft.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.effect.PoisonMobEffect")
public abstract class PoisonMobEffectMixin {

    @Inject(
            method = "applyEffectTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void jolcraft$applyPoisonResistance(
            LivingEntity entity,
            int amplifier,
            CallbackInfoReturnable<Boolean> cir
    ) {
        double resistance = entity.getAttributeValue(JolCraftAttributes.POISON_RESISTANCE);

        if (resistance <= 0.0D) {
            return;
        }

        if (resistance >= 1.0D || entity.getRandom().nextDouble() < resistance) {
            cir.setReturnValue(true);
        }
    }
}