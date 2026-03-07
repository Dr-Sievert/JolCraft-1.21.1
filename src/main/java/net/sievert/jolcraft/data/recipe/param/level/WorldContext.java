package net.sievert.jolcraft.data.recipe.param.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WorldContext(
        @NotNull ServerLevel level,
        @NotNull Player player,
        @Nullable Entity entity
) {
    public @NotNull RandomSource random() {
        return level.getRandom();
    }
}