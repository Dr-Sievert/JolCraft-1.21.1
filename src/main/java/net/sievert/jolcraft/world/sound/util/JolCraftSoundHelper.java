package net.sievert.jolcraft.world.sound.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundPlaySoundPacket;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Sound helper safe to call from common code.
 * Rules:
 * - World sounds are SERVER-authoritative and must be called once.
 * - Client NEVER plays world sounds.
 * - Local-only sounds are heard only by the owning client.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftSoundHelper {

    private JolCraftSoundHelper() {}

    public static final double DEFAULT_RADIUS = JolCraftNetworking.DEFAULT_RADIUS;

    // ------------------------------------------------------------
    // WORLD SOUNDS (server-authoritative, called once)
    // ------------------------------------------------------------

    public static void play(Level level,
                            double radius,
                            ResourceLocation soundId,
                            SoundSource source,
                            double x, double y, double z,
                            float volume,
                            float pitch) {

        if (level.isClientSide) return;

        BlockPos pos = BlockPos.containing(x, y, z);
        JolCraftNetworking.sendToNearbyClients(
                level,
                pos,
                radius,
                new ClientboundPlaySoundPacket(soundId, x, y, z, source, volume, pitch)
        );
    }

    public static void play(Level level,
                            ResourceLocation soundId,
                            SoundSource source,
                            double x, double y, double z,
                            float volume,
                            float pitch) {

        if (level.isClientSide) return;

        BlockPos pos = BlockPos.containing(x, y, z);
        JolCraftNetworking.sendToNearbyClients(
                level,
                pos,
                new ClientboundPlaySoundPacket(soundId, x, y, z, source, volume, pitch)
        );
    }

    public static void play(Level level,
                            double radius,
                            SoundEvent sound,
                            SoundSource source,
                            double x, double y, double z,
                            float volume,
                            float pitch) {

        ResourceLocation id = BuiltInRegistries.SOUND_EVENT.getKey(sound);
        if (id == null) return;

        play(level, radius, id, source, x, y, z, volume, pitch);
    }

    public static void play(Level level,
                            SoundEvent sound,
                            SoundSource source,
                            double x, double y, double z,
                            float volume,
                            float pitch) {

        ResourceLocation id = BuiltInRegistries.SOUND_EVENT.getKey(sound);
        if (id == null) return;

        play(level, id, source, x, y, z, volume, pitch);
    }

    // ------------------------------------------------------------
    // LOCAL-ONLY SOUNDS (single player hears it)
    // ------------------------------------------------------------

    public static void playLocalOnly(Player player,
                                     ResourceLocation soundId,
                                     SoundSource source,
                                     double x, double y, double z,
                                     float volume,
                                     float pitch) {

        Level level = player.level();

        if (level.isClientSide) {
            if (!(player instanceof LocalPlayer)) return;

            var opt = BuiltInRegistries.SOUND_EVENT.get(soundId);
            if (opt.isEmpty()) return;

            SoundEvent sound = opt.get().value();
            level.playLocalSound(x, y, z, sound, source, volume, pitch, false);
            return;
        }

        if (player instanceof ServerPlayer sp) {
            JolCraftNetworking.sendToClient(
                    sp,
                    new ClientboundPlaySoundPacket(soundId, x, y, z, source, volume, pitch)
            );
        }
    }

    public static void playLocalOnly(Player player,
                                     SoundEvent sound,
                                     SoundSource source,
                                     double x, double y, double z,
                                     float volume,
                                     float pitch) {

        ResourceLocation id = BuiltInRegistries.SOUND_EVENT.getKey(sound);
        if (id == null) return;

        playLocalOnly(player, id, source, x, y, z, volume, pitch);
    }

    // ------------------------------------------------------------
    // ENTITY CONVENIENCE
    // ------------------------------------------------------------

    public static void playNeutral(LivingEntity entity, SoundEvent sound) {
        play(entity, DEFAULT_RADIUS, sound, SoundSource.NEUTRAL, 1.0F);
    }

    public static void play(LivingEntity entity,
                            double radius,
                            SoundEvent sound,
                            SoundSource source,
                            float volume) {

        BlockPos pos = entity.blockPosition();
        play(entity.level(),
                radius,
                sound,
                source,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                volume,
                entity.getVoicePitch());
    }

    // ------------------------------------------------------------
    // NAMED HELPERS
    // ------------------------------------------------------------

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