package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.sievert.jolcraft.data.recipe.param.output.custom.EffectOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EffectOutputBuilder {

    private @Nullable Holder<MobEffect> id;
    private int duration = 0;
    private int amplifier = 0;

    private EffectOutputBuilder() {}

    public static @NotNull EffectOutputBuilder builder() {
        return new EffectOutputBuilder();
    }

    // ---------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    public @NotNull DataResult<EffectOutput> build() {
        if (id == null) {
            return DataResult.error(() -> "Missing required field: 'id'");
        }
        if (duration < 1) {
            return DataResult.error(() -> "'duration' must be >= 1");
        }
        if (amplifier < 0) {
            return DataResult.error(() -> "'amplifier' must be >= 0");
        }

        EffectOutput out = new EffectOutput(id, duration, amplifier);
        return out.validate();
    }

    public @NotNull EffectOutput buildOrEmpty() {
        return build().result().orElse(EffectOutput.EMPTY);
    }
}