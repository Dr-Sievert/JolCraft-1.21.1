package net.sievert.jolcraft.world.effect.custom.curse;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDeliriumCursePacket;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeliriumCurseEffect extends MobEffect {

    public static final int EPISODE_TICKS = 200;

    private static final Map<UUID, Integer> EPISODE_TIMERS = new ConcurrentHashMap<>();

    private static final String NBT_EPISODE_END = JolCraftStrings.underscored(JolCraftDictionary.DELIRIUM, JolCraftDictionary.END);

    public DeliriumCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static void cleanupRuntime(ServerPlayer player) {
        EPISODE_TIMERS.remove(player.getUUID());
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return;

        EPISODE_TIMERS.computeIfAbsent(player.getUUID(), id ->
                100 + player.getRandom().nextInt(400)
        );
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return false;

        var effect = player.getEffect(JolCraftEffects.DELIRIUM_CURSE);
        if (effect == null || effect.getDuration() <= 1) {
            EPISODE_TIMERS.remove(player.getUUID());
            clearEpisodeEnd(player);
            return true;
        }

        UUID id = player.getUUID();

        int timer = EPISODE_TIMERS.computeIfAbsent(id, __ ->
                100 + level.random.nextInt(400)
        );

        if (timer <= 0) {

            // Episode fires: apply effects
            player.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS, EPISODE_TICKS, 0, false, false, false
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.CONFUSION, EPISODE_TICKS, 0, false, false, false
            ));

            long endTick = level.getGameTime() + EPISODE_TICKS;
            setEpisodeEnd(player, endTick);

            if (player instanceof ServerPlayer serverPlayer) {
                JolCraftNetworking.sendToClient(serverPlayer, new ClientboundDeliriumCursePacket(EPISODE_TICKS));
            }

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

    public static int getRemainingEpisodeTicks(ServerPlayer player) {
        long now = player.level().getGameTime();
        long end = getEpisodeEnd(player);
        return (int) Math.max(0L, end - now);
    }

    private static long getEpisodeEnd(Player player) {
        CompoundTag data = player.getPersistentData();
        return data.getLong(NBT_EPISODE_END);
    }

    private static void setEpisodeEnd(Player player, long endTick) {
        player.getPersistentData().putLong(NBT_EPISODE_END, endTick);
    }

    private static void clearEpisodeEnd(Player player) {
        player.getPersistentData().remove(NBT_EPISODE_END);
    }
}