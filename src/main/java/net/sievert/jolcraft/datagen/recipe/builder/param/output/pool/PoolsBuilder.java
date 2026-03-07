package net.sievert.jolcraft.datagen.recipe.builder.param.output.pool;

import net.sievert.jolcraft.data.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pools;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;

public final class PoolsBuilder implements ParamBuilder<Pools> {

    private final ArrayList<Pool> pools = new ArrayList<>();

    private PoolsBuilder() {}

    public static PoolsBuilder create() {
        return new PoolsBuilder();
    }

    public PoolsBuilder pool(Pool pool) {
        if (pool != null) pools.add(pool);
        return this;
    }

    public PoolsBuilder pool(PoolBuilder builder) {
        Pool p = builder != null ? builder.build() : null;
        if (p != null) pools.add(p);
        return this;
    }

    public PoolsBuilder pools(List<Pool> list) {
        if (list == null || list.isEmpty()) return this;
        for (Pool p : list) if (p != null) pools.add(p);
        return this;
    }

    @Override
    public Pools build() {
        return new Pools(pools.isEmpty() ? List.of() : List.copyOf(pools));
    }
}