package net.sievert.jolcraft.datagen.recipe.builder.param.output.base;

import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.world.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.world.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.world.recipe.param.output.pool.PoolEntry;
import net.sievert.jolcraft.world.recipe.param.output.pool.Pools;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.param.custom.quantity.WeightParam;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolsBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class OutputsBuilder implements ParamBuilder<Outputs> {

    private @Nullable Conditions conditions;
    private @Nullable Pools pools;

    private OutputsBuilder() {}

    public static @NotNull OutputsBuilder create() {
        return new OutputsBuilder();
    }

    public @NotNull OutputsBuilder conditions(@Nullable Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public @NotNull OutputsBuilder conditions(@Nullable ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
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
        return pool(new Pool(IntRange.ONE, Conditions.EMPTY,
                List.of(new PoolEntry(out, Conditions.EMPTY, IntRange.ONE, WeightParam.ONE))));
    }

    @Override
    public @NotNull Outputs build() {
        return new Outputs(
                conditions != null ? conditions : Conditions.EMPTY,
                pools != null ? pools : Outputs.EMPTY_POOLS
        );
    }
}