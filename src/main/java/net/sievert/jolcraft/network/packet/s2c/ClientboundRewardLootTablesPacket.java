package net.sievert.jolcraft.network.packet.s2c;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.network.JolCraftNetworkIds;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public record ClientboundRewardLootTablesPacket(
        @NotNull Map<ResourceKey<LootTable>, LootTable> tables
) implements CustomPacketPayload {

    private static final int MAX_TABLES = 2048;

    private static final StreamCodec<RegistryFriendlyByteBuf, LootTable> LOOT_TABLE_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(
                    LootTable.DIRECT_CODEC
            );

    public static final Type<ClientboundRewardLootTablesPacket> TYPE =
            new Type<>(
                    JolCraft.location(
                            JolCraftNetworkIds.REWARD_LOOT_TABLES
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRewardLootTablesPacket> CODEC =
            StreamCodec.of(
                    ClientboundRewardLootTablesPacket::write,
                    ClientboundRewardLootTablesPacket::read
            );

    public ClientboundRewardLootTablesPacket {
        tables = Map.copyOf(tables);

        if (tables.size() > MAX_TABLES) {
            throw new IllegalArgumentException(
                    "reward loot-table payload exceeds "
                            + MAX_TABLES
                            + " entries"
            );
        }
    }

    private static @NotNull ClientboundRewardLootTablesPacket read(
            @NotNull RegistryFriendlyByteBuf buffer
    ) {
        int size = buffer.readVarInt();

        if (size < 0 || size > MAX_TABLES) {
            throw new IllegalArgumentException(
                    "invalid reward loot-table payload size: "
                            + size
            );
        }

        Map<ResourceKey<LootTable>, LootTable> tables =
                new LinkedHashMap<>(size);

        for (int index = 0; index < size; index++) {
            ResourceKey<LootTable> key =
                    ResourceKey.create(
                            Registries.LOOT_TABLE,
                            buffer.readResourceLocation()
                    );

            tables.put(
                    key,
                    LOOT_TABLE_CODEC.decode(buffer)
            );
        }

        return new ClientboundRewardLootTablesPacket(tables);
    }

    private static void write(
            @NotNull RegistryFriendlyByteBuf buffer,
            @NotNull ClientboundRewardLootTablesPacket packet
    ) {
        buffer.writeVarInt(packet.tables().size());

        for (Map.Entry<ResourceKey<LootTable>, LootTable> entry :
                packet.tables().entrySet()) {
            buffer.writeResourceLocation(
                    entry.getKey().location()
            );

            LOOT_TABLE_CODEC.encode(
                    buffer,
                    entry.getValue()
            );
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
