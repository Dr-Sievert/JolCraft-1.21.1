package net.sievert.jolcraft.world.entity.effect.custom.harmful.curse;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import org.jetbrains.annotations.NotNull;

public class FamineCurseEffect extends AbstractCurseEffect {

    private static final float EXHAUSTION_PER_TICK = 0.1F;
    private static final float BASE_HUNGER_THRESHOLD = 10.0F;
    private static final double MAX_CURSE_VULNERABILITY_SCALING = 3.0D;

    public FamineCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        float hungerThreshold =
                BASE_HUNGER_THRESHOLD / (amplifier + 1.0F);

        int foodLevel =
                player.getFoodData().getFoodLevel();

        if (foodLevel > hungerThreshold) {
            double curseVulnerability = Mth.clamp(
                    player.getAttributeValue(
                            JolCraftAttributes.CURSE_VULNERABILITY
                    ),
                    0.0D,
                    MAX_CURSE_VULNERABILITY_SCALING
            );

            player.causeFoodExhaustion(
                    (float) (
                            EXHAUSTION_PER_TICK
                                    * Math.pow(
                                    2.0D,
                                    curseVulnerability
                            )
                    )
            );
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(
            int duration,
            int amplifier
    ) {
        return true;
    }
}