package net.sievert.jolcraft.world.entity.effect.custom.harmful.curse;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import org.jetbrains.annotations.NotNull;

public class FamineCurseEffect extends AbstractCurseEffect {

    private static final float EXHAUSTION_PER_TICK = 0.1F;
    private static final float BASE_HUNGER_THRESHOLD = 10.0F;
    private static final int MAX_HEX_SCALING = 3;

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
            MobEffectInstance hex = player.getEffect(JolCraftEffects.HEX);
            int hexLevel = hex == null
                    ? 0
                    : Math.min(hex.getAmplifier() + 1, MAX_HEX_SCALING);

            player.causeFoodExhaustion(
                    Math.scalb(EXHAUSTION_PER_TICK, hexLevel)
            );
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}