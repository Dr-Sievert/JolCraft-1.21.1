package net.sievert.jolcraft.param.custom.condition.custom;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.event.game.world.JolCraftTimeHelper;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.base.ConditionParam;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.util.JolCraftEnumHelper;

import java.util.List;

public record TimeCondition(
        Mode mode,
        int min,
        int max
) implements ConditionParam, ParamData<TimeCondition> {

    public enum Mode implements JolCraftEnumHelper.StringId {
        RANGE(JolCraftParameterIds.RANGE),
        DAY(JolCraftDictionary.DAY),
        NIGHT(JolCraftDictionary.NIGHT);

        private final String id;

        Mode(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public String id() {
            return id;
        }

        public static DataResult<Mode> fromId(String id) {
            Mode mode = JolCraftEnumHelper.byStringIdNullable(Mode.class, id, null);

            if (mode == Mode.DAY || mode == Mode.NIGHT) {
                return DataResult.success(mode);
            }

            return DataResult.error(() ->
                    "time must be either 'day', 'night', or a two-int range [min, max] (got " + id + ")"
            );
        }
    }

    private static final int DAY_TICKS = (int) JolCraftTimeHelper.TICKS_PER_DAY;
    private static final int DAY_MIN = 0;
    private static final int DAY_MAX = 11999;
    private static final int NIGHT_MIN = 12000;
    private static final int NIGHT_MAX = 23999;
    private static final int RANGE_SIZE = 2;

    private static final Codec<Mode> MODE_CODEC =
            Codec.STRING.comapFlatMap(Mode::fromId, Mode::id);

    private static final Codec<TimeCondition> RANGE_CODEC =
            Codec.INT.listOf().comapFlatMap(TimeCondition::fromRangeList, TimeCondition::toRangeList);

    public static final String KEY = JolCraftParameterIds.TIME;

    public TimeCondition {
        mode = mode != null ? mode : Mode.RANGE;

        if (mode == Mode.DAY) {
            min = DAY_MIN;
            max = DAY_MAX;
        } else if (mode == Mode.NIGHT) {
            min = NIGHT_MIN;
            max = NIGHT_MAX;
        }
    }

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public boolean matches(WorldContext ctx) {
        int time = (int) (ctx.level().getDayTime() % DAY_TICKS);

        return switch (mode) {
            case DAY -> time >= DAY_MIN && time <= DAY_MAX;
            case NIGHT -> time >= NIGHT_MIN;
            case RANGE -> min <= max
                    ? time >= min && time <= max
                    : time >= min || time <= max;
        };
    }

    @Override
    public Codec<TimeCondition> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TimeCondition> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<TimeCondition> validate() {
        if (mode == Mode.DAY || mode == Mode.NIGHT) {
            return ParamValidations.ok(this);
        }

        if (min < 0 || min >= DAY_TICKS) {
            return ParamValidations.invalid(
                    JolCraftParameterIds.MIN + " must be in range [0, 23999]"
            );
        }

        if (max < 0 || max >= DAY_TICKS) {
            return ParamValidations.invalid(
                    JolCraftParameterIds.MAX + " must be in range [0, 23999]"
            );
        }

        return ParamValidations.ok(this);
    }

    private static DataResult<TimeCondition> fromRangeList(List<Integer> values) {
        if (values.size() != RANGE_SIZE) {
            return DataResult.error(() ->
                    "time range must contain exactly two integers [min, max]"
            );
        }

        return new TimeCondition(Mode.RANGE, values.get(0), values.get(1)).validate();
    }

    private static List<Integer> toRangeList(TimeCondition condition) {
        return List.of(condition.min(), condition.max());
    }

    private static DataResult<TimeCondition> fromEither(Either<Mode, TimeCondition> either) {
        return either.map(
                mode -> DataResult.success(new TimeCondition(mode, DAY_MIN, DAY_MAX)),
                TimeCondition::validate
        );
    }

    private static Either<Mode, TimeCondition> toEither(TimeCondition condition) {
        return condition.mode() == Mode.RANGE
                ? Either.right(condition)
                : Either.left(condition.mode());
    }

    public static final Codec<TimeCondition> CODEC =
            ParamCodecs.validated(
                    ParamCodecs.either(
                            MODE_CODEC,
                            RANGE_CODEC,
                            TimeCondition::fromEither,
                            c -> DataResult.success(toEither(c))
                    ),
                    TimeCondition::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, TimeCondition> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, c) -> {
                        buf.writeEnum(c.mode());
                        buf.writeVarInt(c.min());
                        buf.writeVarInt(c.max());
                    },
                    buf -> new TimeCondition(
                            buf.readEnum(Mode.class),
                            buf.readVarInt(),
                            buf.readVarInt()
                    )
            ), TimeCondition::validate);
}