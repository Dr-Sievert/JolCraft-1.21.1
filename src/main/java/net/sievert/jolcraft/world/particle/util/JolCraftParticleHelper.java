package net.sievert.jolcraft.world.particle.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundParticlePacket;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Particle spawning helper safe to call from common code.
 *
 * Rules:
 * - World particles are SERVER-authoritative and must be spawned once.
 * - Client NEVER spawns world particles.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftParticleHelper {

    private JolCraftParticleHelper() {}

    public static final double DEFAULT_RADIUS = 32.0D;

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

    public static void spawnLocal(Level level,
                                  ParticleOptions particle,
                                  boolean overrideLimiter,
                                  boolean alwaysShow,
                                  double x, double y, double z,
                                  double vx, double vy, double vz) {

        if (!level.isClientSide) return;
        level.addParticle(particle, overrideLimiter, alwaysShow, x, y, z, vx, vy, vz);
    }
}