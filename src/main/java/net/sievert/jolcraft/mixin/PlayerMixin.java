package net.sievert.jolcraft.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Redirect(
            method = "causeFoodExhaustion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"
            )
    )
    private void jolcraft$modifyFoodExhaustion(
            FoodData foodData,
            float exhaustion
    ) {
        Player player = (Player) (Object) this;

        double exhaustionModifier = player.getAttributeValue(JolCraftAttributes.EXHAUSTION);

        float modifiedExhaustion = exhaustion * (1.0F + (float) exhaustionModifier);

        foodData.addExhaustion(modifiedExhaustion);
    }
}