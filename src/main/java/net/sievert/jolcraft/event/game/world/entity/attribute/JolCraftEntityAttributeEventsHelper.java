package net.sievert.jolcraft.event.game.world.entity.attribute;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.attribute.JolCraftAttributeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.attachment.custom.overheal.OverhealAttachmentHelper;
import net.sievert.jolcraft.world.entity.damage.JolCraftDamageTypes;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.effect.JolCraftOwnedEffectHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.util.JolCraftDimensionHelper;
import net.sievert.jolcraft.world.util.JolCraftTimeHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("SameParameterValue")
public final class JolCraftEntityAttributeEventsHelper {

    private static final ResourceLocation FROSTVEIN_ID =
            JolCraft.location(JolCraftAttributeIds.SLOW_RESISTANCE);

    private static final Map<UUID, Double> FROSTVEIN_CACHE = new HashMap<>();

    private static final int MAX_SUNFIRE_DURATION = 60 * 20;

    private static final String NBT_LUMINANCE_EFFECTS =
            JolCraftStrings.underscored(
                    JolCraft.MOD_ID,
                    JolCraftAttributeIds.LUMINANCE,
                    JolCraftDictionary.EFFECT
            );

    private static final String LUMINANCE_GLOWING_ID =
            MobEffects.GLOWING.unwrapKey()
                    .orElseThrow()
                    .location()
                    .toString();

    private JolCraftEntityAttributeEventsHelper() {}

    public static void tickAttributes(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        tickFrostvein(entity);
        tickMoonShield(entity);
        tickLuminance(entity);
        tickSunfire(entity);
    }

    public static void clearTrackedAttributes(LivingEntity entity) {
        FROSTVEIN_CACHE.remove(entity.getUUID());
    }

    private static boolean shouldIgnoreAttribute(
            LivingEntity entity,
            double value,
            Map<UUID, Double> cache,
            ResourceLocation modifierId,
            AttributeInstance instance
    ) {
        UUID uuid = entity.getUUID();

        if (value <= 0.0D) {
            if (cache.remove(uuid) != null) {
                instance.removeModifier(modifierId);
            }
            return true;
        }

        Double oldValue = cache.get(uuid);
        if (oldValue != null && Double.compare(oldValue, value) == 0) {
            return true;
        }

        cache.put(uuid, value);
        return false;
    }

    private static void tickFrostvein(LivingEntity entity) {
        AttributeInstance speed =
                entity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speed == null) return;

        double resistance = Mth.clamp(
                entity.getAttributeValue(JolCraftAttributes.SLOW_RESISTANCE),
                0.0D,
                1.0D
        );

        double vulnerability = Math.max(
                0.0D,
                entity.getAttributeValue(JolCraftAttributes.SLOW_VULNERABILITY)
        );

        MobEffectInstance slow =
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN);

        if (slow == null) {
            FROSTVEIN_CACHE.remove(entity.getUUID());
            speed.removeModifier(FROSTVEIN_ID);
            return;
        }

        int amplifier = slow.getAmplifier();

        double originalSlow =
                0.15D * (amplifier + 1);

        double vanillaMultiplier =
                1.0D - originalSlow;

        if (vanillaMultiplier <= 0.0D) {
            FROSTVEIN_CACHE.remove(entity.getUUID());
            speed.removeModifier(FROSTVEIN_ID);
            return;
        }

        double actualSlow =
                originalSlow * Math.max(
                        0.0D,
                        1.0D - resistance + vulnerability
                );

        double desiredMultiplier =
                Math.max(0.0D, 1.0D - actualSlow);

        double extra =
                desiredMultiplier / vanillaMultiplier - 1.0D;

        if (shouldIgnoreAttribute(
                entity,
                extra,
                FROSTVEIN_CACHE,
                FROSTVEIN_ID,
                speed
        )) {
            return;
        }

        double oldSpeed =
                speed.getValue();

        speed.removeModifier(FROSTVEIN_ID);

        speed.addTransientModifier(
                new AttributeModifier(
                        FROSTVEIN_ID,
                        extra,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );

        double newSpeed =
                speed.getValue();

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Slowness modifier triggered: entity={}, resistance={}%, vulnerability={}%, originalSlow={}%, actualSlow={}%, oldSpeed={}, newSpeed={}",
                entity.getDisplayName().getString(),
                JolCraftLogs.pct1(resistance),
                JolCraftLogs.pct1(vulnerability),
                JolCraftLogs.pct1(originalSlow),
                JolCraftLogs.pct1(actualSlow),
                oldSpeed,
                newSpeed
        );
    }

    private static void tickLuminance(LivingEntity entity) {
        JolCraftOwnedEffectHelper.syncInfinite(
                entity,
                MobEffects.GLOWING,
                0,
                NBT_LUMINANCE_EFFECTS,
                LUMINANCE_GLOWING_ID,
                entity.getAttributeValue(JolCraftAttributes.LUMINANCE) > 0.0D,
                false,
                false,
                false
        );
    }

    private static void tickSunfire(LivingEntity entity) {
        if (!entity.hasEffect(JolCraftEffects.SUNFIRE)
                || !isSunfireEnvironment(entity)
                || entity.isOnFire()) {
            return;
        }

        entity.igniteForSeconds(2.0F);
    }

    public static void applyAttackBuild(LivingIncomingDamageEvent event) {
        applyProjectileDamage(event);
    }

    public static void applyDefenseShaping(LivingIncomingDamageEvent event) {
        applyArmorPenetration(event);
    }

    public static void applyFinalDefenses(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        float damage = event.getNewDamage();

        damage = applyMoonShieldDamage(entity, damage);

        float overheal = OverhealAttachmentHelper.getAmount(entity);
        if (overheal > 0.0F && damage > 0.0F) {
            float absorbed = Math.min(overheal, damage);

            OverhealAttachmentHelper.setAmount(
                    entity,
                    overheal - absorbed
            );

            damage -= absorbed;
        }

        event.setNewDamage(Math.max(0.0F, damage));
    }

    public static void applyPostHitMarkers(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();

        if (!isValidSunfireTrigger(event)) return;

        LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
        double sunFireDamage =
                Objects.requireNonNull(attacker).getAttributeValue(JolCraftAttributes.SUN_FIRE_DAMAGE);

        if (sunFireDamage <= 0.0D) return;

        applySunfire(
                entity,
                Mth.floor(sunFireDamage) * 20
        );
    }

    public static void applySecondaryDamage(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();

        if (!isValidSunfireTrigger(event)
                || !entity.isOnFire()
                || !isSunfireEnvironment(entity)) {
            return;
        }

        LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
        double sunFireDamage =
                Objects.requireNonNull(attacker).getAttributeValue(JolCraftAttributes.SUN_FIRE_DAMAGE);

        if (sunFireDamage <= 0.0D) return;

        entity.hurt(
                entity.damageSources().source(
                        JolCraftDamageTypes.SUNFIRE,
                        attacker
                ),
                (float) sunFireDamage
        );

        JolCraftLogs.debug(
                JolCraftLogTags.ENTITY,
                "Entity {} dealt {} bonus fire damage using sunfire attribute to {} at {} in {}",
                attacker.getName().getString(),
                sunFireDamage,
                entity.getName().getString(),
                JolCraftLogs.roundedPos(entity),
                entity.level().dimension().location()
        );
    }

    public static void applyPostHitSideEffects(LivingDamageEvent.Post event) {
        // Reserved for non-damage consequences that should happen after the hit resolves.
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isValidSunfireTrigger(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();

        return !entity.level().isClientSide()
                && !entity.isDeadOrDying()
                && event.getNewDamage() > 0.0F
                && !event.getSource().is(JolCraftDamageTypes.SUNFIRE)
                && event.getSource().getEntity() instanceof LivingEntity;
    }

    private static void applySunfire(
            LivingEntity entity,
            int addedDuration
    ) {
        MobEffectInstance current =
                entity.getEffect(JolCraftEffects.SUNFIRE);

        int duration = Math.min(
                MAX_SUNFIRE_DURATION,
                addedDuration + (current != null ? current.getDuration() : 0)
        );

        entity.forceAddEffect(
                new MobEffectInstance(
                        JolCraftEffects.SUNFIRE,
                        duration,
                        0,
                        false,
                        true,
                        true
                ),
                null
        );
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isSunfireEnvironment(LivingEntity entity) {
        return JolCraftDimensionHelper.isNether(entity)
                || (JolCraftTimeHelper.isDay(entity)
                && entity.level().canSeeSky(entity.blockPosition()));
    }

    private static float applyMoonShieldDamage(
            LivingEntity entity,
            float damage
    ) {
        MobEffectInstance shield =
                entity.getEffect(JolCraftEffects.MOON_SHIELD);

        if (shield == null) return damage;

        int maxStacks =
                Mth.floor(
                        entity.getAttributeValue(JolCraftAttributes.MOON_SHIELD)
                );

        if (maxStacks <= 0) {
            entity.removeEffect(JolCraftEffects.MOON_SHIELD);
            return damage;
        }

        int stacks =
                Math.min(
                        shield.getAmplifier() + 1,
                        maxStacks
                );

        double reduction =
                Mth.clamp(stacks * 0.05D, 0.0D, 1.0D);

        float reduced =
                (float) (damage * (1.0D - reduction));

        if (reduced >= damage) return damage;

        int newAmplifier =
                stacks - 2;

        entity.removeEffect(JolCraftEffects.MOON_SHIELD);

        JolCraftSoundHelper.entity(
                entity,
                SoundEvents.GLASS_BREAK,
                0.3F,
                1.25F + entity.getRandom().nextFloat() * 0.5F
        );

        if (newAmplifier >= 0) {
            entity.addEffect(
                    new MobEffectInstance(
                            JolCraftEffects.MOON_SHIELD,
                            MobEffectInstance.INFINITE_DURATION,
                            newAmplifier,
                            false,
                            false,
                            true
                    )
            );
        }

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Moon Shield: entity={}, stacks={}, reduction={}%, inputDmg={}, finalDmg={}",
                entity.getDisplayName().getString(),
                stacks,
                JolCraftLogs.pct1(reduction),
                damage,
                reduced
        );

        return reduced;
    }

    private static void tickMoonShield(LivingEntity entity) {
        int maxStacks =
                Mth.floor(
                        entity.getAttributeValue(JolCraftAttributes.MOON_SHIELD)
                );

        MobEffectInstance current =
                entity.getEffect(JolCraftEffects.MOON_SHIELD);

        if (maxStacks <= 0) {
            if (current != null) {
                entity.removeEffect(JolCraftEffects.MOON_SHIELD);
            }
            return;
        }

        int maxAmplifier =
                maxStacks - 1;

        if (current != null
                && current.getAmplifier() > maxAmplifier) {
            entity.removeEffect(JolCraftEffects.MOON_SHIELD);

            entity.addEffect(
                    new MobEffectInstance(
                            JolCraftEffects.MOON_SHIELD,
                            MobEffectInstance.INFINITE_DURATION,
                            maxAmplifier,
                            false,
                            false,
                            true
                    )
            );

            return;
        }

        if (JolCraftTimeHelper.isDay(entity)
                && !JolCraftDimensionHelper.isEnd(entity)) {
            if (current != null) {
                entity.removeEffect(JolCraftEffects.MOON_SHIELD);
            }
            return;
        }

        if ((entity.tickCount % 200) != 0) return;

        if (current == null) {
            entity.addEffect(
                    new MobEffectInstance(
                            JolCraftEffects.MOON_SHIELD,
                            MobEffectInstance.INFINITE_DURATION,
                            0,
                            false,
                            false,
                            true
                    )
            );

            JolCraftSoundHelper.entity(
                    entity,
                    SoundEvents.BEACON_ACTIVATE,
                    0.15F,
                    1.3F + entity.getRandom().nextFloat() * 0.15F
            );

            return;
        }

        if (current.getAmplifier() >= maxAmplifier) return;

        entity.addEffect(
                new MobEffectInstance(
                        JolCraftEffects.MOON_SHIELD,
                        MobEffectInstance.INFINITE_DURATION,
                        current.getAmplifier() + 1,
                        false,
                        false,
                        true
                )
        );

        JolCraftSoundHelper.entity(
                entity,
                SoundEvents.BEACON_ACTIVATE,
                0.3F,
                1.3F + entity.getRandom().nextFloat() * 0.15F
        );
    }

    public static void applyArmorPenetration(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();

        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        LivingEntity target = event.getEntity();

        if (target.getArmorValue() <= 0.0D) return;

        double penetration =
                Mth.clamp(
                        attacker.getAttributeValue(
                                JolCraftAttributes.ARMOR_PENETRATION
                        ),
                        0.0D,
                        1.0D
                );

        if (penetration <= 0.0D) return;

        event.addReductionModifier(
                DamageContainer.Reduction.ARMOR,
                (container, armorReduction) -> {
                    float originalDamage =
                            container.getNewDamage();

                    float newReduction =
                            armorReduction
                                    * (float) (1.0D - penetration);

                    float finalDamage =
                            originalDamage
                                    - (newReduction - armorReduction);

                    double armor =
                            target.getArmorValue();

                    double effectiveArmor =
                            armor * (1.0D - penetration);

                    JolCraftLogs.debug(
                            JolCraftLogTags.PLAYER,
                            "Armor penetration: attacker={}, target={}, pen={}%, armor={}, effectiveArmor={}, originalDmg={}, finalDmg={}",
                            attacker.getDisplayName().getString(),
                            target.getDisplayName().getString(),
                            JolCraftLogs.pct1(penetration),
                            armor,
                            effectiveArmor,
                            originalDamage,
                            finalDamage
                    );

                    return newReduction;
                }
        );
    }

    private static void applyProjectileDamage(
            LivingIncomingDamageEvent event
    ) {
        DamageSource source =
                event.getSource();

        if (!source.is(DamageTypeTags.IS_PROJECTILE)
                || !(source.getDirectEntity() instanceof AbstractArrow)
                || !(source.getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        double bonus =
                attacker.getAttributeValue(
                        JolCraftAttributes.PROJECTILE_DAMAGE
                );

        if (bonus <= 0.0D) return;

        float originalDamage =
                event.getAmount();

        float modifiedDamage =
                originalDamage + (float) bonus;

        event.setAmount(modifiedDamage);

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Projectile damage increased: attacker={}, target={}, bonus={}, originalDmg={}, finalDmg={}",
                attacker.getDisplayName().getString(),
                event.getEntity().getDisplayName().getString(),
                bonus,
                originalDamage,
                modifiedDamage
        );
    }

    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) return;

        float maxOverheal = OverhealAttachmentHelper.getMaxAmount(entity);
        if (maxOverheal <= 0.0F) return;

        float missingHealth = Math.max(
                0.0F,
                entity.getMaxHealth() - entity.getHealth()
        );

        float excessHealing = event.getAmount() - missingHealth;
        if (excessHealing <= 0.0F) return;

        OverhealAttachmentHelper.addAmount(entity, excessHealing);
    }
}