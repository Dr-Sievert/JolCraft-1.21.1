package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record TimeCondition(TimeCondition.Mode mode, int min, int max, boolean invert) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.TIME);
    public static final byte DISC = 3;

    private static final int DAY_TICKS = 24000;

    private static final int DAY_MIN = 0;
    private static final int DAY_MAX = 11999;
    private static final int NIGHT_MIN = 12000;
    private static final int NIGHT_MAX = 23999;

    public enum Mode {
        RANGE(JolCraftParameterIds.RANGE),
        DAY(JolCraftDictionary.DAY),
        NIGHT(JolCraftDictionary.NIGHT);

        private final String id;

        Mode(String id) {
            this.id = id;
        }

        public @NotNull String id() {
            return id;
        }

        public static @NotNull DataResult<Mode> fromId(@NotNull String id) {
            for (Mode mode : values()) {
                if (mode.id.equals(id)) {
                    return DataResult.success(mode);
                }
            }

            return DataResult.error(() ->
                    "time must be one of ["
                            + JolCraftDictionary.DAY + ","
                            + JolCraftDictionary.NIGHT + ","
                            + JolCraftParameterIds.RANGE + "] (got " + id + ")"
            );
        }
    }

    private record RangeRaw(int min, int max) {}

    private static final Codec<Mode> MODE_CODEC =
            Codec.STRING.comapFlatMap(Mode::fromId, Mode::id);

    private static final Codec<RangeRaw> RANGE_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<RangeRaw, T>> decode(DynamicOps<T> ops, T input) {
            var mapResult = ops.getMap(input).result();
            if (mapResult.isEmpty()) {
                return DataResult.error(() -> "time range must be an object");
            }

            var map = mapResult.get();

            T minValue = map.get(ops.createString(JolCraftParameterIds.MIN));
            T maxValue = map.get(ops.createString(JolCraftParameterIds.MAX));

            if (minValue == null) {
                return DataResult.error(() ->
                        "time." + JolCraftParameterIds.MIN + " is required for time range"
                );
            }
            if (maxValue == null) {
                return DataResult.error(() ->
                        "time." + JolCraftParameterIds.MAX + " is required for time range"
                );
            }

            DataResult<Integer> min = Codec.INT.parse(ops, minValue);
            if (min.error().isPresent()) {
                return DataResult.error(() ->
                        JolCraftParameterIds.MIN + " invalid: " +
                                min.error().map(DataResult.Error::message).orElse("invalid")
                );
            }

            DataResult<Integer> max = Codec.INT.parse(ops, maxValue);
            if (max.error().isPresent()) {
                return DataResult.error(() ->
                        JolCraftParameterIds.MAX + " invalid: " +
                                max.error().map(DataResult.Error::message).orElse("invalid")
                );
            }

            return DataResult.success(Pair.of(
                    new RangeRaw(
                            min.result().orElseThrow(),
                            max.result().orElseThrow()
                    ),
                    input
            ));
        }

        @Override
        public <T> DataResult<T> encode(RangeRaw input, DynamicOps<T> ops, T prefix) {
            T result = ops.createMap(Stream.empty());

            result = ops.mergeToMap(
                    result,
                    ops.createString(JolCraftParameterIds.MIN),
                    Codec.INT.encodeStart(ops, input.min()).result().orElseThrow()
            ).result().orElse(result);

            result = ops.mergeToMap(
                    result,
                    ops.createString(JolCraftParameterIds.MAX),
                    Codec.INT.encodeStart(ops, input.max()).result().orElseThrow()
            ).result().orElse(result);

            return DataResult.success(result);
        }
    };

    /**
     * Object-only typed codec.
     * Safe for registry dispatch / explicit conditions list encoding.
     */
    public static final Codec<TimeCondition> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<TimeCondition, T>> decode(DynamicOps<T> ops, T input) {
            var mapResult = ops.getMap(input).result();
            if (mapResult.isEmpty()) {
                return DataResult.error(() -> "time must be an object");
            }

            var map = mapResult.get();

            T invertValue = map.get(ops.createString(JolCraftParameterIds.INVERT));
            boolean invert = invertValue != null
                    ? Codec.BOOL.parse(ops, invertValue).result().orElse(false)
                    : false;

            T modeValue = map.get(ops.createString(JolCraftParameterIds.MODE));
            if (modeValue != null) {
                DataResult<Mode> mode = MODE_CODEC.parse(ops, modeValue);
                if (mode.error().isPresent()) {
                    return DataResult.error(() ->
                            mode.error().map(DataResult.Error::message).orElse("invalid time mode")
                    );
                }

                Mode decoded = mode.result().orElseThrow();

                if (decoded == Mode.DAY) {
                    return validateDecoded(new TimeCondition(Mode.DAY, DAY_MIN, DAY_MAX, invert))
                            .map(value -> Pair.of(value, input));
                }

                if (decoded == Mode.NIGHT) {
                    return validateDecoded(new TimeCondition(Mode.NIGHT, NIGHT_MIN, NIGHT_MAX, invert))
                            .map(value -> Pair.of(value, input));
                }
            }

            T minValue = map.get(ops.createString(JolCraftParameterIds.MIN));
            T maxValue = map.get(ops.createString(JolCraftParameterIds.MAX));

            if (minValue == null || maxValue == null) {
                return DataResult.error(() ->
                        "time object must contain either '" + JolCraftParameterIds.MODE +
                                "' = '" + JolCraftDictionary.DAY + "'/'" + JolCraftDictionary.NIGHT +
                                "' or both '" + JolCraftParameterIds.MIN + "' and '" +
                                JolCraftParameterIds.MAX + "'"
                );
            }

            DataResult<Integer> min = Codec.INT.parse(ops, minValue);
            if (min.error().isPresent()) {
                return DataResult.error(() ->
                        JolCraftParameterIds.MIN + " invalid: " +
                                min.error().map(DataResult.Error::message).orElse("invalid")
                );
            }

            DataResult<Integer> max = Codec.INT.parse(ops, maxValue);
            if (max.error().isPresent()) {
                return DataResult.error(() ->
                        JolCraftParameterIds.MAX + " invalid: " +
                                max.error().map(DataResult.Error::message).orElse("invalid")
                );
            }

            return validateDecoded(new TimeCondition(
                    Mode.RANGE,
                    min.result().orElseThrow(),
                    max.result().orElseThrow(),
                    invert
            )).map(value -> Pair.of(value, input));
        }

        @Override
        public <T> DataResult<T> encode(TimeCondition input, DynamicOps<T> ops, T prefix) {
            T result = ops.createMap(Stream.empty());

            if (input.mode() == Mode.DAY || input.mode() == Mode.NIGHT) {
                result = ops.mergeToMap(
                        result,
                        ops.createString(JolCraftParameterIds.MODE),
                        MODE_CODEC.encodeStart(ops, input.mode()).result().orElseThrow()
                ).result().orElse(result);
            } else {
                result = ops.mergeToMap(
                        result,
                        ops.createString(JolCraftParameterIds.MIN),
                        Codec.INT.encodeStart(ops, input.min()).result().orElseThrow()
                ).result().orElse(result);

                result = ops.mergeToMap(
                        result,
                        ops.createString(JolCraftParameterIds.MAX),
                        Codec.INT.encodeStart(ops, input.max()).result().orElseThrow()
                ).result().orElse(result);
            }

            if (input.invert()) {
                result = ops.mergeToMap(
                        result,
                        ops.createString(JolCraftParameterIds.INVERT),
                        Codec.BOOL.encodeStart(ops, true).result().orElseThrow()
                ).result().orElse(result);
            }

            return DataResult.success(result);
        }
    };

    /**
     * Flattened inline codec for the "time" field.
     *
     * Accepted inputs:
     * - "day"
     * - "night"
     * - { "min": x, "max": y }
     * - full object form as fallback
     *
     * Encoding policy:
     * - inverted values encode as full object form
     * - non-inverted DAY/NIGHT encode as string shorthand
     * - non-inverted RANGE encodes as {min,max}
     */
    public static final Codec<TimeCondition> INLINE_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<TimeCondition, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<TimeCondition, T>> full = CODEC.decode(ops, input);
            if (full.result().isPresent()) {
                return full;
            }

            DataResult<Pair<String, T>> stringResult = Codec.STRING.decode(ops, input);
            if (stringResult.result().isPresent()) {
                String id = stringResult.result().orElseThrow().getFirst();
                DataResult<Mode> mode = Mode.fromId(id);
                if (mode.error().isPresent()) {
                    return DataResult.error(() ->
                            mode.error().map(DataResult.Error::message).orElse("invalid time mode")
                    );
                }

                Mode decoded = mode.result().orElseThrow();
                if (decoded == Mode.DAY) {
                    return DataResult.success(Pair.of(
                            new TimeCondition(Mode.DAY, DAY_MIN, DAY_MAX, false),
                            input
                    ));
                }
                if (decoded == Mode.NIGHT) {
                    return DataResult.success(Pair.of(
                            new TimeCondition(Mode.NIGHT, NIGHT_MIN, NIGHT_MAX, false),
                            input
                    ));
                }

                return DataResult.error(() ->
                        "time string form only supports '"
                                + JolCraftDictionary.DAY + "' or '"
                                + JolCraftDictionary.NIGHT + "'"
                );
            }

            DataResult<Pair<RangeRaw, T>> range = RANGE_CODEC.decode(ops, input);
            if (range.result().isPresent()) {
                RangeRaw raw = range.result().orElseThrow().getFirst();
                return validateDecoded(new TimeCondition(Mode.RANGE, raw.min(), raw.max(), false))
                        .map(value -> Pair.of(value, input));
            }

            return DataResult.error(() ->
                    "time must be either an object, '" + JolCraftDictionary.DAY + "', '" +
                            JolCraftDictionary.NIGHT + "', or an object with '" +
                            JolCraftParameterIds.MIN + "' and '" + JolCraftParameterIds.MAX + "'"
            );
        }

        @Override
        public <T> DataResult<T> encode(TimeCondition input, DynamicOps<T> ops, T prefix) {
            if (input.invert()) {
                return CODEC.encode(input, ops, prefix);
            }

            if (input.mode() == Mode.DAY || input.mode() == Mode.NIGHT) {
                return MODE_CODEC.encodeStart(ops, input.mode());
            }

            return RANGE_CODEC.encode(new RangeRaw(input.min(), input.max()), ops, prefix);
        }
    };

    private static final byte KIND_RANGE = 1;
    private static final byte KIND_DAY = 2;
    private static final byte KIND_NIGHT = 3;

    public static final StreamCodec<RegistryFriendlyByteBuf, TimeCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        if (c.mode() == Mode.DAY) {
                            buf.writeByte(KIND_DAY);
                            buf.writeBoolean(c.invert());
                            return;
                        }

                        if (c.mode() == Mode.NIGHT) {
                            buf.writeByte(KIND_NIGHT);
                            buf.writeBoolean(c.invert());
                            return;
                        }

                        buf.writeByte(KIND_RANGE);
                        buf.writeVarInt(c.min());
                        buf.writeVarInt(c.max());
                        buf.writeBoolean(c.invert());
                    },
                    buf -> {
                        byte kind = buf.readByte();

                        if (kind == KIND_DAY) {
                            return new TimeCondition(Mode.DAY, DAY_MIN, DAY_MAX, buf.readBoolean());
                        }
                        if (kind == KIND_NIGHT) {
                            return new TimeCondition(Mode.NIGHT, NIGHT_MIN, NIGHT_MAX, buf.readBoolean());
                        }
                        if (kind == KIND_RANGE) {
                            int min = buf.readVarInt();
                            int max = buf.readVarInt();
                            boolean inv = buf.readBoolean();
                            return new TimeCondition(Mode.RANGE, min, max, inv);
                        }

                        throw new IllegalArgumentException("Unknown TimeCondition stream kind: " + kind);
                    }
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public TimeCondition {
        mode = mode != null ? mode : Mode.RANGE;
    }

    private static @NotNull DataResult<TimeCondition> validateDecoded(@NotNull TimeCondition c) {
        if (c.mode() == Mode.DAY) {
            return DataResult.success(new TimeCondition(Mode.DAY, DAY_MIN, DAY_MAX, c.invert()));
        }

        if (c.mode() == Mode.NIGHT) {
            return DataResult.success(new TimeCondition(Mode.NIGHT, NIGHT_MIN, NIGHT_MAX, c.invert()));
        }

        if (invalidTime(c.min())) {
            return DataResult.error(() ->
                    "time." + JolCraftParameterIds.MIN + " must be in range [0, 23999] (got " + c.min() + ")"
            );
        }

        if (invalidTime(c.max())) {
            return DataResult.error(() ->
                    "time." + JolCraftParameterIds.MAX + " must be in range [0, 23999] (got " + c.max() + ")"
            );
        }

        return DataResult.success(new TimeCondition(Mode.RANGE, c.min(), c.max(), c.invert()));
    }

    private static boolean invalidTime(int t) {
        return t < 0 || t >= DAY_TICKS;
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        int t = (int) (ctx.level().getDayTime() % DAY_TICKS);

        if (mode == Mode.RANGE) {
            boolean pass = (min <= max)
                    ? (t >= min && t <= max)
                    : (t >= min || t <= max);

            return invert != pass;
        }

        if (mode == Mode.DAY) {
            return invert != (t >= DAY_MIN && t <= DAY_MAX);
        }

        if (mode == Mode.NIGHT) {
            return invert != t >= NIGHT_MIN;
        }

        return false;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}