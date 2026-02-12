package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.network.JolCraftNetworkIds;
import org.jetbrains.annotations.NotNull;

public record ClientboundDwarvenReputationPacket(int tier) implements CustomPacketPayload {
    public static final Type<ClientboundDwarvenReputationPacket> TYPE =
            new Type<>(JolCraft.location(JolCraftNetworkIds.SYNC_DWARVEN_REPUTATION));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDwarvenReputationPacket> CODEC =
            CustomPacketPayload.codec(ClientboundDwarvenReputationPacket::write, ClientboundDwarvenReputationPacket::read);

    public static ClientboundDwarvenReputationPacket read(FriendlyByteBuf buf) {
        return new ClientboundDwarvenReputationPacket(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(tier);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
