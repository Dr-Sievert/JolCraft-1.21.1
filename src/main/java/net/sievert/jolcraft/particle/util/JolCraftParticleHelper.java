package net.sievert.jolcraft.particle.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundParticlePacket;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Particle spawning helper that is safe to call from common code.
 * - On the physical client: spawns particles locally via Level#addParticle.
 * - On the server: sends a clientbound particle packet to nearby players (keeps exact velocity).
 *
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftParticleHelper {

    private JolCraftParticleHelper() {}

    // Reasonable default for "nearby effects" (tweak per call if needed)
    public static final double DEFAULT_RADIUS = 32.0D;

    public static void spawn(Level level, ParticleOptions particle,
                             double x, double y, double z,
                             double vx, double vy, double vz) {
        spawn(level, DEFAULT_RADIUS, particle, false, false, x, y, z, vx, vy, vz);
    }

    public static void spawn(Level level, double radius, ParticleOptions particle,
                             double x, double y, double z,
                             double vx, double vy, double vz) {
        spawn(level, radius, particle, false, false, x, y, z, vx, vy, vz);
    }

    public static void spawn(Level level, double radius, ParticleOptions particle,
                             boolean overrideLimiter, boolean alwaysShow,
                             double x, double y, double z,
                             double vx, double vy, double vz) {

        if (level.isClientSide) {
            level.addParticle(particle, overrideLimiter, alwaysShow, x, y, z, vx, vy, vz);
            return;
        }

        if (level instanceof ServerLevel) {
            BlockPos pos = BlockPos.containing(x, y, z);
            JolCraftNetworking.sendToNearbyClients(
                    level,
                    pos,
                    radius,
                    new ClientboundParticlePacket(particle, overrideLimiter, alwaysShow, x, y, z, vx, vy, vz)
            );
        }
    }
}
