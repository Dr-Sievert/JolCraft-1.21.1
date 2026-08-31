package net.sievert.jolcraft.event.game.world.entity.effect.util.harmful;

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
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.damage.JolCraftDamageTypes;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.curse.DeliriumCurseEffect;

import javax.annotation.Nullable;

public final class JolCraftCurseEventsHelper {

    private static final ResourceLocation HEX_VITALITY_MODIFIER_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftEffectIds.HEX,
                    JolCraftEffectIds.VITALITY_CURSE
            ));

    private static final double VITALITY_REDUCTION_PER_LEVEL = 0.2D;
    private static final double MAX_RELEVANT_CURSE_VULNERABILITY = 3.0D;
    private static final double CURSE_VULNERABILITY_SCALING_BASE = 2.0D;
    private static final int LETHAL_VITALITY_LEVEL = 5;

    private JolCraftCurseEventsHelper() {}

    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance cursedWound =
                entity.getEffect(JolCraftEffects.CURSED_WOUND);

        if (cursedWound == null) {
            return;
        }

        event.setCanceled(true);

        int amplifier = cursedWound.getAmplifier();

        if (amplifier <= 0
                || !(entity.level() instanceof ServerLevel level)) {
            return;
        }

        double curseVulnerability =
                getRelevantCurseVulnerability(entity);

        float backlashDamage = (float) (
                event.getAmount()
                        * 0.20D
                        * amplifier
                        * getCurseScaling(curseVulnerability)
        );

        if (backlashDamage <= 0.0F) {
            return;
        }

        entity.hurt(
                level.damageSources().source(
                        JolCraftDamageTypes.CURSED_WOUND
                ),
                backlashDamage
        );
    }

    public static void applyPreMitigationTargetModifiers(
            LivingIncomingDamageEvent event
    ) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance frailty =
                entity.getEffect(JolCraftEffects.FRAILTY_CURSE);

        if (frailty == null) {
            return;
        }

        double curseVulnerability =
                getRelevantCurseVulnerability(entity);

        float multiplier = (float) (
                1.0D
                        + 0.25D
                        * (frailty.getAmplifier() + 1)
                        * getCurseScaling(curseVulnerability)
        );

        event.setAmount(
                event.getAmount() * multiplier
        );
    }

    public static void onEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance addedEffect =
                event.getEffectInstance();

        if (!affectsVitalityScaling(addedEffect)) {
            return;
        }

        updateVitalityCurseModifier(
                event.getEntity(),
                addedEffect,
                null
        );
    }

    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        MobEffectInstance removedEffect = event.getEffectInstance();

        cleanupDelirium(event.getEntity(), removedEffect);

        if (removedEffect == null
                || !affectsVitalityScaling(removedEffect)) {
            return;
        }

        updateVitalityCurseModifier(
                event.getEntity(),
                null,
                removedEffect
        );
    }

    public static void onEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance expiredEffect = event.getEffectInstance();

        cleanupDelirium(event.getEntity(), expiredEffect);

        if (expiredEffect == null
                || !affectsVitalityScaling(expiredEffect)) {
            return;
        }

        updateVitalityCurseModifier(
                event.getEntity(),
                null,
                expiredEffect
        );
    }

    private static void cleanupDelirium(
            LivingEntity entity,
            @Nullable MobEffectInstance effect
    ) {
        if (entity instanceof ServerPlayer player
                && effect != null
                && effect.is(JolCraftEffects.DELIRIUM_CURSE)) {
            DeliriumCurseEffect.cleanupRuntime(player);
        }
    }

    private static void updateVitalityCurseModifier(
            LivingEntity entity,
            @Nullable MobEffectInstance addedEffect,
            @Nullable MobEffectInstance removedEffect
    ) {
        AttributeInstance maxHealth =
                entity.getAttribute(Attributes.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        AttributeModifier existingModifier =
                maxHealth.getModifier(
                        HEX_VITALITY_MODIFIER_ID
                );

        if (existingModifier != null) {
            maxHealth.removeModifier(existingModifier);
        }

        MobEffectInstance vitality = resolveEffect(
                entity,
                JolCraftEffects.VITALITY_CURSE,
                addedEffect,
                removedEffect
        );

        if (vitality == null) {
            return;
        }

        double curseVulnerability =
                resolveCurseVulnerability(
                        entity,
                        addedEffect,
                        removedEffect
                );

        int vitalityLevels =
                vitality.getAmplifier() + 1;

        double effectiveLevels = Math.min(
                vitalityLevels
                        * getCurseScaling(
                        Math.min(
                                curseVulnerability,
                                MAX_RELEVANT_CURSE_VULNERABILITY
                        )
                ),
                LETHAL_VITALITY_LEVEL
        );

        if (effectiveLevels > vitalityLevels) {
            double baseMultiplier =
                    1.0D
                            - VITALITY_REDUCTION_PER_LEVEL
                            * vitalityLevels;

            double effectiveMultiplier = Math.max(
                    0.0D,
                    1.0D
                            - VITALITY_REDUCTION_PER_LEVEL
                            * effectiveLevels
            );

            double modifierAmount =
                    effectiveMultiplier
                            / baseMultiplier
                            - 1.0D;

            maxHealth.addTransientModifier(
                    new AttributeModifier(
                            HEX_VITALITY_MODIFIER_ID,
                            modifierAmount,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            );
        }

        if (effectiveLevels >= LETHAL_VITALITY_LEVEL) {
            killFromVitalityCurse(entity);
        }
    }

    private static double getRelevantCurseVulnerability(
            LivingEntity entity
    ) {
        return Math.min(
                Math.max(
                        0.0D,
                        entity.getAttributeValue(
                                JolCraftAttributes.CURSE_VULNERABILITY
                        )
                ),
                MAX_RELEVANT_CURSE_VULNERABILITY
        );
    }

    private static double getCurseScaling(
            double curseVulnerability
    ) {
        return Math.pow(
                CURSE_VULNERABILITY_SCALING_BASE,
                Math.max(0.0D, curseVulnerability)
        );
    }

    private static double resolveCurseVulnerability(
            LivingEntity entity,
            @Nullable MobEffectInstance addedEffect,
            @Nullable MobEffectInstance removedEffect
    ) {
        double[] value = {
                entity.getAttributeValue(
                        JolCraftAttributes.CURSE_VULNERABILITY
                )
        };

        AttributeInstance attribute =
                entity.getAttribute(
                        JolCraftAttributes.CURSE_VULNERABILITY
                );

        if (attribute == null) {
            return 0.0D;
        }

        if (removedEffect != null) {
            removedEffect.getEffect().value().createModifiers(
                    removedEffect.getAmplifier(),
                    (modifierAttribute, modifier) -> {
                        if (!modifierAttribute.is(
                                JolCraftAttributes.CURSE_VULNERABILITY.getKey()
                        )) {
                            return;
                        }

                        AttributeModifier current =
                                attribute.getModifier(modifier.id());

                        if (current != null) {
                            value[0] -= current.amount();
                        }
                    }
            );
        }

        if (addedEffect != null) {
            addedEffect.getEffect().value().createModifiers(
                    addedEffect.getAmplifier(),
                    (modifierAttribute, modifier) -> {
                        if (!modifierAttribute.is(
                                JolCraftAttributes.CURSE_VULNERABILITY.getKey()
                        )) {
                            return;
                        }

                        AttributeModifier current =
                                attribute.getModifier(modifier.id());

                        if (current == null) {
                            value[0] += modifier.amount();
                            return;
                        }

                        if (modifier.amount() > current.amount()) {
                            value[0] +=
                                    modifier.amount()
                                            - current.amount();
                        }
                    }
            );
        }

        return Math.max(
                0.0D,
                value[0]
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static MobEffectInstance resolveEffect(
            LivingEntity entity,
            Holder<MobEffect> effect,
            @Nullable MobEffectInstance addedEffect,
            @Nullable MobEffectInstance removedEffect
    ) {
        if (removedEffect != null
                && removedEffect.is(effect)) {
            return null;
        }

        MobEffectInstance currentEffect =
                entity.getEffect(effect);

        if (addedEffect == null
                || !addedEffect.is(effect)) {
            return currentEffect;
        }

        if (currentEffect == null
                || addedEffect.getAmplifier()
                > currentEffect.getAmplifier()) {
            return addedEffect;
        }

        return currentEffect;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean affectsVitalityScaling(
            MobEffectInstance effect
    ) {
        return effect.is(JolCraftEffects.VITALITY_CURSE)
                || modifiesCurseVulnerability(effect);
    }

    private static boolean modifiesCurseVulnerability(
            MobEffectInstance effect
    ) {
        boolean[] modifies = {false};

        effect.getEffect().value().createModifiers(
                effect.getAmplifier(),
                (attribute, modifier) -> {
                    if (attribute.is(
                            JolCraftAttributes.CURSE_VULNERABILITY.getKey()
                    )) {
                        modifies[0] = true;
                    }
                }
        );

        return modifies[0];
    }

    private static void killFromVitalityCurse(
            LivingEntity entity
    ) {
        if (entity instanceof ServerPlayer player
                && player.isCreative()) {
            return;
        }

        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }

        entity.hurt(
                level.damageSources().source(
                        JolCraftDamageTypes.VITALITY_CURSE
                ),
                Float.MAX_VALUE
        );
    }
}