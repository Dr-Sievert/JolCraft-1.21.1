package net.sievert.jolcraft.datagen.recipe.builder.param.input.item.requirement;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement.EnchantmentRequirement;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.Optional;

/**
 * Datagen builder for {@link EnchantmentRequirement}.
 *
 * Contract (matches param):
 * - Exactly one of enchantment / enchantmentTag should be set.
 * - minLevel defaults to 1 and is clamped to >= 1 for fail-closed stability.
 *
 * Policy:
 * - Never throws
 * - Deterministic build
 * - Leaves strict validation to {@link EnchantmentRequirement#validate()}
 */
public final class EnchantmentRequirementBuilder implements ParamBuilder<EnchantmentRequirement> {

    private Holder<Enchantment> enchantment;
    private TagKey<Enchantment> enchantmentTag;
    private Integer minLevel;

    private EnchantmentRequirementBuilder() {}

    public static EnchantmentRequirementBuilder create() {
        return new EnchantmentRequirementBuilder();
    }

    // ---------------------------------------------------------------------
    // FIELDS
    // ---------------------------------------------------------------------

    public EnchantmentRequirementBuilder enchantment(Holder<Enchantment> enchantment) {
        this.enchantment = enchantment;
        return this;
    }

    public EnchantmentRequirementBuilder enchantmentTag(TagKey<Enchantment> enchantmentTag) {
        this.enchantmentTag = enchantmentTag;
        return this;
    }

    public EnchantmentRequirementBuilder minLevel(int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    // Convenience: ensure exactly-one-of in the builder (last call wins)
    public EnchantmentRequirementBuilder clearEnchantment() {
        this.enchantment = null;
        return this;
    }

    public EnchantmentRequirementBuilder clearEnchantmentTag() {
        this.enchantmentTag = null;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public EnchantmentRequirement build() {
        int lvl = (minLevel != null) ? minLevel : 1;
        if (lvl < 1) lvl = 1;

        Optional<Holder<Enchantment>> enchOpt = (enchantment != null) ? Optional.of(enchantment) : Optional.empty();
        Optional<TagKey<Enchantment>> tagOpt = (enchantmentTag != null) ? Optional.of(enchantmentTag) : Optional.empty();

        return new EnchantmentRequirement(enchOpt, tagOpt, lvl);
    }
}