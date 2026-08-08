package net.sievert.jolcraft.event.game.entity.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.sievert.jolcraft.event.game.entity.effect.harmful.JolCraftCrowdControlEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.harmful.JolCraftCurseEventsHelper;
import net.sievert.jolcraft.event.game.entity.util.JolCraftEntityDamageEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.util.JolCraftEffectDurationEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.util.JolCraftStackingEffectEventsHelper;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.curse.DeliriumCurseEffect;

public final class JolCraftEffectEvents {

    private JolCraftEffectEvents() {}

    public static void onEffectAdded(MobEffectEvent.Added event) {
        JolCraftStackingEffectEventsHelper.onEffectAdded(event);
        JolCraftEffectDurationEventsHelper.onEffectAdded(event);
        JolCraftCurseEventsHelper.onEffectAdded(event);
    }

    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        JolCraftCurseEventsHelper.onEffectRemoved(event);
        cleanupDelirium(event.getEntity(), event.getEffectInstance());
    }

    public static void onEffectExpired(MobEffectEvent.Expired event) {
        JolCraftCurseEventsHelper.onEffectExpired(event);
        cleanupDelirium(event.getEntity(), event.getEffectInstance());
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        JolCraftCurseEventsHelper.onIncomingDamage(event);
    }

    public static void onArmorHurt(ArmorHurtEvent event) {
        JolCraftEntityDamageEventsHelper.onArmorHurt(event);
    }

    public static void onLivingHeal(LivingHealEvent event) {
        JolCraftCurseEventsHelper.onLivingHeal(event);
    }

    public static void onEntityTick(EntityTickEvent.Post event) {
        JolCraftCrowdControlEventsHelper.onEntityTick(event);
    }

    private static void cleanupDelirium(
            LivingEntity entity,
            MobEffectInstance effect
    ) {
        if (entity instanceof ServerPlayer player
                && effect != null
                && effect.is(JolCraftEffects.DELIRIUM_CURSE)) {
            DeliriumCurseEffect.cleanupRuntime(player);
        }
    }
}