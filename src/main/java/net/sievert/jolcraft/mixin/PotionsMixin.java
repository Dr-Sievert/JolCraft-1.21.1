package net.sievert.jolcraft.mixin;

import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Potions.class)
public abstract class PotionsMixin {

    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(intValue = 9600),
            require = 1
    )
    private static int jolcraft$reduceLongFireResistanceDuration(int duration) {
        return 7200;
    }
}