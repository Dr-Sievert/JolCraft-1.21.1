package net.sievert.jolcraft.network.packet.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

/**
 * Sent from server to client(s) when a specific dwarf entity
 * should play the endorsement animation (tablet signing).
 */
public record ClientboundDwarfEndorseAnimationPacket(int entityId) implements CustomPacketPayload {
    public static final Type<ClientboundDwarfEndorseAnimationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "endorse_anim"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDwarfEndorseAnimationPacket> CODEC =
            CustomPacketPayload.codec(ClientboundDwarfEndorseAnimationPacket::write, ClientboundDwarfEndorseAnimationPacket::read);

    public static ClientboundDwarfEndorseAnimationPacket read(FriendlyByteBuf buf) {
        return new ClientboundDwarfEndorseAnimationPacket(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
