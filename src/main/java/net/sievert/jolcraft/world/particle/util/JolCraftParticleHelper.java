package net.sievert.jolcraft.world.particle.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.c2s.ServerboundSpawnParticlePacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Particle helper safe to call from common code.
 * Rules:
 * - World particles are SERVER-authoritative and must be spawned once.
 * - Client NEVER spawns world particles (prevents double-spawn).
 * - Local-only particles are seen only by the owning client.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftParticleHelper {

    private JolCraftParticleHelper() {}

    // ------------------------------------------------------------
    // WORLD PARTICLES (server-authoritative, called once)
    // ------------------------------------------------------------

    public static void spawn(Level level,
                             ParticleOptions particle,
                             boolean overrideLimiter,
                             boolean alwaysShow,
                             double x, double y, double z,
                             double vx, double vy, double vz) {

        if (!level.isClientSide) {
            if (!(level instanceof ServerLevel serverLevel)) return;

            serverLevel.sendParticles(
                    particle,
                    overrideLimiter,
                    alwaysShow,
                    x, y, z,
                    0,
                    vx, vy, vz,
                    1.0D
            );
            return;
        }

        JolCraftNetworking.sendToServer(
                new ServerboundSpawnParticlePacket(
                        particle,
                        overrideLimiter,
                        alwaysShow,
                        x, y, z,
                        vx, vy, vz
                )
        );
    }

    public static void spawn(Level level,
                             ParticleOptions particle,
                             double x, double y, double z,
                             double vx, double vy, double vz) {
        spawn(level, particle, particle.getType().getOverrideLimiter(), false, x, y, z, vx, vy, vz);
    }

    // ------------------------------------------------------------
    // LOCAL-ONLY PARTICLES (single player sees it)
    // ------------------------------------------------------------

    public static void spawnLocal(Player player,
                                  ParticleOptions particle,
                                  boolean overrideLimiter,
                                  boolean alwaysShow,
                                  double x, double y, double z,
                                  double vx, double vy, double vz) {

        if (!Double.isFinite(vx) || !Double.isFinite(vy) || !Double.isFinite(vz)) return;

        Level level = player.level();

        if (level.isClientSide) {
            Player local = JolCraftProxy.access().getLocalPlayer();
            if (local != player) return;

            level.addParticle(particle, overrideLimiter, alwaysShow, x, y, z, vx, vy, vz);
            return;
        }

        if (player instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundLevelParticlesPacket(
                    particle,
                    overrideLimiter,
                    alwaysShow,
                    x, y, z,
                    (float) vx,
                    (float) vy,
                    (float) vz,
                    1.0F,
                    0
            ));
        }
    }

    public static void spawnLocal(Player player,
                                  ParticleOptions particle,
                                  double x, double y, double z,
                                  double vx, double vy, double vz) {
        spawnLocal(player, particle, particle.getType().getOverrideLimiter(), false, x, y, z, vx, vy, vz);
    }
}