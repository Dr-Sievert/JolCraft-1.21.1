package net.sievert.jolcraft.mixin;

import net.minecraft.world.item.alchemy.Potions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Potions.class)
public abstract class PotionsMixin {

    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(intValue = 9600),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/item/alchemy/Potions;FIRE_RESISTANCE:Lnet/minecraft/core/Holder;",
                            opcode = Opcodes.PUTSTATIC,
                            shift = At.Shift.AFTER
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/item/alchemy/Potions;LONG_FIRE_RESISTANCE:Lnet/minecraft/core/Holder;",
                            opcode = Opcodes.PUTSTATIC
                    )
            ),
            require = 1,
            allow = 1
    )
    private static int jolcraft$reduceLongFireResistanceDuration(int duration) {
        return 7200;
    }
}
