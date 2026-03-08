package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpawnConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen builder for {@link EntitySpawnConfig}.
 *
 * - no throwing
 * - fail-closed
 * - minimal surface
 */
public final class EntitySpawnConfigBuilder {

    private BlockPos pos = BlockPos.ZERO;
    private int offsetX = 0;
    private int offsetY = 0;
    private int offsetZ = 0;
    private int radius = 0;
    private boolean forced = false;
    private boolean persistent = false;
    private boolean noAi = false;

    private EntitySpawnConfigBuilder() {}

    public static @NotNull EntitySpawnConfigBuilder builder() {
        return new EntitySpawnConfigBuilder();
    }

    // ---------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------

    public @NotNull EntitySpawnConfigBuilder pos(@NotNull BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder pos(int x, int y, int z) {
        this.pos = new BlockPos(x, y, z);
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder offset(int x, int y, int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder offsetX(int x) {
        this.offsetX = x;
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder offsetY(int y) {
        this.offsetY = y;
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder offsetZ(int z) {
        this.offsetZ = z;
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder radius(int radius) {
        this.radius = radius;
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder forced(boolean forced) {
        this.forced = forced;
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder persistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    public @NotNull EntitySpawnConfigBuilder noAi(boolean noAi) {
        this.noAi = noAi;
        return this;
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    public @NotNull DataResult<EntitySpawnConfig> build() {
        return new EntitySpawnConfig(
                pos,
                offsetX,
                offsetY,
                offsetZ,
                radius,
                forced,
                persistent,
                noAi
        ).validate();
    }

    public @Nullable EntitySpawnConfig buildOrNull() {
        return build().result().orElse(null);
    }
}