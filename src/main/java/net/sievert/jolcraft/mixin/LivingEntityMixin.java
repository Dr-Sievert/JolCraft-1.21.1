package net.sievert.jolcraft.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z"
            )
    )
    private boolean jolcraft$removeFireResistanceImmunity(
            LivingEntity instance,
            Holder<MobEffect> effect
    ) {
        if (effect == MobEffects.FIRE_RESISTANCE) {
            return false;
        }

        return instance.hasEffect(effect);
    }
}