package net.sievert.jolcraft.world.data.custom;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.data.util.AbstractSavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Stores custom stat awards for players who are offline when the stat is earned.
 * Pending awards are accumulated per player and stat, then granted and removed
 * when the player next joins the server.
 */
public final class PendingStatData extends AbstractSavedData {

    private static final String DATA_NAME = JolCraftStrings.underscored(
            JolCraft.MOD_ID,
            JolCraftDictionary.PENDING,
            JolCraftStrings.plural(JolCraftDictionary.STAT)
    );

    private static final String NBT_PLAYER = JolCraftDictionary.PLAYER;

    private static final String NBT_PLAYERS = JolCraftStrings.plural(NBT_PLAYER);

    private static final String NBT_STAT = JolCraftDictionary.STAT;

    private static final String NBT_STATS = JolCraftStrings.plural(NBT_STAT);

    private static final String NBT_AMOUNT = JolCraftDictionary.AMOUNT;

    private static final Type<PendingStatData> TYPE =
            new Type<>(
                    DATA_NAME,
                    PendingStatData::new,
                    PendingStatData::load
            );

    /**
     * Pending stat amounts grouped first by player UUID and then by stat ID.
     */
    private final Map<UUID, Map<ResourceLocation, Integer>> pendingStats = new HashMap<>();

    private PendingStatData() {}

    /**
     * Awards one point of the supplied stat immediately when the player is online,
     * or queues it for the player's next login when they are offline.
     */
    public static void awardOrQueue(
            ServerLevel level,
            UUID playerId,
            ResourceLocation stat
    ) {
        awardOrQueue(
                level,
                playerId,
                stat,
                1
        );
    }

    /**
     * Awards the supplied stat amount immediately when the player is online,
     * or accumulates it in persistent storage when they are offline.
     */
    public static void awardOrQueue(
            ServerLevel level,
            UUID playerId,
            ResourceLocation stat,
            int amount
    ) {
        if (amount <= 0) {
            return;
        }

        ServerPlayer player =
                level.getServer()
                        .getPlayerList()
                        .getPlayer(
                                playerId
                        );

        if (player != null) {
            player.awardStat(
                    stat,
                    amount
            );

            return;
        }

        PendingStatData data =
                get(
                        level
                );

        data.pendingStats
                .computeIfAbsent(
                        playerId,
                        ignored -> new HashMap<>()
                )
                .merge(
                        stat,
                        amount,
                        Integer::sum
                );

        data.setDirty();
    }

    /**
     * Awards and removes every pending stat stored for the supplied player.
     * This should be called when the player logs into the server.
     */
    public static void awardPending(
            ServerPlayer player
    ) {
        PendingStatData data = get(player.serverLevel());

        Map<ResourceLocation, Integer> playerStats =
                data.pendingStats.remove(
                        player.getUUID()
                );

        if (playerStats == null) {
            return;
        }

        data.setDirty();

        StringJoiner awardedStats = new StringJoiner(", ");

        for (
                Map.Entry<ResourceLocation, Integer> entry
                : playerStats.entrySet()
        ) {
            int amount = entry.getValue();

            if (amount <= 0) {
                continue;
            }

            player.awardStat(
                    entry.getKey(),
                    amount
            );

            awardedStats.add(
                    entry.getKey() + "=" + amount
            );
        }

        if (awardedStats.length() > 0) {
            JolCraftLogs.debug(
                    JolCraftLogTags.PLAYER,
                    "Pending stats awarded on login for player {}. Stats = [{}]",
                    player.getName().getString(),
                    player.getUUID(),
                    awardedStats
            );
        }
    }

    /**
     * Retrieves the server-wide pending stat data from the overworld data storage.
     */
    private static PendingStatData get(
            ServerLevel level
    ) {
        return TYPE.get(level.getServer().overworld());
    }

    /**
     * Loads all pending player and stat entries from persistent NBT.
     * Invalid stat IDs and non-positive amounts are ignored.
     */
    private static PendingStatData load(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        PendingStatData data = new PendingStatData();

        ListTag players =
                tag.getList(
                        NBT_PLAYERS,
                        Tag.TAG_COMPOUND
                );

        for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
            CompoundTag playerTag =
                    players.getCompound(
                            playerIndex
                    );

            if (!playerTag.hasUUID(
                    NBT_PLAYER
            )) {
                continue;
            }

            UUID playerId =
                    playerTag.getUUID(
                            NBT_PLAYER
                    );

            ListTag stats =
                    playerTag.getList(
                            NBT_STATS,
                            Tag.TAG_COMPOUND
                    );

            Map<ResourceLocation, Integer> playerStats =
                    new HashMap<>();

            for (int statIndex = 0; statIndex < stats.size(); statIndex++) {
                CompoundTag statTag =
                        stats.getCompound(
                                statIndex
                        );

                ResourceLocation stat =
                        ResourceLocation.tryParse(
                                statTag.getString(
                                        NBT_STAT
                                )
                        );

                int amount = statTag.getInt(NBT_AMOUNT);

                if (stat == null || amount <= 0) {
                    continue;
                }

                playerStats.merge(
                        stat,
                        amount,
                        Integer::sum
                );
            }

            if (!playerStats.isEmpty()) {
                data.pendingStats.put(
                        playerId,
                        playerStats
                );
            }
        }

        return data;
    }

    /**
     * Saves all non-empty pending player and stat entries to persistent NBT.
     */
    @Override
    protected void saveData(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        ListTag players =
                new ListTag();

        for (
                Map.Entry<UUID, Map<ResourceLocation, Integer>> playerEntry
                : pendingStats.entrySet()
        ) {
            ListTag stats = getTags(playerEntry);

            if (stats.isEmpty()) {
                continue;
            }

            CompoundTag playerTag =
                    new CompoundTag();

            playerTag.putUUID(
                    NBT_PLAYER,
                    playerEntry.getKey()
            );

            playerTag.put(
                    NBT_STATS,
                    stats
            );

            players.add(
                    playerTag
            );
        }

        if (!players.isEmpty()) {
            tag.put(
                    NBT_PLAYERS,
                    players
            );
        }
    }

    /**
     * Serializes the valid pending stat entries belonging to one player.
     */
    private static @NotNull ListTag getTags(
            Map.Entry<UUID, Map<ResourceLocation, Integer>> playerEntry
    ) {
        ListTag stats =
                new ListTag();

        for (
                Map.Entry<ResourceLocation, Integer> statEntry
                : playerEntry.getValue().entrySet()
        ) {
            int amount =
                    statEntry.getValue();

            if (amount <= 0) {
                continue;
            }

            CompoundTag statTag =
                    new CompoundTag();

            statTag.putString(
                    NBT_STAT,
                    statEntry.getKey().toString()
            );

            statTag.putInt(
                    NBT_AMOUNT,
                    amount
            );

            stats.add(
                    statTag
            );
        }

        return stats;
    }
}