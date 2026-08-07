package net.sievert.jolcraft.event.game.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.HearthBlockEntity;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.player.attachment.custom.effect.AlchemistFocusAttachmentHelper;
import net.sievert.jolcraft.world.player.attachment.custom.hearth.HearthAttachmentHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import java.util.List;
import java.util.Map;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftEffectEvents {

    private static final float ALCHEMIST_FOCUS_DURATION_BONUS_PER_LEVEL = 0.25F;

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        MobEffectInstance addedEffect = event.getEffectInstance();

        if (addedEffect.is(JolCraftEffects.ALCHEMIST_FOCUS)) {
            applyFocusToExistingEffects(player, addedEffect, event);
            return;
        }

        if (!addedEffect.getEffect().value().isBeneficial()
                || addedEffect.isInfiniteDuration()) {
            return;
        }

        MobEffectInstance focus =
                player.getEffect(JolCraftEffects.ALCHEMIST_FOCUS);

        if (focus == null) {
            clearTrackingIfEffectWasReplaced(
                    player,
                    event.getOldEffectInstance(),
                    addedEffect
            );
            return;
        }

        int focusLevel = focus.getAmplifier() + 1;

        increaseDuration(addedEffect, focusLevel);
        AlchemistFocusAttachmentHelper.markBoostedEffect(
                player,
                addedEffect.getEffect()
        );

        if (event.getOldEffectInstance() != null) {
            player.forceAddEffect(
                    addedEffect,
                    event.getEffectSource()
            );
        }

        logAlchemistFocus(player, focusLevel, 1);
    }

    private static void applyFocusToExistingEffects(
            ServerPlayer player,
            MobEffectInstance addedFocus,
            MobEffectEvent.Added event
    ) {
        if (event.getOldEffectInstance() != null) {
            return;
        }

        int focusLevel = addedFocus.getAmplifier() + 1;
        int increasedEffects = 0;

        for (MobEffectInstance effect : List.copyOf(player.getActiveEffects())) {
            if (effect.is(JolCraftEffects.ALCHEMIST_FOCUS)
                    || !effect.getEffect().value().isBeneficial()
                    || effect.isInfiniteDuration()
                    || AlchemistFocusAttachmentHelper.hasBoostedEffect(
                    player,
                    effect.getEffect()
            )) {
                continue;
            }

            increaseDuration(effect, focusLevel);
            player.forceAddEffect(effect, event.getEffectSource());

            AlchemistFocusAttachmentHelper.markBoostedEffect(
                    player,
                    effect.getEffect()
            );

            increasedEffects++;
        }

        logAlchemistFocus(player, focusLevel, increasedEffects);
    }

    private static void clearTrackingIfEffectWasReplaced(
            ServerPlayer player,
            MobEffectInstance oldEffect,
            MobEffectInstance addedEffect
    ) {
        if (oldEffect == null || replacesActiveEffect(oldEffect, addedEffect)) {
            AlchemistFocusAttachmentHelper.clearBoostedEffect(
                    player,
                    addedEffect.getEffect()
            );
        }
    }

    private static boolean replacesActiveEffect(
            MobEffectInstance oldEffect,
            MobEffectInstance addedEffect
    ) {
        if (addedEffect.getAmplifier() > oldEffect.getAmplifier()) {
            return true;
        }

        if (addedEffect.getAmplifier() != oldEffect.getAmplifier()) {
            return false;
        }

        return !oldEffect.isInfiniteDuration()
                && (addedEffect.isInfiniteDuration()
                || addedEffect.getDuration() > oldEffect.getDuration());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        MobEffectInstance removedEffect = event.getEffectInstance();

        if (removedEffect == null || removedEffect.is(JolCraftEffects.ALCHEMIST_FOCUS)) {
            return;
        }

        AlchemistFocusAttachmentHelper.clearBoostedEffect(
                player,
                removedEffect.getEffect()
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.isCanceled()
                || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        MobEffectInstance expiredEffect = event.getEffectInstance();

        if (expiredEffect == null
                || expiredEffect.is(JolCraftEffects.ALCHEMIST_FOCUS)) {
            return;
        }

        AlchemistFocusAttachmentHelper.clearBoostedEffect(
                player,
                expiredEffect.getEffect()
        );
    }

    private static void increaseDuration(
            MobEffectInstance effect,
            int focusLevel
    ) {
        int increasedDuration = Math.round(
                effect.getDuration()
                        * (1.0F
                        + ALCHEMIST_FOCUS_DURATION_BONUS_PER_LEVEL
                        * focusLevel)
        );

        effect.update(new MobEffectInstance(
                effect.getEffect(),
                increasedDuration,
                effect.getAmplifier(),
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon()
        ));
    }

    private static void logAlchemistFocus(
            LivingEntity entity,
            int focusLevel,
            int increasedEffects
    ) {
        if (increasedEffects <= 0) return;

        int percentageIncrease = Math.round(
                ALCHEMIST_FOCUS_DURATION_BONUS_PER_LEVEL
                        * focusLevel
                        * 100.0F
        );

        JolCraftLogs.debug(
                JolCraftLogTags.ENTITY,
                "Alchemist Focus increased duration by {}% for {} beneficial effect(s) on entity {} at {} in {}",
                percentageIncrease,
                increasedEffects,
                entity.getName().getString(),
                JolCraftLogs.roundedPos(entity.position()),
                entity.level().dimension().location()
        );
    }

    @SubscribeEvent
    public static void onStoneSkinDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance stoneSkin = entity.getEffect(JolCraftEffects.STONE_SKIN);

        if (stoneSkin == null) return;

        int level = stoneSkin.getAmplifier() + 1;
        float originalDamage = event.getAmount();
        float reduction = 3.0F * level;
        float modifiedDamage = Math.max(0.0F, originalDamage - reduction);

        event.setAmount(modifiedDamage);

        JolCraftLogs.debug(
                JolCraftLogTags.ENTITY,
                "Stone Skin reduced damage on entity {} at {} in {}, original={}, reduction={}, new={}",
                entity.getName().getString(),
                JolCraftLogs.roundedPos(entity.position()),
                entity.level().dimension().location(),
                originalDamage,
                reduction,
                modifiedDamage
        );

        if (entity.getRandom().nextFloat() >= 0.20F) {
            return;
        }

        if (stoneSkin.getAmplifier() > 0) {
            entity.forceAddEffect(
                    new MobEffectInstance(
                            JolCraftEffects.STONE_SKIN,
                            stoneSkin.getDuration(),
                            stoneSkin.getAmplifier() - 1,
                            stoneSkin.isAmbient(),
                            stoneSkin.isVisible(),
                            stoneSkin.showIcon()
                    ),
                    null
            );
        } else {
            entity.removeEffect(JolCraftEffects.STONE_SKIN);
        }

        JolCraftSoundHelper.entity(
                entity,
                SoundEvents.DEEPSLATE_BREAK,
                1.5F,
                0.9F + entity.getRandom().nextFloat() * 0.2F
        );
    }

    @SubscribeEvent
    public static void onArmorCorrosionDamage(ArmorHurtEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance corrosion =
                entity.getEffect(JolCraftEffects.CORROSION);

        if (corrosion == null) return;

        int level = corrosion.getAmplifier() + 1;

        for (Map.Entry<EquipmentSlot, ArmorHurtEvent.ArmorEntry> entry
                : event.getArmorMap().entrySet()) {
            EquipmentSlot slot = entry.getKey();
            ArmorHurtEvent.ArmorEntry armorEntry = entry.getValue();

            float original = armorEntry.originalDamage;
            float modified = original + level;

            armorEntry.newDamage = modified;

            JolCraftLogs.debug(
                    JolCraftLogTags.ENTITY,
                    "Corrosion armor damage: entity={}, slot={}, effect=corrosion {}, original={}, bonus={}, new={}",
                    entity.getName().getString(),
                    slot,
                    level,
                    original,
                    level,
                    modified
            );
        }
    }

    @SubscribeEvent
    public static void onHearthRemove(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.hasEffect(JolCraftEffects.HOMESTEAD)) return;

        BlockPos pos = HearthAttachmentHelper.activeHearthPos(player);
        if (pos == null) {
            player.removeEffect(JolCraftEffects.HOMESTEAD);
            return;
        }

        if (player.level().getBlockState(pos).getBlock()
                != JolCraftBlocks.HEARTH.get()) {
            HearthAttachmentHelper.clearActiveHearthPos(player);
            player.removeEffect(JolCraftEffects.HOMESTEAD);
            return;
        }

        if (player.blockPosition().distSqr(pos)
                > HearthBlockEntity.RADIUS_SQ) {
            player.removeEffect(JolCraftEffects.HOMESTEAD);
        }
    }

    @SubscribeEvent
    public static void onPoisonApplicable(MobEffectEvent.Applicable event) {
        MobEffectInstance effect = event.getEffectInstance();

        if (!effect.is(MobEffects.POISON)) {
            return;
        }

        LivingEntity entity = event.getEntity();
        double resistance = entity.getAttributeValue(JolCraftAttributes.POISON_RESISTANCE);

        if (resistance <= 0.0D) {
            return;
        }

        if (resistance >= 1.0D || entity.getRandom().nextDouble() < resistance) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);

            JolCraftLogs.debug(
                    JolCraftLogTags.ENTITY,
                    "{} resisted poison application with {}% resistance",
                    entity.getName().getString(),
                    JolCraftLogs.pct1(resistance)
            );
        }
    }
}