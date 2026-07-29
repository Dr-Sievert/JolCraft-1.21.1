package net.sievert.jolcraft.world.entity.effect.custom.curse;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class AbstractCurseEffect extends MobEffect {

    protected AbstractCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectAdded(@NotNull LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);

        if (entity instanceof Player player) {
            PlaySound.curse(player);
        }
    }

    @Override
    public void fillEffectCures(@NotNull Set<EffectCure> cures, @NotNull MobEffectInstance effectInstance) {

    }
}