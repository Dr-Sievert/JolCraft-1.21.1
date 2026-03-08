package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.custom.EffectOutput;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen builder for {@link EffectOutput}.
 *
 * Updated for the current effect output shape:
 * - target is required
 * - no EMPTY sentinel exists
 * - validation is delegated through {@link ParamBuilder#buildValidated()}
 */
public final class EffectOutputBuilder implements ParamBuilder<EffectOutput> {

    private @Nullable Holder<MobEffect> id;
    private int duration = 1;
    private int amplifier = 0;
    private @Nullable Output.EffectTarget target;

    private EffectOutputBuilder() {}

    public static @NotNull EffectOutputBuilder create() {
        return new EffectOutputBuilder();
    }

    public static @NotNull EffectOutputBuilder builder() {
        return create();
    }

    public @NotNull EffectOutputBuilder id(@Nullable Holder<MobEffect> id) {
        this.id = id;
        return this;
    }

    public @NotNull EffectOutputBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public @NotNull EffectOutputBuilder amplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public @NotNull EffectOutputBuilder target(@Nullable Output.EffectTarget target) {
        this.target = target;
        return this;
    }

    public @NotNull EffectOutputBuilder targetPlayer() {
        this.target = Output.EffectTarget.PLAYER;
        return this;
    }

    public @NotNull EffectOutputBuilder targetEntity() {
        this.target = Output.EffectTarget.ENTITY;
        return this;
    }

    @Override
    public @NotNull EffectOutput build() {
        return new EffectOutput(id, duration, amplifier, target);
    }

    public @Nullable EffectOutput buildOrNull() {
        return buildValidated().result().orElse(null);
    }
}
