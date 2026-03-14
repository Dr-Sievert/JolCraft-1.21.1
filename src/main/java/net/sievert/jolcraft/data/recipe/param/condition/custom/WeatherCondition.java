package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record WeatherCondition(
        boolean requireRain,
        boolean rain,
        boolean requireThunder,
        boolean thunder,
        boolean requireClear,
        boolean clear
) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.WEATHER);
    public static final byte DISC = 2;

    private static final String KEY_CLEAR = JolCraftDictionary.CLEAR;

    private static final String SHORTHAND_RAIN = JolCraftDictionary.RAIN;
    private static final String SHORTHAND_THUNDER = JolCraftDictionary.THUNDER;
    private static final String SHORTHAND_CLEAR = JolCraftDictionary.CLEAR;
    private static final String SHORTHAND_NO_CLEAR =
            JolCraftStrings.underscored(JolCraftDictionary.NO, JolCraftDictionary.CLEAR);

    private static final Codec<WeatherCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.RAIN)
                            .forGetter(c -> c.requireRain() ? Optional.of(c.rain()) : Optional.empty()),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.THUNDER)
                            .forGetter(c -> c.requireThunder() ? Optional.of(c.thunder()) : Optional.empty()),
                    Codec.BOOL.optionalFieldOf(KEY_CLEAR)
                            .forGetter(c -> c.requireClear() ? Optional.of(c.clear()) : Optional.empty())
            ).apply(inst, (optRain, optThunder, optClear) -> new WeatherCondition(
                    optRain.isPresent(),
                    optRain.orElse(false),
                    optThunder.isPresent(),
                    optThunder.orElse(false),
                    optClear.isPresent(),
                    optClear.orElse(false)
            )));

    /**
     * Typed base codec.
     * Object-only for registry dispatch / explicit condition encoding.
     */
    public static final Codec<WeatherCondition> CODEC =
            RAW_CODEC.flatXmap(WeatherCondition::validateDecoded, DataResult::success);

    /**
     * Flattened inline codec.
     *
     * Accepted shorthand:
     * - "rain"
     * - "thunder"
     * - "clear"
     * - "no_clear"
     *
     * Object form is still accepted for all other shapes.
     */
    public static final Codec<WeatherCondition> INLINE_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<WeatherCondition, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<WeatherCondition, T>> full = CODEC.decode(ops, input);
            if (full.result().isPresent()) {
                return full;
            }

            DataResult<Pair<String, T>> stringResult = Codec.STRING.decode(ops, input);
            if (stringResult.result().isPresent()) {
                String value = stringResult.result().orElseThrow().getFirst();

                if (SHORTHAND_RAIN.equals(value)) {
                    return DataResult.success(Pair.of(
                            new WeatherCondition(true, true, false, false, false, false),
                            input
                    ));
                }

                if (SHORTHAND_THUNDER.equals(value)) {
                    return DataResult.success(Pair.of(
                            new WeatherCondition(false, false, true, true, false, false),
                            input
                    ));
                }

                if (SHORTHAND_CLEAR.equals(value)) {
                    return DataResult.success(Pair.of(
                            new WeatherCondition(false, false, false, false, true, true),
                            input
                    ));
                }

                if (SHORTHAND_NO_CLEAR.equals(value)) {
                    return DataResult.success(Pair.of(
                            new WeatherCondition(false, false, false, false, true, false),
                            input
                    ));
                }

                return DataResult.error(() ->
                        "weather must be one of ["
                                + SHORTHAND_RAIN + ", "
                                + SHORTHAND_THUNDER + ", "
                                + SHORTHAND_CLEAR + ", "
                                + SHORTHAND_NO_CLEAR + "] (got " + value + ")"
                );
            }

            return DataResult.error(() ->
                    "weather must be either an object or one of ["
                            + SHORTHAND_RAIN + ", "
                            + SHORTHAND_THUNDER + ", "
                            + SHORTHAND_CLEAR + ", "
                            + SHORTHAND_NO_CLEAR + "]"
            );
        }

        @Override
        public <T> DataResult<T> encode(WeatherCondition input, DynamicOps<T> ops, T prefix) {
            if (isRain(input)) {
                return Codec.STRING.encodeStart(ops, SHORTHAND_RAIN);
            }

            if (isThunder(input)) {
                return Codec.STRING.encodeStart(ops, SHORTHAND_THUNDER);
            }

            if (isClear(input)) {
                return Codec.STRING.encodeStart(ops, SHORTHAND_CLEAR);
            }

            if (isNoClear(input)) {
                return Codec.STRING.encodeStart(ops, SHORTHAND_NO_CLEAR);
            }

            return CODEC.encode(input, ops, prefix);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, WeatherCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        buf.writeBoolean(c.requireRain());
                        buf.writeBoolean(c.rain());
                        buf.writeBoolean(c.requireThunder());
                        buf.writeBoolean(c.thunder());
                        buf.writeBoolean(c.requireClear());
                        buf.writeBoolean(c.clear());
                    },
                    buf -> new WeatherCondition(
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean()
                    )
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static boolean isRain(@NotNull WeatherCondition c) {
        return c.requireRain()
                && c.rain()
                && !c.requireThunder()
                && !c.requireClear();
    }

    private static boolean isThunder(@NotNull WeatherCondition c) {
        return !c.requireRain()
                && c.requireThunder()
                && c.thunder()
                && !c.requireClear();
    }

    private static boolean isClear(@NotNull WeatherCondition c) {
        return !c.requireRain()
                && !c.requireThunder()
                && c.requireClear()
                && c.clear();
    }

    private static boolean isNoClear(@NotNull WeatherCondition c) {
        return !c.requireRain()
                && !c.requireThunder()
                && c.requireClear()
                && !c.clear();
    }

    private static @NotNull DataResult<WeatherCondition> validateDecoded(@NotNull WeatherCondition c) {
        if (!c.requireRain() && !c.requireThunder() && !c.requireClear()) {
            return DataResult.error(() ->
                    "weather condition must specify at least one of '"
                            + JolCraftParameterIds.RAIN + "', '"
                            + JolCraftParameterIds.THUNDER + "', or '"
                            + KEY_CLEAR + "'"
            );
        }

        if (c.requireClear() && c.clear()) {
            if (c.requireRain() && c.rain()) {
                return DataResult.error(() ->
                        "weather.clear=true conflicts with rain=true"
                );
            }

            if (c.requireThunder() && c.thunder()) {
                return DataResult.error(() ->
                        "weather.clear=true conflicts with thunder=true"
                );
            }
        }

        return DataResult.success(c);
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        if (!requireRain && !requireThunder && !requireClear) {
            return false;
        }

        var level = ctx.level();
        boolean pass = true;

        if (requireRain) {
            pass &= (level.isRaining() == rain);
        }

        if (requireThunder) {
            pass &= (level.isThundering() == thunder);
        }

        if (requireClear) {
            boolean isClearNow = !level.isRaining() && !level.isThundering();
            pass &= (isClearNow == clear);
        }

        return pass;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}