package net.sievert.jolcraft.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedBowAttackGoal.class)
public abstract class RangedBowAttackGoalMixin<T extends Mob & RangedAttackMob> {

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
    private void jolcraft$blockBowRaise(CallbackInfo ci) {
        if (!jolcraft$shouldBlockBowAttack()) return;
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
    private void jolcraft$blockBowAttack(CallbackInfo ci) {
        if (!jolcraft$shouldBlockBowAttack()) return;

        if (mob.isUsingItem()) {
            mob.stopUsingItem();
        }

        ci.cancel();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Unique
    private boolean jolcraft$shouldBlockBowAttack() {
        if (!mob.hasEffect(JolCraftEffects.DISARMED) && !mob.hasEffect(JolCraftEffects.STUNNED)) {
            return false;
        }

        return JolCraftEquipmentHelper.isWeapon(mob.getMainHandItem());
    }
}