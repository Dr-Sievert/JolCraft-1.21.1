package net.sievert.jolcraft.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

public record ServerboundPlayWorldSoundPacket(
        ResourceLocation soundId,
        double x, double y, double z,
        SoundSource source,
        float volume,
        float pitch
) implements CustomPacketPayload {

    public static final Type<ServerboundPlayWorldSoundPacket> TYPE =
            new Type<>(JolCraft.location("play_world_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPlayWorldSoundPacket> CODEC =
            CustomPacketPayload.codec(ServerboundPlayWorldSoundPacket::write, ServerboundPlayWorldSoundPacket::read);

    public static ServerboundPlayWorldSoundPacket read(FriendlyByteBuf buf) {
        return new ServerboundPlayWorldSoundPacket(
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