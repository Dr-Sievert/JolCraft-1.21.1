package net.sievert.jolcraft.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.attachment.custom.overheal.OverhealAttachmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @SuppressWarnings("deprecation")
    @Inject(
            method = "onAttributeUpdated",
            at = @At("TAIL")
    )
    private void jolcraft$clampOverheal(
            Holder<Attribute> attribute,
            CallbackInfo ci
    ) {
        if (!attribute.is(Attributes.MAX_HEALTH)
                && !attribute.is(JolCraftAttributes.MAX_OVERHEAL)) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        OverhealAttachmentHelper.setAmount(
                entity,
                OverhealAttachmentHelper.getAmount(entity)
        );
    }
}