package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.network.JolCraftNetworkIds;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

/**
 * Sent from server to client to sync the player's full set of profession endorsements.
 */
public record ClientboundDwarvenEndorsementsPacket(Set<DwarfProfession> endorsements) implements CustomPacketPayload {

    public static final Type<ClientboundDwarvenEndorsementsPacket> TYPE =
            new Type<>(JolCraft.location(JolCraftNetworkIds.SYNC_DWARVEN_ENDORSEMENTS));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDwarvenEndorsementsPacket> CODEC =
            CustomPacketPayload.codec(ClientboundDwarvenEndorsementsPacket::write, ClientboundDwarvenEndorsementsPacket::read);

    public static ClientboundDwarvenEndorsementsPacket read(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Set<DwarfProfession> endorsements = EnumSet.noneOf(DwarfProfession.class);
        for (int i = 0; i < size; i++) {
            String id = buf.readUtf();
            DwarfProfession prof = DwarfProfession.byId(id);
            if (prof != DwarfProfession.NONE) {
                endorsements.add(prof);
            }
        }
        return new ClientboundDwarvenEndorsementsPacket(endorsements);
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
