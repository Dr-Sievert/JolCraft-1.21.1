package net.sievert.jolcraft.world.effect.custom.curse;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDeliriumPacket;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeliriumCurseEffect extends MobEffect {

    public static final int BLINDNESS_TICKS = 200;

    // Runtime-only episode timers (not persisted)
    private static final Map<UUID, Integer> EPISODE_TIMERS = new ConcurrentHashMap<>();

    public DeliriumCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return;

        // Initialize runtime timer if absent
        EPISODE_TIMERS.computeIfAbsent(player.getUUID(), id ->
                100 + player.getRandom().nextInt(400)
        );
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return false;

        // If the effect is gone (or about to expire), cleanup runtime timer.
        var effect = player.getEffect(JolCraftEffects.DELIRIUM_CURSE);
        if (effect == null || effect.getDuration() <= 1) {
            EPISODE_TIMERS.remove(player.getUUID());
            return true;
        }

        UUID id = player.getUUID();

        // Server restart / relog safety: effect may exist but runtime map is empty.
        int timer = EPISODE_TIMERS.computeIfAbsent(id, __ ->
                100 + level.random.nextInt(400)
        );

        if (timer <= 0) {
            // Episode fires: apply blindness (server)
            player.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS, BLINDNESS_TICKS, 0, false, false, false
            ));

            // Episode audio window for muffling (client-side mixing)
            if (player instanceof ServerPlayer serverPlayer) {
                JolCraftNetworking.sendToClient(serverPlayer, new ClientboundDeliriumPacket(BLINDNESS_TICKS));
            }

            // Episode ambience: local-only to this player (server-triggered)
            JolCraftSoundHelper.playLocal(
                    player,
                    SoundEvents.AMBIENT_CAVE.value(),
                    player.getSoundSource(),
                    player.getX(), player.getY(), player.getZ(),
                    0.7F + level.random.nextFloat() * 0.4F,
                    0.8F + level.random.nextFloat() * 0.4F
            );

            int next = 400 + level.random.nextInt(400);
            EPISODE_TIMERS.put(id, next);
        } else {
            EPISODE_TIMERS.put(id, timer - 1);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}