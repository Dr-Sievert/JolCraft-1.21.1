package net.sievert.jolcraft.world.entity.effect.custom.beneficial;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

public final class DwarvenRageEffect extends MobEffect {

    public static final ResourceLocation DWARVEN_RAGE_EFFECT_MODIFIER_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.DWARVEN_RAGE, JolCraftDictionary.EFFECT));

    private static final double ATTACK_SPEED_BONUS_PER_HEALTH_STEP = 0.025D;
    private static final int MAX_MISSING_HEALTH_STEPS = 8;
    private static final int UPDATE_INTERVAL_TICKS = 5;

    public DwarvenRageEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % UPDATE_INTERVAL_TICKS == 0;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        updateAttackSpeed(entity, amplifier);
        return true;
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity entity, int amplifier) {
        updateAttackSpeed(entity, amplifier);
    }

    private static void updateAttackSpeed(LivingEntity entity, int amplifier) {
        AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed == null) {
            return;
        }

        double missingHealthFraction = 1.0D - entity.getHealth() / entity.getMaxHealth();

        int missingHealthSteps = Mth.clamp(
                Mth.floor(missingHealthFraction * 10.0D + 1.0E-6D),
                0,
                MAX_MISSING_HEALTH_STEPS
        );

        double bonus = missingHealthSteps * ATTACK_SPEED_BONUS_PER_HEALTH_STEP * (amplifier + 1);

        AttributeModifier currentModifier = attackSpeed.getModifier(DWARVEN_RAGE_EFFECT_MODIFIER_ID);

        if (currentModifier != null && Double.compare(currentModifier.amount(), bonus) == 0) {
            return;
        }

        attackSpeed.removeModifier(DWARVEN_RAGE_EFFECT_MODIFIER_ID);
        attackSpeed.addTransientModifier(new AttributeModifier(
                DWARVEN_RAGE_EFFECT_MODIFIER_ID,
                bonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));
    }
}