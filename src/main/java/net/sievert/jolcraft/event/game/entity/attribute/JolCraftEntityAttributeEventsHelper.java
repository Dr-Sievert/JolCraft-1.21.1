package net.sievert.jolcraft.event.game.entity.attribute;

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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.attribute.JolCraftAttributeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.util.JolCraftDimensionHelper;
import net.sievert.jolcraft.world.util.JolCraftTimeHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("SameParameterValue")
public final class JolCraftEntityAttributeEventsHelper {

    private static final ResourceLocation FROSTVEIN_ID = JolCraft.location(JolCraftAttributeIds.SLOW_RESISTANCE);

    private static final Map<UUID, Double> FROSTVEIN_CACHE = new HashMap<>();

    private static final String NBT_LUMINANCE_GLOWING =
            JolCraftStrings.underscored(
                    JolCraft.MOD_ID,
                    JolCraftAttributeIds.LUMINANCE,
                    JolCraftDictionary.EFFECT
            );

    private JolCraftEntityAttributeEventsHelper() {}

    public static void tickAttributes(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        tickFrostvein(entity);
        tickMoonShield(entity);
        tickLuminance(entity);
    }

    public static void clearTrackedAttributes(LivingEntity entity) {
        UUID uuid = entity.getUUID();

        FROSTVEIN_CACHE.remove(uuid);
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

        double resist = Mth.clamp(
                entity.getAttributeValue(JolCraftAttributes.SLOW_RESISTANCE),
                0.0D,
                1.0D
        );

        MobEffectInstance slow =
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN);

        if (slow == null) {
            FROSTVEIN_CACHE.remove(entity.getUUID());
            speed.removeModifier(FROSTVEIN_ID);
            return;
        }

        int amp = slow.getAmplifier();

        double slowAmount =
                -0.15D * (amp + 1);

        double vanillaMultiplier =
                1.0D + slowAmount;

        if (vanillaMultiplier <= 0.0D) {
            FROSTVEIN_CACHE.remove(entity.getUUID());
            speed.removeModifier(FROSTVEIN_ID);
            return;
        }

        double desiredSlowAmount =
                slowAmount * (1.0D - resist);

        double desiredMultiplier =
                1.0D + desiredSlowAmount;

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

        double originalSlow =
                0.15D * (amp + 1);

        double actualSlow =
                originalSlow * (1.0D - resist);

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
                "Frostvein slow resistance triggered: entity={}, originalSlow={}%, actualSlow={}%, oldSpeed={}, newSpeed={}",
                entity.getDisplayName().getString(),
                JolCraftLogs.pct1(originalSlow),
                JolCraftLogs.pct1(actualSlow),
                oldSpeed,
                newSpeed
        );
    }

    private static void tickLuminance(LivingEntity entity) {
        boolean hasLuminance = entity.getAttributeValue(JolCraftAttributes.LUMINANCE) > 0.0D;

        MobEffectInstance glowing = entity.getEffect(MobEffects.GLOWING);

        boolean owned = entity.getPersistentData().getBoolean(NBT_LUMINANCE_GLOWING);

        if (hasLuminance) {
            if (glowing != null) return;

            entity.addEffect(
                    new MobEffectInstance(
                            MobEffects.GLOWING,
                            MobEffectInstance.INFINITE_DURATION,
                            0,
                            false,
                            false,
                            false
                    )
            );

            entity.getPersistentData().putBoolean(
                    NBT_LUMINANCE_GLOWING,
                    true
            );

            return;
        }

        if (owned
                && glowing != null
                && glowing.isInfiniteDuration()
                && glowing.getAmplifier() == 0) {
            entity.removeEffect(MobEffects.GLOWING);
        }

        entity.getPersistentData().remove(NBT_LUMINANCE_GLOWING);
    }

    public static void applyIncomingDamageModifiers(LivingIncomingDamageEvent event) {
        applyProjectileDamage(event);
        applyArmorPenetration(event);
    }

    public static void applyFinalDamageReductions(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();

        float damage = event.getNewDamage();

        damage = applyMagicResistance(entity, event.getSource(), damage);
        damage = applyMoonShieldDamage(entity, damage);

        event.setNewDamage(Math.max(0.0F, damage));
    }

    private static float applyMagicResistance(
            LivingEntity entity,
            DamageSource source,
            float damage
    ) {
        if (!source.is(Tags.DamageTypes.IS_MAGIC)) return damage;

        double resist = Mth.clamp(
                entity.getAttributeValue(JolCraftAttributes.MAGIC_RESISTANCE),
                0.0D,
                1.0D
        );
        if (resist <= 0.0D) return damage;

        float reduced = (float) (damage * (1.0D - resist));
        if (reduced >= damage) return damage;

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Magic damage reduced: entity={}, input={}, resist={}%, final={}",
                entity.getDisplayName().getString(),
                damage,
                JolCraftLogs.pct1(resist),
                reduced
        );

        return reduced;
    }

    private static float applyMoonShieldDamage(
            LivingEntity entity,
            float damage
    ) {
        MobEffectInstance shield = entity.getEffect(JolCraftEffects.MOON_SHIELD);
        if (shield == null) return damage;

        int maxStacks = Mth.floor(entity.getAttributeValue(JolCraftAttributes.MOON_SHIELD));
        if (maxStacks <= 0) {
            entity.removeEffect(JolCraftEffects.MOON_SHIELD);
            return damage;
        }

        int stacks = Math.min(shield.getAmplifier() + 1, maxStacks);
        double reduction = Mth.clamp(stacks * 0.05D, 0.0D, 1.0D);

        float reduced = (float) (damage * (1.0D - reduction));
        if (reduced >= damage) return damage;

        int newAmplifier = stacks - 2;

        entity.removeEffect(JolCraftEffects.MOON_SHIELD);

        JolCraftSoundHelper.entity(
                entity,
                SoundEvents.GLASS_BREAK,
                0.3F,
                1.25F + entity.getRandom().nextFloat() * 0.5F
        );

        if (newAmplifier >= 0) {
            entity.addEffect(new MobEffectInstance(
                    JolCraftEffects.MOON_SHIELD,
                    MobEffectInstance.INFINITE_DURATION,
                    newAmplifier,
                    false,
                    false,
                    true
            ));
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
        int maxStacks = Mth.floor(
                entity.getAttributeValue(JolCraftAttributes.MOON_SHIELD)
        );

        MobEffectInstance current = entity.getEffect(JolCraftEffects.MOON_SHIELD);

        if (maxStacks <= 0) {
            if (current != null) {
                entity.removeEffect(JolCraftEffects.MOON_SHIELD);
            }
            return;
        }

        int maxAmplifier = maxStacks - 1;

        if (current != null && current.getAmplifier() > maxAmplifier) {
            entity.removeEffect(JolCraftEffects.MOON_SHIELD);

            entity.addEffect(new MobEffectInstance(
                    JolCraftEffects.MOON_SHIELD,
                    MobEffectInstance.INFINITE_DURATION,
                    maxAmplifier,
                    false,
                    false,
                    true
            ));

            return;
        }

        if (JolCraftTimeHelper.isDay(entity) && !JolCraftDimensionHelper.isEnd(entity)) {
            if (current != null) {
                entity.removeEffect(JolCraftEffects.MOON_SHIELD);
            }
            return;
        }

        if ((entity.tickCount % 200) != 0) return;

        if (current == null) {
            entity.addEffect(new MobEffectInstance(
                    JolCraftEffects.MOON_SHIELD,
                    MobEffectInstance.INFINITE_DURATION,
                    0,
                    false,
                    false,
                    true
            ));

            JolCraftSoundHelper.entity(
                    entity,
                    SoundEvents.BEACON_ACTIVATE,
                    0.15F,
                    1.3F + entity.getRandom().nextFloat() * 0.15F
            );
            return;
        }

        if (current.getAmplifier() >= maxAmplifier) return;

        entity.addEffect(new MobEffectInstance(
                JolCraftEffects.MOON_SHIELD,
                MobEffectInstance.INFINITE_DURATION,
                current.getAmplifier() + 1,
                false,
                false,
                true
        ));

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

        double penetration = Mth.clamp(
                attacker.getAttributeValue(JolCraftAttributes.ARMOR_PENETRATION),
                0.0D,
                1.0D
        );
        if (penetration <= 0.0D) return;

        event.addReductionModifier(
                DamageContainer.Reduction.ARMOR,
                (container, armorReduction) -> {
                    float originalDamage = container.getNewDamage();
                    float newReduction = armorReduction * (float) (1.0D - penetration);

                    float finalDamage = originalDamage - (newReduction - armorReduction);

                    double armor = target.getArmorValue();
                    double effectiveArmor = armor * (1.0D - penetration);

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

    private static void applyProjectileDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();

        if (!source.is(DamageTypeTags.IS_PROJECTILE)
                || !(source.getDirectEntity() instanceof AbstractArrow)
                || !(source.getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        double bonus = attacker.getAttributeValue(JolCraftAttributes.PROJECTILE_DAMAGE);
        if (bonus <= 0.0D) return;

        float originalDamage = event.getAmount();
        float modifiedDamage = originalDamage + (float) bonus;

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
}