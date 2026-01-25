package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

public record ClientboundParticlePacket(
        ParticleOptions particle,
        boolean overrideLimiter,
        boolean alwaysShow,
        double x, double y, double z,
        double vx, double vy, double vz
) implements CustomPacketPayload {

    public static final Type<ClientboundParticlePacket> TYPE =
            new Type<>(JolCraft.location("particle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundParticlePacket> CODEC =
            CustomPacketPayload.codec(ClientboundParticlePacket::write, ClientboundParticlePacket::read);

    public static ClientboundParticlePacket read(FriendlyByteBuf buf) {
        RegistryFriendlyByteBuf regBuf = (RegistryFriendlyByteBuf) buf;
        ParticleOptions particle = ParticleTypes.STREAM_CODEC.decode(regBuf);
        boolean overrideLimiter = buf.readBoolean();
        boolean alwaysShow = buf.readBoolean();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        double vx = buf.readDouble();
        double vy = buf.readDouble();
        double vz = buf.readDouble();
        return new ClientboundParticlePacket(particle, overrideLimiter, alwaysShow, x, y, z, vx, vy, vz);
    }

    public void write(FriendlyByteBuf buf) {
        RegistryFriendlyByteBuf regBuf = (RegistryFriendlyByteBuf) buf;
        ParticleTypes.STREAM_CODEC.encode(regBuf, this.particle);
        buf.writeBoolean(this.overrideLimiter);
        buf.writeBoolean(this.alwaysShow);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeDouble(this.vx);
        buf.writeDouble(this.vy);
        buf.writeDouble(this.vz);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
