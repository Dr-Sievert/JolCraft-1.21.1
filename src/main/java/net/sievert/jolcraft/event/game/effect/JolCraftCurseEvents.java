package net.sievert.jolcraft.event.game.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftCurseEvents {

    //General

    private static final List<Holder<MobEffect>> CURSE_EFFECTS = List.of(
            JolCraftEffects.DELIRIUM_CURSE,
            JolCraftEffects.CURSED_WOUND
    );

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onCurseAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var instance = event.getEffectInstance();

        if (CURSE_EFFECTS.stream().anyMatch(curse -> instance.getEffect().is(curse))) {
            PlaySound.curse(player);
        }
    }

    private static final Set<UUID> MILK_REMOVAL_PLAYERS = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onMilkStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player) || player.isCreative()) return;
        if ((event.getItem().is(Tags.Items.BUCKETS_MILK) || event.getItem().is(Tags.Items.DRINKS_MILK))
                && CURSE_EFFECTS.stream().anyMatch(player::hasEffect)) {
            MILK_REMOVAL_PLAYERS.add(player.getUUID());
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!MILK_REMOVAL_PLAYERS.contains(player.getUUID())) return;

        if (CURSE_EFFECTS.stream().anyMatch(curse -> event.getEffect().is(curse))) {
            PlaySound.curse(player);

            ResourceLocation effectId = event.getEffect()
                    .unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);

            JolCraftLogs.debug(
                    JolCraftLogTags.PLAYER,
                    "Blocked {} removal for {}",
                    effectId,
                    player.getDisplayName().getString()
            );

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMilkStop(LivingEntityUseItemEvent.Stop event) {
        clearMilkGuard(event.getEntity(), event.getItem().is(Tags.Items.BUCKETS_MILK) || event.getItem().is(Tags.Items.DRINKS_MILK));
    }

    @SubscribeEvent
    public static void onMilkFinish(LivingEntityUseItemEvent.Finish event) {
        clearMilkGuard(event.getEntity(), event.getItem().is(Tags.Items.BUCKETS_MILK) || event.getItem().is(Tags.Items.DRINKS_MILK));
    }

    private static void clearMilkGuard(LivingEntity entity, boolean wasMilk) {
        if (!wasMilk) return;
        if (!(entity instanceof Player player)) return;
        MILK_REMOVAL_PLAYERS.remove(player.getUUID());
    }

    //Cursed Wound

    @SubscribeEvent
    public static void onCursedWoundHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(JolCraftEffects.CURSED_WOUND)) {
            event.setCanceled(true);
        }
    }

}