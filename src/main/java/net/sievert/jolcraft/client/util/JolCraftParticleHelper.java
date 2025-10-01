package net.sievert.jolcraft.client.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundParticlePacket;

public class JolCraftParticleHelper {

    /**
     * Send a particle packet to all nearby players.
     */
    public static void sendParticle(Level level, BlockPos pos, ParticleOptions particle,
                                    boolean overrideLimiter, boolean alwaysShow,
                                    double x, double y, double z,
                                    double vx, double vy, double vz,
                                    double radius) {
        if(level.isClientSide) return;
        ClientboundParticlePacket packet = new ClientboundParticlePacket(
                particle, overrideLimiter, alwaysShow, x, y, z, vx, vy, vz
        );

        JolCraftNetworking.sendToNearbyClients(level, pos, radius, packet);
    }

    /**
     * Overload with defaults (no velocity, not forced).
     */
    public static void sendParticle(Level level, BlockPos pos, ParticleOptions particle,
                                    double x, double y, double z, double radius) {
        sendParticle(level, pos, particle, false, false, x, y, z, 0.0, 0.0, 0.0, radius);
    }

    /**
     * Overload with defaults, using BlockPos as coordinates.
     */
    public static void sendParticle(Level level, BlockPos pos, ParticleOptions particle, double radius) {
        sendParticle(level, pos, particle, false, false,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                0.0, 0.0, 0.0, radius);
    }
}
