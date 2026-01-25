package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public record ClientboundLoreUnlocksPacket(Set<String> unlocks) implements CustomPacketPayload {
    public static final Type<ClientboundLoreUnlocksPacket> TYPE =
            new Type<>(JolCraft.location("sync_tome_unlocks"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundLoreUnlocksPacket> CODEC =
            CustomPacketPayload.codec(ClientboundLoreUnlocksPacket::write, ClientboundLoreUnlocksPacket::read);

    public static <K extends Enum<K>> ClientboundLoreUnlocksPacket fromEnumSet(Set<K> enumUnlocks) {
        Set<String> keys = enumUnlocks.stream()
                .map(k -> k.name().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        return new ClientboundLoreUnlocksPacket(keys);
    }

    public static ClientboundLoreUnlocksPacket read(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Set<String> unlocks = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            unlocks.add(buf.readUtf());
        }
        return new ClientboundLoreUnlocksPacket(unlocks);
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
