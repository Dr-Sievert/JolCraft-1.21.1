package net.sievert.jolcraft.network.packet.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record ClientboundTomeUnlocksPacket(Set<String> unlocks) implements CustomPacketPayload {
    public static final Type<ClientboundTomeUnlocksPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "sync_tome_unlocks"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundTomeUnlocksPacket> CODEC =
            CustomPacketPayload.codec(ClientboundTomeUnlocksPacket::write, ClientboundTomeUnlocksPacket::read);

    public static ClientboundTomeUnlocksPacket read(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Set<String> unlocks = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            unlocks.add(buf.readUtf());
        }
        return new ClientboundTomeUnlocksPacket(unlocks);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(unlocks.size());
        for (String unlock : unlocks) {
            buf.writeUtf(unlock);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
