package net.sievert.jolcraft.world.entity.effect.custom.beneficial.combat.defence.resistance;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public abstract class AbstractResistanceEffect extends MobEffect {

    protected AbstractResistanceEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

}
