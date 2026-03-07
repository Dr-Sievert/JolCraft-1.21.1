package net.sievert.jolcraft.datagen.recipe.builder.param.input.item.requirement;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement.ComponentRequirement;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen builder for {@link ComponentRequirement}.
 *
 * Policy:
 * - Never throws
 * - Ignores null entries
 * - Deterministic build
 * - Leaves domain validation to {@link ComponentRequirement#validate()}
 */
public final class ComponentRequirementBuilder implements ParamBuilder<ComponentRequirement> {

    private List<DataComponentPredicate> predicates;
    private List<Holder<DataComponentType<?>>> has;

    private ComponentRequirementBuilder() {}

    public static ComponentRequirementBuilder create() {
        return new ComponentRequirementBuilder();
    }

    // ---------------------------------------------------------------------
    // PREDICATES
    // ---------------------------------------------------------------------

    public ComponentRequirementBuilder predicates(List<DataComponentPredicate> predicates) {
        this.predicates = predicates;
        return this;
    }

    public ComponentRequirementBuilder predicate(DataComponentPredicate predicate) {
        if (predicate == null) return this;

        List<DataComponentPredicate> list = this.predicates;
        if (list == null || list.isEmpty()) {
            this.predicates = new ArrayList<>(List.of(predicate));
            return this;
        }

        ArrayList<DataComponentPredicate> next = new ArrayList<>(list.size() + 1);
        for (DataComponentPredicate p : list) if (p != null) next.add(p);
        next.add(predicate);
        this.predicates = next;
        return this;
    }

    // ---------------------------------------------------------------------
    // HAS
    // ---------------------------------------------------------------------

    public ComponentRequirementBuilder has(List<Holder<DataComponentType<?>>> has) {
        this.has = has;
        return this;
    }

    public ComponentRequirementBuilder has(Holder<DataComponentType<?>> component) {
        if (component == null) return this;

        List<Holder<DataComponentType<?>>> list = this.has;
        if (list == null || list.isEmpty()) {
            this.has = new ArrayList<>(List.of(component));
            return this;
        }

        ArrayList<Holder<DataComponentType<?>>> next = new ArrayList<>(list.size() + 1);
        for (Holder<DataComponentType<?>> h : list) if (h != null) next.add(h);
        next.add(component);
        this.has = next;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public ComponentRequirement build() {
        List<DataComponentPredicate> preds = this.predicates;
        List<Holder<DataComponentType<?>>> hasList = this.has;

        ArrayList<DataComponentPredicate> safePreds = new ArrayList<>();
        if (preds != null) for (DataComponentPredicate p : preds) if (p != null) safePreds.add(p);

        ArrayList<Holder<DataComponentType<?>>> safeHas = new ArrayList<>();
        if (hasList != null) for (Holder<DataComponentType<?>> h : hasList) if (h != null) safeHas.add(h);

        return new ComponentRequirement(
                safePreds.isEmpty() ? List.of() : List.copyOf(safePreds),
                safeHas.isEmpty() ? List.of() : List.copyOf(safeHas)
        );
    }
}