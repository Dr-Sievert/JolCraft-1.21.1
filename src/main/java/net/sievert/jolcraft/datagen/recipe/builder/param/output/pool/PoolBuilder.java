package net.sievert.jolcraft.datagen.recipe.builder.param.output.pool;

import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.data.recipe.param.output.pool.PoolEntry;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;

import java.util.ArrayList;
import java.util.List;

public final class PoolBuilder implements ParamBuilder<Pool> {

    private IntRange rolls;
    private Conditions conditions;

    private final ArrayList<PoolEntry> entries = new ArrayList<>();

    private PoolBuilder() {}

    public static PoolBuilder create() {
        return new PoolBuilder();
    }

    public PoolBuilder rolls(IntRange rolls) {
        this.rolls = rolls;
        return this;
    }

    public PoolBuilder rollsFixed(int value) {
        this.rolls = IntRange.fixed(value);
        return this;
    }

    public PoolBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public PoolBuilder conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    public PoolBuilder entry(PoolEntry entry) {
        if (entry != null) entries.add(entry);
        return this;
    }

    public PoolBuilder entry(PoolEntryBuilder builder) {
        PoolEntry e = builder != null ? builder.build() : null;
        if (e != null) entries.add(e);
        return this;
    }

    public PoolBuilder entries(List<PoolEntry> list) {
        if (list == null || list.isEmpty()) return this;
        for (PoolEntry e : list) {
            if (e != null) entries.add(e);
        }
        return this;
    }

    @Override
    public Pool build() {
        IntRange r = (rolls != null) ? rolls : IntRange.ONE;
        Conditions c = (conditions != null) ? conditions : Conditions.EMPTY;
        List<PoolEntry> es = entries.isEmpty() ? List.of() : List.copyOf(entries);
        return new Pool(r, c, es);
    }
}