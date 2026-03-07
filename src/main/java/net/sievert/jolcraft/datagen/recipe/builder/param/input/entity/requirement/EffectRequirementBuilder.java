package net.sievert.jolcraft.datagen.recipe.builder.param.input.entity.requirement;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.EffectRequirement;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

/**
 * Datagen builder for {@link EffectRequirement}.
 *
 * Policy:
 * - Never throws
 * - Ignores domain validation (delegated to param validate())
 * - Deterministic build; fail-closed defaults
 */
public final class EffectRequirementBuilder implements ParamBuilder<EffectRequirement> {

    private Holder<MobEffect> effect;
    private Integer minAmplifier;

    private EffectRequirementBuilder() {}

    public static EffectRequirementBuilder create() {
        return new EffectRequirementBuilder();
    }

    // ---------------------------------------------------------------------
    // FIELDS
    // ---------------------------------------------------------------------

    public EffectRequirementBuilder effect(Holder<MobEffect> effect) {
        this.effect = effect;
        return this;
    }

    public EffectRequirementBuilder minAmplifier(int minAmplifier) {
        this.minAmplifier = minAmplifier;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public EffectRequirement build() {
        Holder<MobEffect> eff = this.effect;

        int min = (minAmplifier != null) ? minAmplifier : 0;
        if (min < 0) min = 0;

        return new EffectRequirement(eff, min);
    }
}