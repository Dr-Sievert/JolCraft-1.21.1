package net.sievert.jolcraft.world.effect.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

public class RootedEffect extends MobEffect {

    public RootedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectAdded(@NotNull LivingEntity livingEntity, int amplifier) {
        JolCraftSoundHelper.entity(livingEntity, SoundEvents.COBWEB_HIT, 1.20F, 0.80F);
    }
}
