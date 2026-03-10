package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity;

import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.EntityInput;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.EntityRequirements;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.selector.EntitySelector;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity.requirement.EntityRequirementsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity.selector.EntitySelectorBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen builder for {@link EntityInput}.
 *
 * Policy:
 * - Deterministic build
 * - Optional layers may be omitted
 * - selector is required
 * - count defaults to {@link IntRange#ONE}
 */
public final class EntityInputBuilder implements ParamBuilder<EntityInput> {

    private @Nullable Conditions conditions;
    private EntitySelector selector;
    private IntRange count = IntRange.ONE;
    private @Nullable EntityRequirements requirements;

    private EntityInputBuilder() {}

    public static @NotNull EntityInputBuilder create() {
        return new EntityInputBuilder();
    }

    // ---------------------------------------------------------------------
    // CONDITIONS
    // ---------------------------------------------------------------------

    public @NotNull EntityInputBuilder conditions(@Nullable Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public @NotNull EntityInputBuilder conditions(@Nullable ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // SELECTOR
    // ---------------------------------------------------------------------

    public @NotNull EntityInputBuilder selector(@NotNull EntitySelector selector) {
        this.selector = selector;
        return this;
    }

    public @NotNull EntityInputBuilder selector(@NotNull EntitySelectorBuilder builder) {
        this.selector = builder.build();
        return this;
    }

    // ---------------------------------------------------------------------
    // COUNT
    // ---------------------------------------------------------------------

    public @NotNull EntityInputBuilder count(@NotNull IntRange count) {
        this.count = count;
        return this;
    }

    // ---------------------------------------------------------------------
    // REQUIREMENTS
    // ---------------------------------------------------------------------

    public @NotNull EntityInputBuilder requirements(@Nullable EntityRequirements requirements) {
        this.requirements = requirements;
        return this;
    }

    public @NotNull EntityInputBuilder requirements(@Nullable EntityRequirementsBuilder builder) {
        this.requirements = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public @NotNull EntityInput build() {
        if (selector == null) {
            throw new IllegalStateException("EntityInputBuilder: selector is required");
        }

        return new EntityInput(
                conditions,
                selector,
                count,
                requirements
        );
    }
}