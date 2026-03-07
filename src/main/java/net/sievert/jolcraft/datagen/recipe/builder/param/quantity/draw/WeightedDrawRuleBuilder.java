package net.sievert.jolcraft.datagen.recipe.builder.param.quantity.draw;

import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.data.recipe.param.quantity.draw.DrawRule;
import net.sievert.jolcraft.data.recipe.param.quantity.draw.WeightedDrawRule;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

public final class WeightedDrawRuleBuilder implements ParamBuilder<WeightedDrawRule> {

    private DrawRule rule;
    private WeightParam weight;

    private WeightedDrawRuleBuilder() {}

    public static WeightedDrawRuleBuilder create() {
        return new WeightedDrawRuleBuilder();
    }

    public WeightedDrawRuleBuilder rule(DrawRule rule) {
        this.rule = rule;
        return this;
    }

    public WeightedDrawRuleBuilder rule(DrawRuleBuilder builder) {
        this.rule = builder != null ? builder.build() : null;
        return this;
    }

    public WeightedDrawRuleBuilder weight(WeightParam weight) {
        this.weight = weight;
        return this;
    }

    public WeightedDrawRuleBuilder weight(int weight) {
        this.weight = new WeightParam(weight);
        return this;
    }

    @Override
    public WeightedDrawRule build() {
        DrawRule r = (rule != null) ? rule : new DrawRule(null, null);
        WeightParam w = (weight != null) ? weight : WeightParam.ONE;
        return new WeightedDrawRule(r, w);
    }
}