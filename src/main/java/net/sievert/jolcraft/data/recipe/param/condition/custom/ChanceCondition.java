package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Atomic condition: probabilistic gate.
 *
 * Schema:
 * { "type": "jolcraft:chance", "chance": 0.25, "invert": false }
 *
 * Invariant:
 * - chance in [0.0, 1.0]
 *
 * Runtime:
 * - ctx null -> false
 * - ctx.random null -> false (fail-closed; caller must supply RNG)
 * - pass = random.nextDouble() < chance
 * - result = invert XOR pass
 */
public record ChanceCondition(double chance, boolean invert) implements Condition {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<ChanceCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.DOUBLE.fieldOf(JolCraftParameterIds.CHANCE).forGetter(ChanceCondition::chance),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false).forGetter(ChanceCondition::invert)
            ).apply(inst, ChanceCondition::new));

    public static final Codec<ChanceCondition> CODEC =
            RAW_CODEC.flatXmap(
                    ChanceCondition::validateDecoded,
                    DataResult::success
            );

    private static DataResult<ChanceCondition> validateDecoded(ChanceCondition c) {
        double v = c.chance;

        if (Double.isNaN(v)) return DataResult.error(() -> "chance must not be NaN");
        if (Double.isInfinite(v)) return DataResult.error(() -> "chance must be finite");
        if (v < 0.0D || v > 1.0D) {
            return DataResult.error(() -> "chance must be in range [0.0, 1.0] (got " + v + ")");
        }

        return DataResult.success(c);
    }

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, ChanceCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        buf.writeDouble(c.chance);
                        buf.writeBoolean(c.invert);
                    },
                    buf -> new ChanceCondition(buf.readDouble(), buf.readBoolean())
            );

    // ---------------------------------------------------------------------
    // DISPATCH
    // ---------------------------------------------------------------------

    @Override
    public ResourceLocation typeId() {
        return ConditionTypes.TYPE_CHANCE;
    }

    // ---------------------------------------------------------------------
    // TEST
    // ---------------------------------------------------------------------

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        double v = this.chance;
        if (Double.isNaN(v) || Double.isInfinite(v) || v < 0.0D || v > 1.0D) {
            return false;
        }

        boolean pass = ctx.random().nextDouble() < v;
        return invert != pass;
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(c -> c);
    }
}