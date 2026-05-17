package net.sievert.jolcraft.datagen.recipe.builder.param.quantity;

import net.sievert.jolcraft.param.custom.condition.Conditions;
import net.sievert.jolcraft.param.custom.quantity.DrawRule;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

public final class DrawRuleBuilder implements ParamBuilder<DrawRule> {

    private IntRange rolls;
    private Conditions conditions;

    private DrawRuleBuilder() {}

    public static DrawRuleBuilder create() {
        return new DrawRuleBuilder();
    }

    public DrawRuleBuilder rolls(IntRange rolls) {
        this.rolls = rolls;
        return this;
    }

    public DrawRuleBuilder rollsFixed(int value) {
        this.rolls = IntRange.fixed(value);
        return this;
    }

    public DrawRuleBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    @Override
    public DrawRule build() {
        IntRange r = (rolls != null) ? rolls : IntRange.ONE;
        Conditions c = (conditions != null) ? conditions : Conditions.EMPTY;
        return new DrawRule(r, c);
    }
}