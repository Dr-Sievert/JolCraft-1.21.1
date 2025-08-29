package net.sievert.jolcraft.event.game;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.JolCraftSounds;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftCurseEvents {

    //Delirium

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


    // ThreadLocal flag for tracking milk bucket effect clearing
    private static final ThreadLocal<Boolean> isMilkRemoval = ThreadLocal.withInitial(() -> false);


    @SubscribeEvent
    public static void onMilkStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if ((event.getItem().is(Items.MILK_BUCKET) || event.getItem().is(JolCraftItems.MUFFHORN_MILK_BUCKET.get())) && event.getEntity().hasEffect(JolCraftEffects.DELIRIUM_CURSE)) {
            isMilkRemoval.set(true);
        }
    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (isMilkRemoval.get() && event.getEffect().is(JolCraftEffects.DELIRIUM_CURSE)) {
            event.setCanceled(true);
            isMilkRemoval.set(false);

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

    @SubscribeEvent
    public static void onMilkStopOrFinish(LivingEntityUseItemEvent.Stop event) {
        if (!(event.getEntity() instanceof Player) || isMilkRemoval.get() == false) {
            return;
        }
        isMilkRemoval.set(false);
    }

    @SubscribeEvent
    public static void onMilkFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player) || isMilkRemoval.get() == false) {
            return;
        }
        isMilkRemoval.set(false);
    }
}
