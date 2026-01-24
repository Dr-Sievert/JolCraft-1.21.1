package net.sievert.jolcraft.event.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftCurseEvents {

    //Delirium

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

    private static boolean isMilkRemoval = false;

    private static final List<Holder<MobEffect>> CURSE_EFFECTS = List.of(
            JolCraftEffects.DELIRIUM_CURSE,
            JolCraftEffects.CURSED_WOUND
    );

    @SubscribeEvent
    public static void onMilkStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player) || player.isCreative()) return;
        if ((event.getItem().is(Tags.Items.BUCKETS_MILK) || event.getItem().is(Tags.Items.DRINKS_MILK))
                && CURSE_EFFECTS.stream().anyMatch(curse -> event.getEntity().hasEffect(curse))) {
            isMilkRemoval = true;
        }
    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player player) || !isMilkRemoval) return;
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
    public static void onMilkStopOrFinish(LivingEntityUseItemEvent.Stop event) {
        if (!(event.getEntity() instanceof Player) || !isMilkRemoval) return;
        isMilkRemoval = false;
    }
}
