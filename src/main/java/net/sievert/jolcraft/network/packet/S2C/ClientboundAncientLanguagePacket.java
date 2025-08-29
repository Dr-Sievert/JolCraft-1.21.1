package net.sievert.jolcraft.network.packet.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.network.client.data.ClientAncientLanguageData;
import org.jetbrains.annotations.NotNull;

public record ClientboundAncientLanguagePacket(boolean knowsLanguage) implements CustomPacketPayload {
    public static final Type<ClientboundAncientLanguagePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "sync_ancient_language"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAncientLanguagePacket> CODEC =
            CustomPacketPayload.codec(ClientboundAncientLanguagePacket::write, ClientboundAncientLanguagePacket::read);

    public static ClientboundAncientLanguagePacket read(FriendlyByteBuf buf) {
        return new ClientboundAncientLanguagePacket(buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(knowsLanguage);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // This is your client-side handler
    public void handle() {
        ClientAncientLanguageData.setKnows(knowsLanguage);
    }
}
