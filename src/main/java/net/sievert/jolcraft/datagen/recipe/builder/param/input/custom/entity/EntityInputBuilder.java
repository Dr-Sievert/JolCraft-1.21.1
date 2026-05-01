package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity;

import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.input.custom.entity.EntityInput;
import net.sievert.jolcraft.world.recipe.param.input.custom.entity.requirement.EntityRequirements;
import net.sievert.jolcraft.world.recipe.param.input.custom.entity.selector.EntitySelector;
import net.sievert.jolcraft.world.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity.requirement.EntityRequirementsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity.selector.EntitySelectorBuilder;

/**
 * Datagen builder for {@link EntityInput}.
 *
 * Policy:
 * - Mutation methods never throw
 * - Ignores null builders
 * - Deterministic build
 * - Leaves domain validation to {@link EntityInput#validate()}
 */
public final class EntityInputBuilder implements ParamBuilder<EntityInput> {

    private Conditions conditions;
    private EntitySelector selector;
    private IntRange count;
    private EntityRequirements requirements;

    private EntityInputBuilder() {}

    public static EntityInputBuilder create() {
        return new EntityInputBuilder();
    }

    // ---------------------------------------------------------------------
    // CONDITIONS
    // ---------------------------------------------------------------------

    public EntityInputBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public EntityInputBuilder conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // SELECTOR
    // ---------------------------------------------------------------------

    public EntityInputBuilder selector(EntitySelector selector) {
        this.selector = selector;
        return this;
    }

    public EntityInputBuilder selector(EntitySelectorBuilder builder) {
        this.selector = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // COUNT
    // ---------------------------------------------------------------------

    public EntityInputBuilder count(IntRange count) {
        this.count = count;
        return this;
    }

    // ---------------------------------------------------------------------
    // REQUIREMENTS
    // ---------------------------------------------------------------------

    public EntityInputBuilder requirements(EntityRequirements requirements) {
        this.requirements = requirements;
        return this;
    }

    public EntityInputBuilder requirements(EntityRequirementsBuilder builder) {
        this.requirements = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public EntityInput build() {
        Conditions c = conditions != null ? conditions : Conditions.EMPTY;
        IntRange n = count != null ? count : IntRange.ONE;
        EntityRequirements r = requirements != null ? requirements : EntityRequirements.EMPTY;

        if (selector == null) {
            throw new IllegalStateException("Missing required field '" + JolCraftParameterIds.SELECTOR + "'");
        }

        return new EntityInput(c, selector, n, r);
    }
}