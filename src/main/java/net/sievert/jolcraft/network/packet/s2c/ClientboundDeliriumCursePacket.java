package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.network.JolCraftNetworkIds;
import org.jetbrains.annotations.NotNull;

/**
 * Sent from server to client to trigger Delirium hallucination/muffle.
 */
public record ClientboundDeliriumCursePacket(int durationTicks) implements CustomPacketPayload {
    public static final Type<ClientboundDeliriumCursePacket> TYPE =
            new Type<>(JolCraft.location(JolCraftNetworkIds.DELIRIUM_CURSE));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDeliriumCursePacket> CODEC =
            CustomPacketPayload.codec(ClientboundDeliriumCursePacket::write, ClientboundDeliriumCursePacket::read);

    public static ClientboundDeliriumCursePacket read(FriendlyByteBuf buf) {
        return new ClientboundDeliriumCursePacket(buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(durationTicks);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
