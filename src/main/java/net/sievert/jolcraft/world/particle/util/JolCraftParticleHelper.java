package net.sievert.jolcraft.world.particle.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundParticlePacket;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Particle spawning helper safe to call from common code.
 * Rules:
 * - World particles are SERVER-authoritative and must be spawned once.
 * - Client NEVER spawns world particles.
 * - Local-only particles are seen only by the owning client.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftParticleHelper {

    private JolCraftParticleHelper() {}

    public static final double DEFAULT_RADIUS = JolCraftNetworking.DEFAULT_RADIUS;

    // ------------------------------------------------------------
    // WORLD PARTICLES (server-authoritative, called once)
    // ------------------------------------------------------------

    public static void spawn(Level level,
                             ParticleOptions particle,
                             double x, double y, double z,
                             double vx, double vy, double vz) {
        spawn(level, DEFAULT_RADIUS, particle, false, false, x, y, z, vx, vy, vz);
    }

    public static void spawn(Level level,
                             double radius,
                             ParticleOptions particle,
                             double x, double y, double z,
                             double vx, double vy, double vz) {
        spawn(level, radius, particle, false, false, x, y, z, vx, vy, vz);
    }

    public static void spawn(Level level,
                             double radius,
                             ParticleOptions particle,
                             boolean overrideLimiter,
                             boolean alwaysShow,
                             double x, double y, double z,
                             double vx, double vy, double vz) {

        if (level.isClientSide) return;

        BlockPos pos = BlockPos.containing(x, y, z);
        JolCraftNetworking.sendToNearbyClients(
                level,
                pos,
                radius,
                new ClientboundParticlePacket(
                        particle,
                        overrideLimiter,
                        alwaysShow,
                        x, y, z,
                        vx, vy, vz
                )
        );
    }

    // ------------------------------------------------------------
    // LOCAL-ONLY PARTICLES (single player sees it)
    // ------------------------------------------------------------

    public static void spawnLocalOnly(Player player,
                                      ParticleOptions particle,
                                      boolean overrideLimiter,
                                      boolean alwaysShow,
                                      double x, double y, double z,
                                      double vx, double vy, double vz) {

        Level level = player.level();

        if (level.isClientSide) {
            if (!(player instanceof LocalPlayer)) return;
            level.addParticle(particle, overrideLimiter, alwaysShow, x, y, z, vx, vy, vz);
            return;
        }

        if (player instanceof ServerPlayer sp) {
            JolCraftNetworking.sendToClient(
                    sp,
                    new ClientboundParticlePacket(
                            particle,
                            overrideLimiter,
                            alwaysShow,
                            x, y, z,
                            vx, vy, vz
                    )
            );
        }
    }
}