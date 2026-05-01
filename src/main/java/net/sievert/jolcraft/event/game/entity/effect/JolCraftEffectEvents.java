package net.sievert.jolcraft.event.game.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.player.attachment.custom.hearth.HearthAttachmentHelper;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.custom.HearthBlockEntity;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;

import java.util.Map;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftEffectEvents {

    @SubscribeEvent
    public static void onCrowdControlledLivingEntity(EntityTickEvent.Post event) {
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
        if (entity.isUsingItem() && !JolCraftEquipmentHelper.isRangedWeapon(entity.getMainHandItem())) {
            entity.stopUsingItem();
        }
    }

    @SubscribeEvent
    public static void onDwarvenHasteMining(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        MobEffectInstance effect = player.getEffect(JolCraftEffects.DWARVEN_HASTE);
        if (effect == null) return;

        int amplifier = effect.getAmplifier();

        float originalSpeed = event.getOriginalSpeed();
        float newSpeed = originalSpeed * (1.0F + 0.2F * (amplifier + 1));
        event.setNewSpeed(newSpeed);
    }

    @SubscribeEvent
    public static void onArmorCorrosionDamage(ArmorHurtEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance corrosion = entity.getEffect(JolCraftEffects.CORROSION);
        if (corrosion == null) return;

        int amp = corrosion.getAmplifier();
        float multiplier = 1.0F + 0.20F * (1 + amp);

        for (Map.Entry<EquipmentSlot, ArmorHurtEvent.ArmorEntry> entry : event.getArmorMap().entrySet()) {
            EquipmentSlot slot = entry.getKey();
            ArmorHurtEvent.ArmorEntry armorEntry = entry.getValue();

            float original = armorEntry.originalDamage;
            float modified = original * multiplier;

            armorEntry.newDamage = modified;

            JolCraftLogs.debug(
                    JolCraftLogTags.ENTITY,
                    "Corrosion armor damage: entity={}, slot={}, effect= corrosion {}, original={}, multiplier={}, new={}",
                    entity.getName().getString(),
                    slot,
                    1 + amp,
                    original,
                    JolCraftLogs.pct1(multiplier),
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

        if (player.level().getBlockState(pos).getBlock() != JolCraftBlocks.HEARTH.get()) {
            HearthAttachmentHelper.clearActiveHearthPos(player);
            player.removeEffect(JolCraftEffects.HOMESTEAD);
            return;
        }

        if (player.blockPosition().distSqr(pos) > HearthBlockEntity.RADIUS_SQ) {
            player.removeEffect(JolCraftEffects.HOMESTEAD);
        }
    }
}
