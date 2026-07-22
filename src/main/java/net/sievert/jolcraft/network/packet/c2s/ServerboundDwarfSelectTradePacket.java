package net.sievert.jolcraft.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.network.JolCraftNetworkIds;
import org.jetbrains.annotations.NotNull;

public record ServerboundDwarfSelectTradePacket(int item) implements CustomPacketPayload {
    public static final Type<ServerboundDwarfSelectTradePacket> TYPE = new Type<>(JolCraft.location(JolCraftNetworkIds.DWARF_SELECT_TRADE));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundDwarfSelectTradePacket> CODEC =
            CustomPacketPayload.codec(ServerboundDwarfSelectTradePacket::write, ServerboundDwarfSelectTradePacket::read);

    public static ServerboundDwarfSelectTradePacket read(RegistryFriendlyByteBuf buf) {
        int item = buf.readVarInt();
        return new ServerboundDwarfSelectTradePacket(item);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.item);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}