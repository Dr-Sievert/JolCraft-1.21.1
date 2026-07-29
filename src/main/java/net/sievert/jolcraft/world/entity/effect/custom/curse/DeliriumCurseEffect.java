package net.sievert.jolcraft.world.entity.effect.custom.curse;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDeliriumCursePacket;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeliriumCurseEffect extends AbstractCurseEffect {

    public static final int EPISODE_TICKS = 200;

    private static final int MIN_INITIAL_DELAY = 100;
    private static final int INITIAL_DELAY_RANGE = 400;
    private static final int MIN_REPEAT_DELAY = 400;
    private static final int REPEAT_DELAY_RANGE = 400;

    private static final Map<UUID, Integer> EPISODE_TIMERS = new ConcurrentHashMap<>();

    private static final String NBT_EPISODE_END = JolCraftStrings.underscored(
            JolCraftDictionary.DELIRIUM,
            JolCraftDictionary.END
    );

    public DeliriumCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static void cleanupRuntime(ServerPlayer player) {
        EPISODE_TIMERS.remove(player.getUUID());
        clearEpisodeEnd(player);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        EPISODE_TIMERS.put(player.getUUID(), createInitialDelay(player));
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return true;
        }

        MobEffectInstance effect = player.getEffect(JolCraftEffects.DELIRIUM_CURSE);
        if (effect == null || effect.getDuration() <= 1) {
            cleanupRuntime(player);
            return true;
        }

        UUID playerId = player.getUUID();
        int timer = EPISODE_TIMERS.computeIfAbsent(playerId, __ -> createInitialDelay(level));

        if (timer > 0) {
            EPISODE_TIMERS.put(playerId, timer - 1);
            return true;
        }

        triggerEpisode(player, level);
        EPISODE_TIMERS.put(playerId, createRepeatDelay(level));
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

    private static void triggerEpisode(ServerPlayer player, ServerLevel level) {
        player.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                EPISODE_TICKS,
                0,
                false,
                false,
                false
        ));

        player.addEffect(new MobEffectInstance(
                MobEffects.CONFUSION,
                EPISODE_TICKS,
                0,
                false,
                false,
                false
        ));

        setEpisodeEnd(player, level.getGameTime() + EPISODE_TICKS);

        JolCraftNetworking.sendToClient(player, new ClientboundDeliriumCursePacket(EPISODE_TICKS));

        JolCraftSoundHelper.playLocal(
                player,
                SoundEvents.AMBIENT_CAVE.value(),
                player.getSoundSource(),
                player.getX(),
                player.getY(),
                player.getZ(),
                0.7F + level.random.nextFloat() * 0.4F,
                0.8F + level.random.nextFloat() * 0.4F
        );
    }

    private static int createInitialDelay(Player player) {
        return MIN_INITIAL_DELAY + player.getRandom().nextInt(INITIAL_DELAY_RANGE);
    }

    private static int createInitialDelay(ServerLevel level) {
        return MIN_INITIAL_DELAY + level.random.nextInt(INITIAL_DELAY_RANGE);
    }

    private static int createRepeatDelay(ServerLevel level) {
        return MIN_REPEAT_DELAY + level.random.nextInt(REPEAT_DELAY_RANGE);
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