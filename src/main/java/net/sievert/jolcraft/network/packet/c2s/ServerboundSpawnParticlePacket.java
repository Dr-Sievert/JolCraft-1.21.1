package net.sievert.jolcraft.network.packet.c2s;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.network.JolCraftNetworkIds;
import org.jetbrains.annotations.NotNull;

public record ServerboundSpawnParticlePacket(
        ParticleOptions particle,
        boolean overrideLimiter,
        boolean alwaysShow,
        double x, double y, double z,
        int count,
        double xDist, double yDist, double zDist,
        double speed
) implements CustomPacketPayload {

    public static final Type<ServerboundSpawnParticlePacket> TYPE =
            new Type<>(JolCraft.location(JolCraftNetworkIds.SPAWN_PARTICLE));

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

        int count = buf.readVarInt();

        double xDist = buf.readDouble();
        double yDist = buf.readDouble();
        double zDist = buf.readDouble();

        double speed = buf.readDouble();

        return new ServerboundSpawnParticlePacket(
                particle,
                overrideLimiter,
                alwaysShow,
                x, y, z,
                count,
                xDist, yDist, zDist,
                speed
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

        buf.writeVarInt(this.count);

        buf.writeDouble(this.xDist);
        buf.writeDouble(this.yDist);
        buf.writeDouble(this.zDist);

        buf.writeDouble(this.speed);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}