package net.sievert.jolcraft.world.entity.effect.custom.beneficial.combat.defence;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MoonShieldEffect extends MobEffect {

    public MoonShieldEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void fillEffectCures(@NotNull Set<EffectCure> cures, @NotNull MobEffectInstance effectInstance) {

    }
}
