package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

/**
 * Sent from server to client to sync the player's full set of profession endorsements.
 * Now uses DwarfProfession enum for full type safety.
 */
public record ClientboundEndorsementsPacket(Set<DwarfProfession> endorsements) implements CustomPacketPayload {

    public static final Type<ClientboundEndorsementsPacket> TYPE =
            new Type<>(JolCraft.location("endorsement_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundEndorsementsPacket> CODEC =
            CustomPacketPayload.codec(ClientboundEndorsementsPacket::write, ClientboundEndorsementsPacket::read);

    public static ClientboundEndorsementsPacket read(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Set<DwarfProfession> endorsements = EnumSet.noneOf(DwarfProfession.class);
        for (int i = 0; i < size; i++) {
            String id = buf.readUtf();
            DwarfProfession prof = DwarfProfession.byId(id);
            if (prof != DwarfProfession.NONE) {
                endorsements.add(prof);
            }
        }
        return new ClientboundEndorsementsPacket(endorsements);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(endorsements.size());
        for (DwarfProfession prof : endorsements) {
            buf.writeUtf(prof.getId());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
