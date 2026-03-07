package net.sievert.jolcraft.data.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.data.recipe.param.input.base.InputDispatch;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.EntityInput;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputDispatch;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.custom.EffectOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.TextOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntityOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleOutput;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Central registration point for param dispatch tables that exist.
 *
 * Currently:
 * - OutputDispatch only
 *
 * Rule:
 * - Register in one place
 * - Never reorder existing registrations (append-only)
 * - Freeze dispatch tables after registration
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftRecipeParameters {

    private static boolean DONE = false;

    private JolCraftRecipeParameters() {}

    public static void registerAll() {
        if (DONE) return;
        DONE = true;

        // =========================================================
        // INPUT PARAMS
        // =========================================================

        InputDispatch.register(
                ItemInput.TYPE_ID,
                ItemInput.CODEC,
                ItemInput.STREAM_CODEC
        );

        InputDispatch.register(
                EntityInput.TYPE_ID,
                EntityInput.CODEC,
                EntityInput.STREAM_CODEC
        );

        InputDispatch.freeze();

        // =========================================================
        // OUTPUT PARAMS
        // =========================================================

        OutputDispatch.register(
                ItemOutput.TYPE_ID,
                ItemOutput.CODEC,
                ItemOutput.STREAM_CODEC
        );

        OutputDispatch.register(
                EntityOutput.TYPE_ID,
                EntityOutput.CODEC,
                EntityOutput.STREAM_CODEC
        );

        OutputDispatch.register(
                EffectOutput.TYPE_ID,
                EffectOutput.CODEC,
                EffectOutput.STREAM_CODEC
        );

        OutputDispatch.register(
                SoundOutput.TYPE_ID,
                SoundOutput.CODEC,
                SoundOutput.STREAM_CODEC
        );

        OutputDispatch.register(
                ParticleOutput.TYPE_ID,
                ParticleOutput.CODEC,
                ParticleOutput.STREAM_CODEC
        );

        OutputDispatch.register(
                TextOutput.TYPE_ID,
                TextOutput.CODEC,
                TextOutput.STREAM_CODEC
        );

        OutputDispatch.register(
                Outputs.TYPE_ID,
                Outputs.CODEC,
                Outputs.STREAM_CODEC
        );

        OutputDispatch.freeze();
    }
}