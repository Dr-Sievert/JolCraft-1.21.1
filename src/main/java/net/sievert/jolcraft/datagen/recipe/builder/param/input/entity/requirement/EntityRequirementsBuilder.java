package net.sievert.jolcraft.datagen.recipe.builder.param.input.entity.requirement;

import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.AttributeRequirement;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.BabyRequirement;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.EffectRequirement;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.EquipmentRequirement;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.EntityRequirements;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen builder for {@link EntityRequirements} (AND bundle).
 *
 * Notes:
 * - This wraps atomics; JSON is a list of exactly-one-of maps per atomic.
 * - Builder ignores nulls and never throws.
 * - Domain validation is delegated to {@link EntityRequirements#validate()} (currently always ok).
 */
public final class EntityRequirementsBuilder implements ParamBuilder<EntityRequirements> {

    private List<EntityRequirements.Atomic> atomics;

    private EntityRequirementsBuilder() {}

    public static EntityRequirementsBuilder create() {
        return new EntityRequirementsBuilder();
    }

    // ---------------------------------------------------------------------
    // BULK
    // ---------------------------------------------------------------------

    public EntityRequirementsBuilder requirements(List<EntityRequirements.Atomic> atomics) {
        this.atomics = atomics;
        return this;
    }

    public EntityRequirementsBuilder atomic(EntityRequirements.Atomic atomic) {
        if (atomic == null) return this;

        List<EntityRequirements.Atomic> list = this.atomics;
        if (list == null || list.isEmpty()) {
            this.atomics = new ArrayList<>(List.of(atomic));
            return this;
        }

        ArrayList<EntityRequirements.Atomic> next = new ArrayList<>(list.size() + 1);
        for (EntityRequirements.Atomic a : list) if (a != null) next.add(a);
        next.add(atomic);
        this.atomics = next;
        return this;
    }

    // ---------------------------------------------------------------------
    // CONVENIENCE ATOMICS
    // ---------------------------------------------------------------------

    public EntityRequirementsBuilder baby(BabyRequirement req) {
        return atomic(req != null ? new EntityRequirements.BabyAtomic(req) : null);
    }

    public EntityRequirementsBuilder baby(BabyRequirementBuilder builder) {
        return baby(builder != null ? builder.build() : null);
    }

    public EntityRequirementsBuilder effect(EffectRequirement req) {
        return atomic(req != null ? new EntityRequirements.EffectAtomic(req) : null);
    }

    public EntityRequirementsBuilder effect(EffectRequirementBuilder builder) {
        return effect(builder != null ? builder.build() : null);
    }

    public EntityRequirementsBuilder attribute(AttributeRequirement req) {
        return atomic(req != null ? new EntityRequirements.AttributeAtomic(req) : null);
    }

    public EntityRequirementsBuilder attribute(AttributeRequirementBuilder builder) {
        return attribute(builder != null ? builder.build() : null);
    }

    public EntityRequirementsBuilder equipment(EquipmentRequirement req) {
        return atomic(req != null ? new EntityRequirements.EquipmentAtomic(req) : null);
    }

    public EntityRequirementsBuilder equipment(EquipmentRequirementBuilder builder) {
        return equipment(builder != null ? builder.build() : null);
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public EntityRequirements build() {
        List<EntityRequirements.Atomic> list = this.atomics;
        if (list == null || list.isEmpty()) {
            return EntityRequirements.EMPTY;
        }

        ArrayList<EntityRequirements.Atomic> safe = new ArrayList<>(list.size());
        for (EntityRequirements.Atomic a : list) if (a != null) safe.add(a);

        return safe.isEmpty() ? EntityRequirements.EMPTY : new EntityRequirements(safe);
    }
}