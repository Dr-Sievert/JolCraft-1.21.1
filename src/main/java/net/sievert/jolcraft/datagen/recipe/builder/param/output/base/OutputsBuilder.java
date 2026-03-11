package net.sievert.jolcraft.datagen.recipe.builder.param.output.base;

import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pools;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolsBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class OutputsBuilder implements ParamBuilder<Outputs> {

    private @Nullable Pools pools;

    private OutputsBuilder() {}

    public static @NotNull OutputsBuilder create() {
        return new OutputsBuilder();
    }

    public @NotNull OutputsBuilder pools(@Nullable Pools pools) {
        this.pools = pools;
        return this;
    }

    public @NotNull OutputsBuilder pools(@Nullable PoolsBuilder builder) {
        this.pools = builder != null ? builder.build() : null;
        return this;
    }

    public @NotNull OutputsBuilder pools(@Nullable List<Pool> pools) {
        this.pools = pools != null ? new Pools(pools) : null;
        return this;
    }

    public @NotNull OutputsBuilder pool(@Nullable Pool pool) {
        if (pool == null) {
            return this;
        }

        List<Pool> next = new ArrayList<>();
        if (this.pools != null) {
            for (Pool existing : this.pools.pools()) {
                if (existing != null) {
                    next.add(existing);
                }
            }
        }

        next.add(pool);
        this.pools = new Pools(next);
        return this;
    }

    public @NotNull OutputsBuilder pool(@Nullable PoolBuilder builder) {
        return pool(builder != null ? builder.build() : null);
    }

    public @NotNull OutputsBuilder wrapSingle(@Nullable OutputParam out) {
        if (out == null) {
            return this;
        }

        this.pools = Outputs.wrapSingle(out).pools();
        return this;
    }

    @Override
    public @NotNull Outputs build() {
        return new Outputs(pools != null ? pools : Outputs.EMPTY_POOLS);
    }
}