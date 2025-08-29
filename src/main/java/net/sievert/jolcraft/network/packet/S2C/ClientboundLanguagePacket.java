package net.sievert.jolcraft.network.packet.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

public record ClientboundLanguagePacket(boolean knowsLanguage) implements CustomPacketPayload {
    public static final Type<ClientboundLanguagePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "sync_language"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLanguagePacket> CODEC =
            CustomPacketPayload.codec(ClientboundLanguagePacket::write, ClientboundLanguagePacket::read);

    public static ClientboundLanguagePacket read(FriendlyByteBuf buf) {
        return new ClientboundLanguagePacket(buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(knowsLanguage);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
