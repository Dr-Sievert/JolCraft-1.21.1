package net.sievert.jolcraft.world.sound.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundPlaySoundPacket;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Sound playing helper that is safe to call from common code.
 * - On the physical client: plays sound locally via Level#playLocalSound.
 * - On the server: sends a clientbound play-sound packet to nearby players.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftSoundHelper {

    private JolCraftSoundHelper() {}

    public static final double DEFAULT_RADIUS = 32.0D;

    // -------------------------
    // Core packet-backed API
    // -------------------------

    public static void play(Level level, SoundEvent sound, SoundSource source,
                            double x, double y, double z,
                            float volume, float pitch) {
        play(level, DEFAULT_RADIUS, sound, source, x, y, z, volume, pitch);
    }

    public static void play(Level level, double radius, SoundEvent sound, SoundSource source,
                            double x, double y, double z,
                            float volume, float pitch) {

        ResourceLocation id = BuiltInRegistries.SOUND_EVENT.getKey(sound);
        if (id == null) return;

        play(level, radius, id, source, x, y, z, volume, pitch);
    }

    public static void play(Level level, ResourceLocation soundId, SoundSource source,
                            double x, double y, double z,
                            float volume, float pitch) {
        play(level, DEFAULT_RADIUS, soundId, source, x, y, z, volume, pitch);
    }

    public static void play(Level level, double radius, ResourceLocation soundId, SoundSource source,
                            double x, double y, double z,
                            float volume, float pitch) {

        if (level.isClientSide) {
            var opt = BuiltInRegistries.SOUND_EVENT.get(soundId);
            if (opt.isEmpty()) return;

            var sound = opt.get().value();
            level.playLocalSound(x, y, z, sound, source, volume, pitch, false);
            return;
        }

        BlockPos pos = BlockPos.containing(x, y, z);
        JolCraftNetworking.sendToNearbyClients(
                level,
                pos,
                radius,
                new ClientboundPlaySoundPacket(soundId, x, y, z, source, volume, pitch)
        );
    }

    // -------------------------
    // Convenience for entities
    // -------------------------

    public static void playNeutral(LivingEntity entity, SoundEvent sound) {
        play(entity, DEFAULT_RADIUS, sound, SoundSource.NEUTRAL, 1.0F);
    }

    public static void play(LivingEntity entity, double radius, SoundEvent sound, SoundSource source, float volume) {
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        float pitch = entity.getVoicePitch();
        play(level, radius, sound, source, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, volume, pitch);
    }

    // -------------------------
    // Existing named helpers
    // -------------------------

    // Dwarf
    public static void playDwarfNo(LivingEntity entity) {
        playNeutral(entity, JolCraftSounds.DWARF_NO.get());
    }

    public static void playDwarfYes(LivingEntity entity) {
        playNeutral(entity, JolCraftSounds.DWARF_YES.get());
    }

    // Villager
    public static void playVillagerNo(LivingEntity entity) {
        playNeutral(entity, SoundEvents.VILLAGER_NO);
    }

    public static void playVillagerYes(LivingEntity entity) {
        playNeutral(entity, SoundEvents.VILLAGER_YES);
    }

    public static void playVillagerFisherman(LivingEntity entity) {
        playNeutral(entity, SoundEvents.VILLAGER_WORK_FISHERMAN);
    }
}