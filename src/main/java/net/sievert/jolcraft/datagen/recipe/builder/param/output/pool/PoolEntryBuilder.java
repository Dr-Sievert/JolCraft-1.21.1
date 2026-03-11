package net.sievert.jolcraft.datagen.recipe.builder.param.output.pool;

import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.pool.PoolEntry;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.data.recipe.param.quantity.DrawRule;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.quantity.DrawRuleBuilder;

public final class PoolEntryBuilder implements ParamBuilder<PoolEntry> {

    private OutputParam output;
    private DrawRule pool;
    private WeightParam weight;

    private PoolEntryBuilder() {}

    public static PoolEntryBuilder create() {
        return new PoolEntryBuilder();
    }

    public PoolEntryBuilder output(OutputParam output) {
        this.output = OutputParam.unwrap(output);
        return this;
    }

    public PoolEntryBuilder output(ParamBuilder<? extends OutputParam> builder) {
        this.output = builder != null ? OutputParam.unwrap(builder.build()) : null;
        return this;
    }

    public PoolEntryBuilder pool(DrawRule pool) {
        this.pool = pool;
        return this;
    }

    public PoolEntryBuilder pool(DrawRuleBuilder builder) {
        this.pool = builder != null ? builder.build() : null;
        return this;
    }

    public PoolEntryBuilder weight(WeightParam weight) {
        this.weight = weight;
        return this;
    }

    public PoolEntryBuilder weight(int weight) {
        this.weight = new WeightParam(weight);
        return this;
    }

    @Override
    public PoolEntry build() {
        return new PoolEntry(output, pool, weight);
    }
}
