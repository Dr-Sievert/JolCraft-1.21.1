package net.sievert.jolcraft.event.game.entity.effect;

import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.sievert.jolcraft.event.game.entity.effect.harmful.JolCraftCrowdControlEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.harmful.JolCraftCurseEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.util.JolCraftEffectDamageEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.util.JolCraftEffectDurationEventsHelper;
import net.sievert.jolcraft.event.game.entity.effect.util.JolCraftStackingEffectEventsHelper;

public final class JolCraftEffectEvents {

    private JolCraftEffectEvents() {}

    public static void onEffectAdded(MobEffectEvent.Added event) {
        JolCraftStackingEffectEventsHelper.onEffectAdded(event);
        JolCraftEffectDurationEventsHelper.onEffectAdded(event);
        JolCraftCurseEventsHelper.onEffectAdded(event);
    }

    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        JolCraftCurseEventsHelper.onEffectRemoved(event);
    }

    public static void onEffectExpired(MobEffectEvent.Expired event) {
        JolCraftCurseEventsHelper.onEffectExpired(event);
    }

    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        JolCraftEffectDamageEventsHelper.onEffectApplicable(event);
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        JolCraftEffectDamageEventsHelper.onIncomingDamage(event);
        JolCraftCurseEventsHelper.onIncomingDamage(event);
    }

    public static void onArmorHurt(ArmorHurtEvent event) {
        JolCraftEffectDamageEventsHelper.onArmorHurt(event);
    }

    public static void onLivingHeal(LivingHealEvent event) {
        JolCraftCurseEventsHelper.onLivingHeal(event);
    }

    public static void onEntityTick(EntityTickEvent.Post event) {
        JolCraftCrowdControlEventsHelper.onEntityTick(event);
    }
}