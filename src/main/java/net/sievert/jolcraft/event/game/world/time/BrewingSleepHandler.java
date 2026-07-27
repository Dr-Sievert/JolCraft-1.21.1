package net.sievert.jolcraft.event.game.world.time;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingBarrelBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;

import java.util.HashSet;
import java.util.Set;

public final class BrewingSleepHandler {

    private static final int CHUNK_SEARCH_RADIUS = 4;

    private BrewingSleepHandler() {}

    public static void handleSleepFinished(
            ServerLevel level,
            long newTime
    ) {
        long skippedTicks =
                newTime - level.getDayTime();

        if (skippedTicks <= 0L) {
            return;
        }

        Set<Long> visitedChunks = new HashSet<>();
        int[] advancedCounts = new int[2];

        for (ServerPlayer player : level.players()) {
            processChunksAroundPlayer(
                    level,
                    player,
                    skippedTicks,
                    visitedChunks,
                    advancedCounts
            );
        }

        JolCraftLogs.debug(
                JolCraftLogTags.BLOCK_ENTITY,
                "Sleep advanced time by {} ticks for {} brewing fermenting cauldron(s) and {} aging fermenting barrel(s)",
                skippedTicks,
                advancedCounts[0],
                advancedCounts[1]
        );
    }

    private static void processChunksAroundPlayer(
            ServerLevel level,
            ServerPlayer player,
            long skippedTicks,
            Set<Long> visitedChunks,
            int[] advancedCounts
    ) {
        int centerChunkX = SectionPos.blockToSectionCoord(
                player.blockPosition().getX()
        );

        int centerChunkZ = SectionPos.blockToSectionCoord(
                player.blockPosition().getZ()
        );

        for (
                int offsetX = -CHUNK_SEARCH_RADIUS;
                offsetX <= CHUNK_SEARCH_RADIUS;
                offsetX++
        ) {
            for (
                    int offsetZ = -CHUNK_SEARCH_RADIUS;
                    offsetZ <= CHUNK_SEARCH_RADIUS;
                    offsetZ++
            ) {
                int chunkX = centerChunkX + offsetX;
                int chunkZ = centerChunkZ + offsetZ;

                long chunkKey = ChunkPos.asLong(
                        chunkX,
                        chunkZ
                );

                if (!visitedChunks.add(chunkKey)) {
                    continue;
                }

                processChunk(
                        level,
                        chunkX,
                        chunkZ,
                        skippedTicks,
                        advancedCounts
                );
            }
        }
    }

    private static void processChunk(
            ServerLevel level,
            int chunkX,
            int chunkZ,
            long skippedTicks,
            int[] advancedCounts
    ) {
        var chunk = level.getChunk(
                chunkX,
                chunkZ,
                ChunkStatus.FULL,
                false
        );

        if (!(chunk instanceof LevelChunk levelChunk)) {
            return;
        }

        for (
                BlockEntity blockEntity :
                levelChunk.getBlockEntities().values()
        ) {
            processBlockEntity(
                    blockEntity,
                    skippedTicks,
                    advancedCounts
            );
        }
    }

    private static void processBlockEntity(
            BlockEntity blockEntity,
            long skippedTicks,
            int[] advancedCounts
    ) {
        if (blockEntity instanceof
                FermentingBarrelBlockEntity barrel) {
            if (!barrel.hasBrew()) {
                return;
            }

            barrel.fastForwardAge(
                    skippedTicks
            );

            advancedCounts[1]++;
            return;
        }

        if (!(blockEntity instanceof
                FermentingCauldronBlockEntity cauldron)
                || !cauldron.isBrewing()) {
            return;
        }

        cauldron.fastForwardBrew(
                skippedTicks
        );

        advancedCounts[0]++;
    }
}