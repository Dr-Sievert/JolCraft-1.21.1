package net.sievert.jolcraft.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Inject(method = "checkAndPerformAttack", at = @At("HEAD"), cancellable = true)
    private void jolcraft$blockWeaponAttack(LivingEntity target, CallbackInfo ci) {
        if (!mob.hasEffect(JolCraftEffects.DISARMED) && !mob.hasEffect(JolCraftEffects.STUNNED)) return;
        if (!JolCraftEquipmentHelper.isWeapon(mob.getMainHandItem())) return;
        ci.cancel();
    }
}