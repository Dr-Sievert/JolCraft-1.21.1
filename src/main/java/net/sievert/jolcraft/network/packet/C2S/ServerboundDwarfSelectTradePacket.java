package net.sievert.jolcraft.network.packet.C2S;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

public class ServerboundDwarfSelectTradePacket implements CustomPacketPayload {
    public static final Type<ServerboundDwarfSelectTradePacket> TYPE =
            new Type<>(JolCraft.location("select_dwarf_trade"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundDwarfSelectTradePacket> CODEC =
            CustomPacketPayload.codec(ServerboundDwarfSelectTradePacket::write, ServerboundDwarfSelectTradePacket::read);

    private final int item;

    public ServerboundDwarfSelectTradePacket(int item) {
        this.item = item;
    }

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

    public int getItem() {
        return item;
    }
}
