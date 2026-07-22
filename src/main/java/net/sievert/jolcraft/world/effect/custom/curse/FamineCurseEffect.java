package net.sievert.jolcraft.world.effect.custom.curse;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class FamineCurseEffect extends AbstractCurseEffect {

    private static final float EXHAUSTION_PER_TICK = 0.1F;
    private static final float BASE_HUNGER_THRESHOLD = 10.0F;

    public FamineCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        float hungerThreshold = BASE_HUNGER_THRESHOLD / (amplifier + 1.0F);
        int foodLevel = player.getFoodData().getFoodLevel();

        if (foodLevel > hungerThreshold) {
            player.causeFoodExhaustion(EXHAUSTION_PER_TICK);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}