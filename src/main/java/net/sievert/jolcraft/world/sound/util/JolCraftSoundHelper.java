package net.sievert.jolcraft.world.sound.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Sound helper safe to call from common code.
 *
 * Naming:
 * - play(...)      → world sound (vanilla broadcast, dupe-safe)
 * - playLocal(...) → single-player only
 *
 * Vanilla guarantee:
 * Level#playSound(@Nullable Player player, ...)
 * - Server: broadcasts to everyone EXCEPT player
 * - Client: plays ONLY if player == local player
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftSoundHelper {

    private JolCraftSoundHelper() {}

    // ------------------------------------------------------------
    // PRIMITIVES
    // ------------------------------------------------------------

    /** World sound without actor semantics (server-authoritative). */
    public static void play(Level level,
                            SoundEvent sound,
                            SoundSource source,
                            double x, double y, double z,
                            float volume,
                            float pitch) {

        if (level.isClientSide) return;
        level.playSound(null, x, y, z, sound, source, volume, pitch);
    }

    /** World sound with actor semantics (dupe-safe, side-safe). */
    public static void play(Player actor,
                            SoundEvent sound,
                            SoundSource source,
                            double x, double y, double z,
                            float volume,
                            float pitch) {

        actor.level().playSound(actor, x, y, z, sound, source, volume, pitch);
    }

    /** Local-only positional sound for exactly one player. */
    public static void playLocal(Player player,
                                 SoundEvent sound,
                                 SoundSource source,
                                 double x, double y, double z,
                                 float volume,
                                 float pitch) {

        Level level = player.level();

        if (level.isClientSide) {
            level.playLocalSound(x, y, z, sound, source, volume, pitch, false);
            return;
        }

        if (player instanceof ServerPlayer sp) {
            Holder<SoundEvent> holder = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound);
            long seed = level.getRandom().nextLong();
            sp.connection.send(
                    new ClientboundSoundPacket(holder, source, x, y, z, volume, pitch, seed)
            );
        }
    }

    // ------------------------------------------------------------
    // PLAYER
    // ------------------------------------------------------------

    /** World sound at player position using player's sound source. */
    public static void player(Player player,
                              SoundEvent sound,
                              float volume,
                              float pitch) {

        play(
                player,
                sound,
                player.getSoundSource(),
                player.getX(), player.getY(), player.getZ(),
                volume,
                pitch
        );
    }

    /** World sound at player position (1.0 / 1.0). */
    public static void player(Player player, SoundEvent sound) {
        player(player, sound, 1.0F, 1.0F);
    }

    // ------------------------------------------------------------
    // ENTITY
    // ------------------------------------------------------------

    /** World sound at entity position using voice pitch. */
    public static void entity(LivingEntity entity,
                              SoundEvent sound,
                              float volume) {

        play(
                entity.level(),
                sound,
                entity.getSoundSource(),
                entity.getX(), entity.getY(), entity.getZ(),
                volume,
                entity.getVoicePitch()
        );
    }

    /** World sound at entity position (1.0 volume). */
    public static void entity(LivingEntity entity, SoundEvent sound) {
        entity(entity, sound, 1.0F);
    }

    // ------------------------------------------------------------
    // BLOCK
    // ------------------------------------------------------------

    private static void blockInternal(Level level,
                                      BlockPos pos,
                                      SoundEvent sound,
                                      float volume,
                                      float pitch) {

        play(
                level,
                sound,
                SoundSource.BLOCKS,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                volume,
                pitch
        );
    }

    /** Preferred block sound entry: BlockEntity. */
    public static void block(BlockEntity be,
                             SoundEvent sound,
                             float volume,
                             float pitch) {

        @Nullable Level level = be.getLevel();
        if (level == null) return;

        blockInternal(level, be.getBlockPos(), sound, volume, pitch);
    }

    /** BlockEntity sound (1.0 / 1.0). */
    public static void block(BlockEntity be, SoundEvent sound) {
        block(be, sound, 1.0F, 1.0F);
    }

    /** Fallback block sound entry. */
    public static void block(Level level,
                             BlockPos pos,
                             SoundEvent sound,
                             float volume,
                             float pitch) {

        blockInternal(level, pos, sound, volume, pitch);
    }

    /** Fallback block sound (1.0 / 1.0). */
    public static void block(Level level, BlockPos pos, SoundEvent sound) {
        block(level, pos, sound, 1.0F, 1.0F);
    }
}
