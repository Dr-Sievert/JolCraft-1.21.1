package net.sievert.jolcraft.event.game.entity.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.damage.JolCraftDamageTypes;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftCurseEvents {

    private JolCraftCurseEvents() {}

    @SubscribeEvent
    public static void onCursedWoundHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(JolCraftEffects.CURSED_WOUND)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFrailtyCurseDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance frailty = entity.getEffect(JolCraftEffects.FRAILTY_CURSE);
        if (frailty == null) return;

        float originalDamage = event.getAmount();

        int amp = frailty.getAmplifier();

        float multiplier = 1.0F + 0.25F * (amp + 1);

        event.setAmount(originalDamage * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onVitalityCurse(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level)) return;

        MobEffectInstance addedEffect = event.getEffectInstance();
        if (!addedEffect.is(JolCraftEffects.VITALITY_CURSE)) return;

        MobEffectInstance currentEffect = entity.getEffect(JolCraftEffects.VITALITY_CURSE);
        if (currentEffect == null) return;

        if (entity instanceof ServerPlayer player && player.isCreative()) return;
        if (currentEffect.getAmplifier() < 4) return;

        DamageSource source = level.damageSources().source(JolCraftDamageTypes.VITALITY_CURSE);
        entity.hurt(source, Float.MAX_VALUE);
    }
}