package net.sievert.jolcraft.event.game.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.damage.JolCraftDamageTypes;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftCurseEvents {

    private static final ResourceLocation HEX_VITALITY_MODIFIER_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftEffectIds.HEX,
                    JolCraftEffectIds.VITALITY_CURSE
            ));

    private static final double VITALITY_REDUCTION_PER_LEVEL = 0.2D;
    private static final int LETHAL_VITALITY_LEVEL = 5;
    private static final int MAX_RELEVANT_HEX_LEVEL = 3;

    private JolCraftCurseEvents() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCursedWoundHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.hasEffect(JolCraftEffects.CURSED_WOUND)) return;

        event.setCanceled(true);

        MobEffectInstance hex = entity.getEffect(JolCraftEffects.HEX);
        if (hex == null || !(entity.level() instanceof ServerLevel level)) return;

        int effectiveHexLevel = Math.min(hex.getAmplifier(), 3) + 1;
        float backlashDamage = event.getAmount() * 0.25F * effectiveHexLevel;

        if (backlashDamage <= 0.0F) return;

        entity.hurt(
                level.damageSources().source(JolCraftDamageTypes.CURSED_WOUND),
                backlashDamage
        );
    }

    @SubscribeEvent
    public static void onFrailtyCurseDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance frailty = entity.getEffect(JolCraftEffects.FRAILTY_CURSE);
        if (frailty == null) return;

        MobEffectInstance hex = entity.getEffect(JolCraftEffects.HEX);
        int hexLevel = hex == null
                ? 0
                : Math.min(hex.getAmplifier() + 1, 3);

        float multiplier = 1.0F
                + 0.25F
                * (frailty.getAmplifier() + 1)
                * Math.scalb(1.0F, hexLevel);

        event.setAmount(event.getAmount() * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onCurseEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance addedEffect = event.getEffectInstance();

        if (!isVitalityOrHex(addedEffect)) return;

        updateVitalityHexModifier(
                event.getEntity(),
                addedEffect,
                null
        );
    }

    @SubscribeEvent
    public static void onCurseEffectRemoved(MobEffectEvent.Remove event) {
        MobEffectInstance removedEffect = event.getEffectInstance();
        if (removedEffect == null || !isVitalityOrHex(removedEffect)) return;

        updateVitalityHexModifier(
                event.getEntity(),
                null,
                removedEffect
        );
    }

    @SubscribeEvent
    public static void onCurseEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance expiredEffect = event.getEffectInstance();
        if (expiredEffect == null || !isVitalityOrHex(expiredEffect)) return;

        updateVitalityHexModifier(
                event.getEntity(),
                null,
                expiredEffect
        );
    }

    private static void updateVitalityHexModifier(
            LivingEntity entity,
            MobEffectInstance addedEffect,
            MobEffectInstance removedEffect
    ) {
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;

        AttributeModifier existingModifier =
                maxHealth.getModifier(HEX_VITALITY_MODIFIER_ID);

        if (existingModifier != null) {
            maxHealth.removeModifier(existingModifier);
        }

        MobEffectInstance vitality = resolveEffect(
                entity,
                JolCraftEffects.VITALITY_CURSE,
                addedEffect,
                removedEffect
        );

        if (vitality == null) return;

        MobEffectInstance hex = resolveEffect(
                entity,
                JolCraftEffects.HEX,
                addedEffect,
                removedEffect
        );

        int vitalityLevels = vitality.getAmplifier() + 1;
        int hexLevels = hex == null ? 0 : hex.getAmplifier() + 1;

        int effectiveLevels = Math.min(
                vitalityLevels << Math.min(hexLevels, MAX_RELEVANT_HEX_LEVEL),
                LETHAL_VITALITY_LEVEL
        );

        if (effectiveLevels > vitalityLevels) {
            double baseMultiplier =
                    1.0D - VITALITY_REDUCTION_PER_LEVEL * vitalityLevels;

            double effectiveMultiplier = Math.max(
                    0.0D,
                    1.0D - VITALITY_REDUCTION_PER_LEVEL * effectiveLevels
            );

            double hexModifierAmount =
                    effectiveMultiplier / baseMultiplier - 1.0D;

            maxHealth.addOrReplacePermanentModifier(new AttributeModifier(
                    HEX_VITALITY_MODIFIER_ID,
                    hexModifierAmount,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }

        if (effectiveLevels == LETHAL_VITALITY_LEVEL) {
            killFromVitalityCurse(entity);
        }
    }

    private static MobEffectInstance resolveEffect(
            LivingEntity entity,
            Holder<MobEffect> effect,
            MobEffectInstance addedEffect,
            MobEffectInstance removedEffect
    ) {
        if (removedEffect != null && removedEffect.is(effect)) {
            return null;
        }

        MobEffectInstance currentEffect = entity.getEffect(effect);

        if (addedEffect == null || !addedEffect.is(effect)) {
            return currentEffect;
        }

        if (currentEffect == null
                || addedEffect.getAmplifier() > currentEffect.getAmplifier()) {
            return addedEffect;
        }

        return currentEffect;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isVitalityOrHex(MobEffectInstance effect) {
        return effect.is(JolCraftEffects.VITALITY_CURSE)
                || effect.is(JolCraftEffects.HEX);
    }

    private static void killFromVitalityCurse(LivingEntity entity) {
        if (entity instanceof ServerPlayer player && player.isCreative()) return;
        if (!(entity.level() instanceof ServerLevel level)) return;

        entity.hurt(
                level.damageSources().source(JolCraftDamageTypes.VITALITY_CURSE),
                Float.MAX_VALUE
        );
    }
}