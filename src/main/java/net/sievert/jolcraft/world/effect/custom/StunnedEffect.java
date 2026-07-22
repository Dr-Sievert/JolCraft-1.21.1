package net.sievert.jolcraft.world.effect.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

public class StunnedEffect extends MobEffect {

    public StunnedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectAdded(@NotNull LivingEntity livingEntity, int amplifier) {
        JolCraftSoundHelper.entity(livingEntity, SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 0.70F);
    }
}
