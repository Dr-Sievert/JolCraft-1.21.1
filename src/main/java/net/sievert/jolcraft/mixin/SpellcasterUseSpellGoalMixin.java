package net.sievert.jolcraft.mixin;

import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.SpellcasterIllager$SpellcasterUseSpellGoal")
public abstract class SpellcasterUseSpellGoalMixin {

    @Shadow(aliases = "this$0")
    @Final
    private SpellcasterIllager jolcraft$spellcaster;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void jolcraft$blockSpellStart(CallbackInfoReturnable<Boolean> cir) {
        if (jolcraft$spellcaster.hasEffect(JolCraftEffects.STUNNED)
                || jolcraft$spellcaster.hasEffect(JolCraftEffects.SUPPRESSED)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void jolcraft$blockSpellTick(CallbackInfo ci) {
        if (!jolcraft$spellcaster.hasEffect(JolCraftEffects.STUNNED)
                && !jolcraft$spellcaster.hasEffect(JolCraftEffects.SUPPRESSED)) {
            return;
        }

        ci.cancel();
    }
}