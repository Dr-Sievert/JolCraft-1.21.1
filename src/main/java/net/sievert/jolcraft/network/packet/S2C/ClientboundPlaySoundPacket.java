package net.sievert.jolcraft.network.packet.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

public record ClientboundPlaySoundPacket(
        ResourceLocation soundId,
        double x, double y, double z,
        SoundSource source,
        float volume,
        float pitch
) implements CustomPacketPayload {
    public static final Type<ClientboundPlaySoundPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "play_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlaySoundPacket> CODEC =
            CustomPacketPayload.codec(ClientboundPlaySoundPacket::write, ClientboundPlaySoundPacket::read);

    public static ClientboundPlaySoundPacket read(FriendlyByteBuf buf) {
        return new ClientboundPlaySoundPacket(
                buf.readResourceLocation(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readEnum(SoundSource.class),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(soundId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeEnum(source);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
