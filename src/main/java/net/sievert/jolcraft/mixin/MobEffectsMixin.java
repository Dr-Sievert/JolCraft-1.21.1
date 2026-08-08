package net.sievert.jolcraft.mixin;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.attribute.JolCraftAttributeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffects.class)
public abstract class MobEffectsMixin {

    @Inject(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/effect/MobEffects;FIRE_RESISTANCE:Lnet/minecraft/core/Holder;",
                    shift = At.Shift.AFTER,
                    opcode = Opcodes.PUTSTATIC)
    )
    private static void jolcraft$addFireResistanceAttribute(CallbackInfo ci) {
        MobEffects.FIRE_RESISTANCE.value().addAttributeModifier(
                JolCraftAttributes.FIRE_RESISTANCE,
                JolCraft.location(JolCraftStrings.underscored(
                        JolCraftAttributeIds.FIRE_RESISTANCE,
                        JolCraftDictionary.EFFECT
                )),
                0.25D,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}