package net.sievert.jolcraft.datagen.recipe.builder.param.input.item.requirement;

import net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement.ComponentRequirement;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement.EnchantmentRequirement;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement.ItemRequirements;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Datagen builder for {@link ItemRequirements}.
 *
 * Policy:
 * - Never throws
 * - Ignores null entries
 * - Deterministic build
 * - Leaves strict validation to {@link ItemRequirements#validate()}
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ItemRequirementsBuilder implements ParamBuilder<ItemRequirements> {

    private List<EnchantmentRequirement> enchantments;
    private Optional<ComponentRequirement> componentRequirement;

    private ItemRequirementsBuilder() {}

    public static ItemRequirementsBuilder create() {
        return new ItemRequirementsBuilder();
    }

    // ---------------------------------------------------------------------
    // ENCHANTMENTS
    // ---------------------------------------------------------------------

    public ItemRequirementsBuilder enchantments(List<EnchantmentRequirement> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public ItemRequirementsBuilder enchantment(EnchantmentRequirement requirement) {
        if (requirement == null) return this;

        List<EnchantmentRequirement> list = this.enchantments;
        if (list == null || list.isEmpty()) {
            this.enchantments = new ArrayList<>(List.of(requirement));
            return this;
        }

        ArrayList<EnchantmentRequirement> next = new ArrayList<>(list.size() + 1);
        for (EnchantmentRequirement r : list) if (r != null) next.add(r);
        next.add(requirement);
        this.enchantments = next;
        return this;
    }

    public ItemRequirementsBuilder enchantment(EnchantmentRequirementBuilder builder) {
        return enchantment(builder != null ? builder.build() : null);
    }

    // ---------------------------------------------------------------------
    // COMPONENTS
    // ---------------------------------------------------------------------

    public ItemRequirementsBuilder componentRequirement(ComponentRequirement requirement) {
        this.componentRequirement = (requirement == null) ? Optional.empty() : Optional.of(requirement);
        return this;
    }

    public ItemRequirementsBuilder componentRequirement(ComponentRequirementBuilder builder) {
        return componentRequirement(builder != null ? builder.build() : null);
    }

    public ItemRequirementsBuilder clearComponentRequirement() {
        this.componentRequirement = Optional.empty();
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public ItemRequirements build() {
        List<EnchantmentRequirement> ench = this.enchantments;
        if (ench == null || ench.isEmpty()) {
            ench = List.of();
        } else {
            ArrayList<EnchantmentRequirement> safe = new ArrayList<>(ench.size());
            for (EnchantmentRequirement r : ench) if (r != null) safe.add(r);
            ench = safe.isEmpty() ? List.of() : List.copyOf(safe);
        }

        Optional<ComponentRequirement> comp = this.componentRequirement;
        if (comp == null) comp = Optional.empty();

        if (ench.isEmpty() && comp.isEmpty()) {
            return ItemRequirements.EMPTY;
        }

        return new ItemRequirements(ench, comp);
    }
}