package net.sievert.jolcraft.datagen.recipe.builder.param.quantity.draw;

import net.sievert.jolcraft.data.recipe.param.quantity.draw.DrawRules;
import net.sievert.jolcraft.data.recipe.param.quantity.draw.WeightedDrawRule;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;

public final class DrawRulesBuilder implements ParamBuilder<DrawRules> {

    private final ArrayList<WeightedDrawRule> rules = new ArrayList<>();

    private DrawRulesBuilder() {}

    public static DrawRulesBuilder create() {
        return new DrawRulesBuilder();
    }

    public DrawRulesBuilder rule(WeightedDrawRule rule) {
        if (rule != null) rules.add(rule);
        return this;
    }

    public DrawRulesBuilder rule(WeightedDrawRuleBuilder builder) {
        WeightedDrawRule r = builder != null ? builder.build() : null;
        if (r != null) rules.add(r);
        return this;
    }

    public DrawRulesBuilder rules(List<WeightedDrawRule> list) {
        if (list == null || list.isEmpty()) return this;
        for (WeightedDrawRule r : list) if (r != null) rules.add(r);
        return this;
    }

    @Override
    public DrawRules build() {
        return new DrawRules(rules.isEmpty() ? List.of() : List.copyOf(rules));
    }
}