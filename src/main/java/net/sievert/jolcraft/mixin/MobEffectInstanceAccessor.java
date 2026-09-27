package net.sievert.jolcraft.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {

    @Accessor("duration")
    void jolcraft$setDuration(int duration);

    @Accessor("amplifier")
    void jolcraft$setAmplifier(int amplifier);

    @Accessor("hiddenEffect")
    @Nullable MobEffectInstance jolcraft$getHiddenEffect();

    @Accessor("hiddenEffect")
    void jolcraft$setHiddenEffect(@Nullable MobEffectInstance hiddenEffect);
}
