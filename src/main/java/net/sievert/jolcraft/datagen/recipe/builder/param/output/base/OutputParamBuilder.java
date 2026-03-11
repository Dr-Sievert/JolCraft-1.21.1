package net.sievert.jolcraft.datagen.recipe.builder.param.output.base;

import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen helper that produces canonical {@link Outputs}.
 *
 * Why Outputs (not OutputParam):
 * - ParamBuilder requires T extends SelfValidating<T>
 * - OutputParam is not SelfValidating<OutputParam>
 * - Outputs is both OutputParam and SelfValidating<Outputs>
 *
 * Use:
 * - wrapSingle(leaf) to normalize any leaf OutputParam into Outputs (single pool / entry).
 * - outputs(...) to provide a full Outputs directly.
 */
public final class OutputParamBuilder implements ParamBuilder<Outputs> {

    private @Nullable Outputs outputs;

    private OutputParamBuilder() {}

    public static @NotNull OutputParamBuilder create() {
        return new OutputParamBuilder();
    }

    public @NotNull OutputParamBuilder outputs(@Nullable Outputs outputs) {
        this.outputs = outputs;
        return this;
    }

    public @NotNull OutputParamBuilder outputs(@Nullable OutputsBuilder builder) {
        this.outputs = builder != null ? builder.build() : null;
        return this;
    }

    public @NotNull OutputParamBuilder wrapSingle(@Nullable OutputParam leaf) {
        this.outputs = leaf != null ? Outputs.wrapSingle(leaf) : null;
        return this;
    }

    @Override
    public @NotNull Outputs build() {
        return outputs != null ? outputs : Outputs.EMPTY;
    }
}