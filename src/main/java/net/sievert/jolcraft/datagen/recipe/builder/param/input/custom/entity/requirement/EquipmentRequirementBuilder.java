package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity.requirement;

import net.minecraft.world.entity.EquipmentSlot;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.EquipmentRequirement;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.ItemInputBuilder;

/**
 * Datagen builder for {@link EquipmentRequirement}.
 *
 * Policy:
 * - Never throws
 * - Allows nulls during building; param validation enforces required fields
 * - Deterministic build
 */
public final class EquipmentRequirementBuilder implements ParamBuilder<EquipmentRequirement> {

    private EquipmentSlot slot;
    private ItemInput item;

    private EquipmentRequirementBuilder() {}

    public static EquipmentRequirementBuilder create() {
        return new EquipmentRequirementBuilder();
    }

    // ---------------------------------------------------------------------
    // FIELDS
    // ---------------------------------------------------------------------

    public EquipmentRequirementBuilder slot(EquipmentSlot slot) {
        this.slot = slot;
        return this;
    }

    public EquipmentRequirementBuilder item(ItemInput item) {
        this.item = item;
        return this;
    }

    public EquipmentRequirementBuilder item(ItemInputBuilder builder) {
        this.item = builder != null ? builder.build() : null;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public EquipmentRequirement build() {
        return new EquipmentRequirement(slot, item);
    }
}