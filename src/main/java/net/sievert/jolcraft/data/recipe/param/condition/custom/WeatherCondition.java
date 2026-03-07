package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Atomic condition: weather gate.
 *
 * Schema (at least one of rain/thunder must be present):
 * { "type": "jolcraft:weather", "rain": true, "invert": false }
 * { "type": "jolcraft:weather", "thunder": false }
 * { "type": "jolcraft:weather", "rain": true, "thunder": false }
 *
 * Invalid state representable (no requirements selected).
 * Invalid -> false at runtime.
 */
public record WeatherCondition(
        boolean requireRaining,
        boolean raining,
        boolean requireThundering,
        boolean thundering,
        boolean invert
) implements Condition {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<WeatherCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.RAIN)
                            .forGetter(c -> c.requireRaining ? Optional.of(c.raining) : Optional.empty()),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.THUNDER)
                            .forGetter(c -> c.requireThundering ? Optional.of(c.thundering) : Optional.empty()),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false)
                            .forGetter(WeatherCondition::invert)
            ).apply(inst, (optRain, optThunder, inv) -> new WeatherCondition(
                    optRain.isPresent(),
                    optRain.orElse(false),
                    optThunder.isPresent(),
                    optThunder.orElse(false),
                    inv
            )));

    public static final Codec<WeatherCondition> CODEC =
            RAW_CODEC.flatXmap(
                    WeatherCondition::validateDecoded,
                    DataResult::success
            );

    private static DataResult<WeatherCondition> validateDecoded(WeatherCondition c) {
        if (c == null) {
            return DataResult.error(() -> "weather condition is null");
        }
        if (!c.requireRaining && !c.requireThundering) {
            return DataResult.error(() ->
                    "weather condition must specify at least one of '" +
                            JolCraftParameterIds.RAIN + "' or '" +
                            JolCraftParameterIds.THUNDER + "'"
            );
        }
        return DataResult.success(c);
    }

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, WeatherCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        buf.writeBoolean(c.requireRaining);
                        buf.writeBoolean(c.raining);
                        buf.writeBoolean(c.requireThundering);
                        buf.writeBoolean(c.thundering);
                        buf.writeBoolean(c.invert);
                    },
                    buf -> new WeatherCondition(
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean()
                    )
            );

    // ---------------------------------------------------------------------
    // DISPATCH
    // ---------------------------------------------------------------------

    @Override
    public ResourceLocation typeId() {
        return ConditionTypes.TYPE_WEATHER;
    }

    // ---------------------------------------------------------------------
    // TEST
    // ---------------------------------------------------------------------

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        if (!requireRaining && !requireThundering) {
            return false;
        }

        var level = ctx.level();

        boolean pass = true;

        if (requireRaining) {
            pass &= (level.isRaining() == raining);
        }
        if (requireThundering) {
            pass &= (level.isThundering() == thundering);
        }

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