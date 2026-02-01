package net.sievert.jolcraft.network.packet.c2s;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

public record ServerboundSpawnParticlePacket(
        ParticleOptions particle,
        boolean overrideLimiter,
        boolean alwaysShow,
        double x, double y, double z,
        double vx, double vy, double vz
) implements CustomPacketPayload {

    public static final Type<ServerboundSpawnParticlePacket> TYPE = new Type<>(JolCraft.location("spawn_particle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSpawnParticlePacket> CODEC =
            CustomPacketPayload.codec(ServerboundSpawnParticlePacket::write, ServerboundSpawnParticlePacket::read);

    public static ServerboundSpawnParticlePacket read(FriendlyByteBuf buf) {
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

        return new ServerboundSpawnParticlePacket(
                particle,
                overrideLimiter,
                alwaysShow,
                x, y, z,
                vx, vy, vz
        );
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