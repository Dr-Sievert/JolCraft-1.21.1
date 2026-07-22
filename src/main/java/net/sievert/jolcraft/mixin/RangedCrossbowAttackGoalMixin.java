package net.sievert.jolcraft.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedCrossbowAttackGoal.class)
public abstract class RangedCrossbowAttackGoalMixin<T extends Mob & CrossbowAttackMob> {

    @Shadow
    @Final
    private T mob;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"
            ),
            cancellable = true
    )
    private void jolcraft$blockCrossbowRaise(CallbackInfo ci) {
        if (!jolcraft$shouldBlockCrossbowAttack()) return;
        ci.cancel();
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/RangedAttackMob;performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"
            ),
            cancellable = true
    )
    private void jolcraft$blockCrossbowAttack(CallbackInfo ci) {
        if (!jolcraft$shouldBlockCrossbowAttack()) return;

        if (mob.isUsingItem()) {
            mob.stopUsingItem();
        }

        mob.setChargingCrossbow(false);
        ci.cancel();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Unique
    private boolean jolcraft$shouldBlockCrossbowAttack() {
        if (!mob.hasEffect(JolCraftEffects.DISARMED) && !mob.hasEffect(JolCraftEffects.STUNNED)) {
            return false;
        }

        return JolCraftEquipmentHelper.isWeapon(mob.getMainHandItem());
    }
}