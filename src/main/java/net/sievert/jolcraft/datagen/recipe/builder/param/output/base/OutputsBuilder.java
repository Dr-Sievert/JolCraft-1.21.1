package net.sievert.jolcraft.datagen.recipe.builder.param.output.base;

import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pools;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolsBuilder;

import java.util.ArrayList;
import java.util.List;

public final class OutputsBuilder implements ParamBuilder<Outputs> {

    private Pools pools;

    private OutputsBuilder() {}

    public static OutputsBuilder create() {
        return new OutputsBuilder();
    }

    public OutputsBuilder pools(Pools pools) {
        this.pools = pools;
        return this;
    }

    public OutputsBuilder pools(PoolsBuilder builder) {
        this.pools = builder != null ? builder.build() : null;
        return this;
    }

    public OutputsBuilder pools(List<Pool> pools) {
        this.pools = new Pools(pools);
        return this;
    }

    public OutputsBuilder pool(Pool pool) {
        if (pool == null) return this;

        Pools current = this.pools;
        if (current == null) {
            this.pools = new Pools(List.of(pool));
            return this;
        }

        List<Pool> list = current.pools();
        if (list.isEmpty()) {
            this.pools = new Pools(List.of(pool));
            return this;
        }

        ArrayList<Pool> next = new ArrayList<>(list.size() + 1);
        for (Pool p : list) {
            if (p != null) next.add(p);
        }
        next.add(pool);
        this.pools = new Pools(next);
        return this;
    }

    public OutputsBuilder pool(PoolBuilder builder) {
        return pool(builder != null ? builder.build() : null);
    }

    public OutputsBuilder wrapSingle(OutputParam out) {
        if (out == null) return this;
        this.pools = Outputs.wrapSingle(OutputParam.unwrap(out)).pools();
        return this;
    }

    @Override
    public Outputs build() {
        Pools ps = pools != null ? pools : Outputs.EMPTY_POOLS;
        return new Outputs(ps);
    }
}