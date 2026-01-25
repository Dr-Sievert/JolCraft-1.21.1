package net.sievert.jolcraft.event.game;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.sievert.jolcraft.sound.JolCraftSounds;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftCurseEvents {

    // Delirium

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onDeliriumCurseAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var instance = event.getEffectInstance();
        if (instance.getEffect().is(JolCraftEffects.DELIRIUM_CURSE)) {
            if (!player.level().isClientSide()) {
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        JolCraftSounds.CURSE.get(),
                        player.getSoundSource(),
                        1.0F, 1.0F
                );
            }
        }
    }

    private static final List<Holder<MobEffect>> CURSE_EFFECTS = List.of(
            JolCraftEffects.DELIRIUM_CURSE,
            JolCraftEffects.CURSED_WOUND
    );

    // Per-player guard: fixes MP bleed + avoids "stuck true" if Finish fires instead of Stop.
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
            if (!player.level().isClientSide()) {
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        JolCraftSounds.CURSE.get(),
                        player.getSoundSource(),
                        1.0F, 1.0F
                );
            }
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
}