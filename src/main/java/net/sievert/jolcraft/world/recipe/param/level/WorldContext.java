package net.sievert.jolcraft.world.recipe.param.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WorldContext(
        @NotNull ServerLevel level,
        @Nullable Player player,
        @Nullable Entity entity,
        @NotNull RandomSource random
) {
    public WorldContext(
            @NotNull ServerLevel level,
            @Nullable Player player,
            @Nullable Entity entity
    ) {
        this(level, player, entity, RandomSource.create());
    }
}