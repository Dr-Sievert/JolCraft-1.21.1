package net.sievert.jolcraft.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Unique
    private static final float ENDURANCE_CHANCE_PER_LEVEL = 0.25F;

    @Shadow
    private int foodLevel;

    @Shadow
    private float saturationLevel;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/food/FoodData;saturationLevel:F",
                    opcode = org.objectweb.asm.Opcodes.PUTFIELD
            )
    )
    private void jolcraft$applyEnduranceToSaturation(
            FoodData foodData,
            float newSaturationLevel,
            Player player
    ) {
        if (jolcraft$preventFoodDrain(player, "saturation", this.saturationLevel)) {
            return;
        }

        this.saturationLevel = newSaturationLevel;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/food/FoodData;foodLevel:I",
                    opcode = org.objectweb.asm.Opcodes.PUTFIELD
            )
    )
    private void jolcraft$applyEnduranceToHunger(
            FoodData foodData,
            int newFoodLevel,
            Player player
    ) {
        if (jolcraft$preventFoodDrain(player, "hunger", this.foodLevel)) {
            return;
        }

        this.foodLevel = newFoodLevel;
    }

    @Unique
    private boolean jolcraft$preventFoodDrain(
            Player player,
            String type,
            float currentValue
    ) {
        MobEffectInstance endurance = player.getEffect(JolCraftEffects.ENDURANCE);
        if (endurance == null) {
            return false;
        }

        int level = endurance.getAmplifier() + 1;
        float preventionChance = Math.min(
                ENDURANCE_CHANCE_PER_LEVEL * level,
                1.0F
        );

        if (player.getRandom().nextFloat() >= preventionChance) {
            return false;
        }

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Endurance prevented {} loss for player {}. Prevention chance={}%, current value={}.",
                type,
                player.getName().getString(),
                level,
                JolCraftLogs.pct1(preventionChance),
                currentValue
        );

        return true;
    }
}