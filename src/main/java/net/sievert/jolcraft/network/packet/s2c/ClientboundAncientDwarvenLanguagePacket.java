package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.network.JolCraftNetworkIds;
import org.jetbrains.annotations.NotNull;

public record ClientboundAncientDwarvenLanguagePacket(boolean knowsLanguage) implements CustomPacketPayload {
    public static final Type<ClientboundAncientDwarvenLanguagePacket> TYPE = new Type<>(JolCraft.location(JolCraftNetworkIds.SYNC_ANCIENT_DWARVEN_LANGUAGE));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAncientDwarvenLanguagePacket> CODEC =
            CustomPacketPayload.codec(ClientboundAncientDwarvenLanguagePacket::write, ClientboundAncientDwarvenLanguagePacket::read);

    public static ClientboundAncientDwarvenLanguagePacket read(FriendlyByteBuf buf) {
        return new ClientboundAncientDwarvenLanguagePacket(buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(knowsLanguage);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
