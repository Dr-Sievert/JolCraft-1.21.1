package net.sievert.jolcraft.event.game.world.entity.effect.harmful;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.AbstractCrowdControlEffect;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;

public final class JolCraftCrowdControlEventsHelper {

    private JolCraftCrowdControlEventsHelper() {}

    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = event.getEffectInstance();

        if (!(effect.getEffect().value() instanceof AbstractCrowdControlEffect)) return;

        double tenacity = entity.getAttributeValue(JolCraftAttributes.CROWD_CONTROL_REDUCTION);
        if (tenacity < 1.0D) return;

        event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);

        JolCraftLogs.debug(
                JolCraftLogTags.ENTITY,
                "{} resisted {} application with {}% tenacity",
                entity.getName().getString(),
                effect.getEffect().unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(null),
                JolCraftLogs.pct1(tenacity)
        );
    }

    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.level().isClientSide()) return;

        boolean rooted = entity.hasEffect(JolCraftEffects.ROOTED);
        boolean stunned = entity.hasEffect(JolCraftEffects.STUNNED);
        boolean suppressed = entity.hasEffect(JolCraftEffects.SUPPRESSED);

        if (!rooted && !suppressed && !stunned) return;

        if (stunned) {
            applyStunned(entity);
            return;
        }

        if (rooted) {
            applyRooted(entity);
        }

        if (suppressed) {
            applySuppressed(entity);
        }
    }

    private static void applyStunned(LivingEntity entity) {
        disableTargeting(entity);
        disableActions(entity);
        disableNavigation(entity);
        disableHorizontalMovement(entity);
    }

    private static void applyRooted(LivingEntity entity) {
        disableNavigation(entity);
        disableHorizontalMovement(entity);
    }

    private static void applySuppressed(LivingEntity entity) {
        disableActions(entity);
    }

    private static void disableTargeting(LivingEntity entity) {
        if (entity instanceof Mob mob) {
            mob.setAggressive(false);
            mob.setTarget(null);
        }
    }

    private static void disableNavigation(LivingEntity entity) {
        if (entity instanceof Mob mob) {
            mob.getNavigation().stop();
            mob.getMoveControl().strafe(0.0F, 0.0F);
        }
    }

    private static void disableHorizontalMovement(LivingEntity entity) {
        var motion = entity.getDeltaMovement();
        entity.setDeltaMovement(0.0D, motion.y, 0.0D);
    }

    private static void disableActions(LivingEntity entity) {
        if (entity.isUsingItem()
                && !JolCraftEquipmentHelper.isRangedWeapon(entity.getMainHandItem())) {
            entity.stopUsingItem();
        }
    }
}
