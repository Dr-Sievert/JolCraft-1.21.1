package net.sievert.jolcraft.datagen.recipe.builder.param.input.entity.requirement;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.AttributeRequirement;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

/**
 * Datagen builder for {@link AttributeRequirement}.
 *
 * Policy:
 * - Never throws
 * - Allows nulls during building; param validation handles required fields
 * - No domain logic beyond simple fail-closed canonicalization (NaN/Inf -> 0)
 */
public final class AttributeRequirementBuilder implements ParamBuilder<AttributeRequirement> {

    private Holder<Attribute> attribute;
    private AttributeRequirement.Operator operator;
    private Double value;

    private AttributeRequirementBuilder() {}

    public static AttributeRequirementBuilder create() {
        return new AttributeRequirementBuilder();
    }

    // ---------------------------------------------------------------------
    // FIELDS
    // ---------------------------------------------------------------------

    public AttributeRequirementBuilder attribute(Holder<Attribute> attribute) {
        this.attribute = attribute;
        return this;
    }

    public AttributeRequirementBuilder operator(AttributeRequirement.Operator operator) {
        this.operator = operator;
        return this;
    }

    public AttributeRequirementBuilder value(double value) {
        this.value = value;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public AttributeRequirement build() {
        Holder<Attribute> attr = this.attribute;

        AttributeRequirement.Operator op =
                (this.operator != null) ? this.operator : AttributeRequirement.Operator.EQUAL;

        double v = (this.value != null) ? this.value : 0.0D;
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            v = 0.0D;
        }

        return new AttributeRequirement(attr, op, v);
    }
}