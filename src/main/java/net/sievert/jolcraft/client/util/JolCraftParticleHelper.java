package net.sievert.jolcraft.client.util;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundParticlePacket;

public class JolCraftParticleHelper {

    /**
     * Send a particle either directly (client side)
     * or via packet to nearby clients (server side).
     */
    public static void sendParticle(
            Level level,
            BlockPos pos,
            ParticleOptions particle,
            boolean overrideLimiter,
            boolean alwaysShow,
            double x, double y, double z,
            double vx, double vy, double vz,
            double radius
    ) {
        if (level.isClientSide) {
            if (level instanceof ClientLevel clientLevel) {
                clientLevel.addParticle(
                        particle,
                        overrideLimiter,
                        alwaysShow,
                        x, y, z,
                        vx, vy, vz
                );
            }
            return;
        }
        ClientboundParticlePacket packet = new ClientboundParticlePacket(
                particle, overrideLimiter, alwaysShow, x, y, z, vx, vy, vz
        );
        JolCraftNetworking.sendToNearbyClients(level, pos, radius, packet);
    }
}
