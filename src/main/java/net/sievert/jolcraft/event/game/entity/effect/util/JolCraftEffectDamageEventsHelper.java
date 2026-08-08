package net.sievert.jolcraft.event.game.entity.effect.util;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import java.util.Map;

public final class JolCraftEffectDamageEventsHelper {

    private JolCraftEffectDamageEventsHelper() {}

    public static void applyEarlyFlatDefenses(LivingIncomingDamageEvent event) {
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

        if (entity.getRandom().nextFloat() >= 0.20F) return;

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

    public static void onArmorHurt(ArmorHurtEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance corrosion = entity.getEffect(JolCraftEffects.CORROSION);

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
}
