package net.sievert.jolcraft.datagen.recipe.builder.param.output.pool;

import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.pool.PoolEntry;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;

public final class PoolEntryBuilder implements ParamBuilder<PoolEntry> {

    private OutputParam output;
    private Conditions conditions;
    private IntRange rolls;
    private WeightParam weight;

    private PoolEntryBuilder() {}

    public static PoolEntryBuilder create() {
        return new PoolEntryBuilder();
    }

    public PoolEntryBuilder output(OutputParam output) {
        this.output = output;
        return this;
    }

    public PoolEntryBuilder output(ParamBuilder<? extends OutputParam> builder) {
        this.output = builder != null ? builder.build() : null;
        return this;
    }

    public PoolEntryBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public PoolEntryBuilder conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    public PoolEntryBuilder rolls(IntRange rolls) {
        this.rolls = rolls;
        return this;
    }

    public PoolEntryBuilder rolls(int rolls) {
        this.rolls = IntRange.fixed(rolls);
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
        return new PoolEntry(
                output,
                conditions != null ? conditions : Conditions.EMPTY,
                rolls != null ? rolls : IntRange.ONE,
                weight != null ? weight : WeightParam.ONE
        );
    }
}