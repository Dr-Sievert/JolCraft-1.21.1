package net.sievert.jolcraft.mixin;

import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.JolCraftTags;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin {

    @Inject(
            method = "fireImmune",
            at = @At("RETURN"),
            cancellable = true
    )
    private void jolcraft$includeFireImmuneTag(
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValue()) {
            return;
        }

        EntityType<?> type = (EntityType<?>) (Object) this;

        if (type.is(JolCraftTags.EntityTypes.FIRE_IMMUNE)) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
            method = "isBlockDangerous",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/EntityType;fireImmune:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean jolcraft$includeFireImmuneTagInBlockDanger(
            EntityType<?> type
    ) {
        return type.fireImmune();
    }
}