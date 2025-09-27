package net.sievert.jolcraft.effect.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundDeliriumPacket;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeliriumCurseEffect extends MobEffect {

    public static final int BLINDNESS_TICKS = 200;

    public DeliriumCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return;
        var nbt = player.getPersistentData();
        String TIMER_KEY = "jolcraft_delirium_timer";
        if (!nbt.contains(TIMER_KEY)) {
            int initial = 100 + player.getRandom().nextInt(400);
            nbt.putInt(TIMER_KEY, initial);
        }
        entity.level().playSound(
                null,
                entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.SOUL_ESCAPE.value(),
                entity.getSoundSource(),
                1.5F,
                0.8F + entity.getRandom().nextFloat() * 0.4F
        );
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return false;

        var nbt = player.getPersistentData();
        String TIMER_KEY = "jolcraft_delirium_timer";

        var effect = player.getEffect(JolCraftEffects.DELIRIUM_CURSE);
        if (effect != null && effect.getDuration() == 1) {
            nbt.remove(TIMER_KEY);
            return true;
        }

        int timer = nbt.contains(TIMER_KEY) ? nbt.getInt(TIMER_KEY) : -1;

        if (timer <= 0) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS, BLINDNESS_TICKS, 0, false, false, false
            ));

            if (player instanceof ServerPlayer serverPlayer) {
                JolCraftNetworking.sendToClient(serverPlayer, new ClientboundDeliriumPacket(BLINDNESS_TICKS));
            }

            int next = 400 + level.random.nextInt(400);
            nbt.putInt(TIMER_KEY, next);
        } else {
            nbt.putInt(TIMER_KEY, timer - 1);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
