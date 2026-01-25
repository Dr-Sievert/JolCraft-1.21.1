package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

/**
 * Sent from server to client to trigger Delirium hallucination/muffle.
 */
public record ClientboundDeliriumPacket(int durationTicks) implements CustomPacketPayload {
    public static final Type<ClientboundDeliriumPacket> TYPE =
            new Type<>(JolCraft.location("delirium"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDeliriumPacket> CODEC =
            CustomPacketPayload.codec(ClientboundDeliriumPacket::write, ClientboundDeliriumPacket::read);

    public static ClientboundDeliriumPacket read(FriendlyByteBuf buf) {
        return new ClientboundDeliriumPacket(buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(durationTicks);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
