package net.sievert.jolcraft.param.runtime;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Runtime context for param evaluation.
 *
 * Contract:
 * - At least one of player or entity must be present
 * - Level and random are derived from the available source
 *
 * This represents an actor-driven context (not a raw world context).
 */
public record WorldContext(
        @Nullable Player player,
        @Nullable Entity entity
) {
    public WorldContext {
        if (player == null && entity == null) {
            throw new IllegalArgumentException("WorldContext requires player or entity");
        }
    }

    /**
     * Resolves the server level from entity or player (entity preferred).
     */
    public ServerLevel level() {
        if (entity != null && entity.level() instanceof ServerLevel level) return level;
        if (player != null && player.level() instanceof ServerLevel level) return level;
        throw new IllegalStateException("WorldContext has no level source");
    }

    /**
     * Resolves the random source from entity or player (entity preferred).
     */
    public RandomSource random() {
        if (entity != null) return entity.getRandom();
        if (player != null) return player.getRandom();
        throw new IllegalStateException("WorldContext has no random source");
    }
}