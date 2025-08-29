package net.sievert.jolcraft.network.packet.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

public record ClientboundReputationPacket(int tier) implements CustomPacketPayload {
    public static final Type<ClientboundReputationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "sync_reputation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundReputationPacket> CODEC =
            CustomPacketPayload.codec(ClientboundReputationPacket::write, ClientboundReputationPacket::read);

    public static ClientboundReputationPacket read(FriendlyByteBuf buf) {
        return new ClientboundReputationPacket(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(tier);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
