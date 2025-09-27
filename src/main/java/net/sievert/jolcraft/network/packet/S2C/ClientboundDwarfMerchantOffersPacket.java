package net.sievert.jolcraft.network.packet.S2C;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffers;
import org.jetbrains.annotations.NotNull;

public record ClientboundDwarfMerchantOffersPacket(
        int containerId,
        DwarfMerchantOffers offers,
        int dwarfLevel,
        int dwarfXp,
        boolean showProgress,
        boolean showLevel,
        boolean canRestock
) implements CustomPacketPayload {
    public static final Type<ClientboundDwarfMerchantOffersPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_merchant_offers"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDwarfMerchantOffersPacket> CODEC =
            CustomPacketPayload.codec(ClientboundDwarfMerchantOffersPacket::write, ClientboundDwarfMerchantOffersPacket::read);

    public static ClientboundDwarfMerchantOffersPacket read(RegistryFriendlyByteBuf buf) {
        int containerId = buf.readVarInt();
        DwarfMerchantOffers offers = DwarfMerchantOffers.STREAM_CODEC.decode(buf);
        int dwarfLevel = buf.readVarInt();
        int dwarfXp = buf.readVarInt();
        boolean showProgress = buf.readBoolean();
        boolean showLevel = buf.readBoolean();
        boolean canRestock = buf.readBoolean();
        return new ClientboundDwarfMerchantOffersPacket(containerId, offers, dwarfLevel, dwarfXp, showProgress, showLevel, canRestock);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        DwarfMerchantOffers.STREAM_CODEC.encode(buf, offers);
        buf.writeVarInt(dwarfLevel);
        buf.writeVarInt(dwarfXp);
        buf.writeBoolean(showProgress);
        buf.writeBoolean(showLevel);
        buf.writeBoolean(canRestock);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
