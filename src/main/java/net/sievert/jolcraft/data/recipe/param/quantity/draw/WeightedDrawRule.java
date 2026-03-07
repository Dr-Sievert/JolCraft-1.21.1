package net.sievert.jolcraft.data.recipe.param.quantity.draw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import org.jetbrains.annotations.NotNull;

/**
 * Binds a {@link DrawRule} with optional weight metadata.
 *
 * Weight is optional at usage sites; missing => defaults to 1.
 * Semantics are defined by the container using these entries.
 *
 * NOTE:
 * This is intentionally not a "loot pool" (no weighted selection here).
 * It is a light entry wrapper for attaching weight metadata to a draw rule.
 */
public record WeightedDrawRule(
        DrawRule rule,
        WeightParam weight
) implements SelfValidating<WeightedDrawRule> {

    // ---------------------------------------------------------------------
    // CANONICAL DEFAULTING
    // ---------------------------------------------------------------------

    public WeightedDrawRule {
        rule = (rule != null) ? rule : new DrawRule(null, null);
        weight = (weight != null) ? weight : WeightParam.ONE;
    }

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    /**
     * Flat entry codec to avoid JSON churn.
     *
     * Schema:
     * - rolls / conditions (same as {@link DrawRule})
     * - optional weight
     */
    private static final Codec<WeightedDrawRule> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.ROLLS, IntRange.ONE)
                            .forGetter(v -> v.rule().rolls()),

                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(v -> v.rule().conditions()),

                    WeightParam.CODEC
                            .optionalFieldOf(JolCraftParameterIds.WEIGHT, WeightParam.ONE)
                            .forGetter(WeightedDrawRule::weight)
            ).apply(instance, (rolls, conds, weight) ->
                    new WeightedDrawRule(new DrawRule(rolls, conds), weight)));

    public static final Codec<WeightedDrawRule> CODEC = ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, WeightedDrawRule> STREAM_CODEC =
            StreamCodec.composite(
                    DrawRule.STREAM_CODEC, v -> v.rule != null ? v.rule : new DrawRule(null, null),
                    WeightParam.STREAM_CODEC, v -> v.weight != null ? v.weight : WeightParam.ONE,
                    WeightedDrawRule::new
            );

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<WeightedDrawRule> validate() {
        DrawRule rr = rule;
        if (rr == null) rr = new DrawRule(null, null);

        var rv = rr.validate();
        var rErrOpt = rv.error();
        if (rErrOpt.isPresent()) {
            var e = rErrOpt.orElse(null);
            String msg = e.message();
            return DataResult.error(() -> "rule invalid: " + msg);
        }

        WeightParam ww = weight;
        if (ww == null) ww = WeightParam.ONE;

        var wv = ww.validate();
        var wErrOpt = wv.error();
        return wErrOpt.<DataResult<WeightedDrawRule>>map(e ->
                DataResult.error(() -> JolCraftParameterIds.WEIGHT + " invalid: " + e.message())
        ).orElseGet(() -> DataResult.success(this));
    }
}