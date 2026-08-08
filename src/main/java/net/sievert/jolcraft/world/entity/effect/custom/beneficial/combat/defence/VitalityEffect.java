package net.sievert.jolcraft.world.entity.effect.custom.beneficial.combat.defence;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class VitalityEffect extends MobEffect {

    public static final float ABSORPTION_PER_LEVEL = 2.0F;

    public VitalityEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity entity, int amplifier) {
        super.onEffectStarted(entity, amplifier);

        entity.setAbsorptionAmount(
                Math.max(
                        entity.getAbsorptionAmount(),
                        ABSORPTION_PER_LEVEL * (amplifier + 1)
                )
        );
    }
}