package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity.requirement;

import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.BabyRequirement;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

/**
 * Datagen builder for {@link BabyRequirement}.
 *
 * Policy:
 * - Never throws
 * - No validation logic here (delegated to param)
 * - Deterministic build
 */
public final class BabyRequirementBuilder implements ParamBuilder<BabyRequirement> {

    private Boolean value;

    private BabyRequirementBuilder() {}

    public static BabyRequirementBuilder create() {
        return new BabyRequirementBuilder();
    }

    // ---------------------------------------------------------------------
    // FIELDS
    // ---------------------------------------------------------------------

    public BabyRequirementBuilder value(boolean value) {
        this.value = value;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public BabyRequirement build() {
        return new BabyRequirement(value != null && value);
    }
}