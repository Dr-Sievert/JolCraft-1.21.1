package net.sievert.jolcraft.datagen.recipe.builder.param.output.base;

import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

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

    private Outputs outputs;

    private OutputParamBuilder() {}

    public static OutputParamBuilder create() {
        return new OutputParamBuilder();
    }

    public OutputParamBuilder outputs(Outputs outputs) {
        this.outputs = outputs;
        return this;
    }

    public OutputParamBuilder outputs(OutputsBuilder builder) {
        this.outputs = builder != null ? builder.build() : null;
        return this;
    }

    public OutputParamBuilder wrapSingle(OutputParam leaf) {
        this.outputs = Outputs.wrapSingle(OutputParam.unwrap(leaf));
        return this;
    }

    @Override
    public Outputs build() {
        return outputs != null ? outputs : Outputs.EMPTY;
    }
}