package net.sievert.jolcraft.param.custom.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.base.ConditionParam;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

public record WeatherCondition(Mode mode)
        implements ConditionParam, ParamData<WeatherCondition> {

    public enum Mode implements JolCraftEnumHelper.StringId {
        RAIN(JolCraftDictionary.RAIN),
        THUNDER(JolCraftDictionary.THUNDER),
        CLEAR(JolCraftDictionary.CLEAR),
        NO_CLEAR(JolCraftStrings.underscored(JolCraftDictionary.NO, JolCraftDictionary.CLEAR));

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

            return mode != null
                    ? DataResult.success(mode)
                    : DataResult.error(() ->
                    "weather must be one of [rain, thunder, clear, no_clear] (got " + id + ")"
            );
        }
    }

    private static final Codec<Mode> MODE_CODEC = Codec.STRING.comapFlatMap(Mode::fromId, Mode::id);

    public static final String KEY = JolCraftParameterIds.WEATHER;

    public WeatherCondition {
        if (mode == null) {
            throw new IllegalArgumentException("WeatherCondition mode cannot be null");
        }
    }

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public boolean matches(WorldContext ctx) {
        var level = ctx.level();

        return switch (mode) {
            case RAIN -> level.isRaining();
            case THUNDER -> level.isThundering();
            case CLEAR -> !level.isRaining() && !level.isThundering();
            case NO_CLEAR -> level.isRaining() || level.isThundering();
        };
    }

    @Override
    public Codec<WeatherCondition> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WeatherCondition> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<WeatherCondition> validate() {
        return ParamValidations.required(mode, JolCraftParameterIds.WEATHER).map(v -> this);
    }

    public static final Codec<WeatherCondition> CODEC =
            ParamCodecs.validated(
                    MODE_CODEC.xmap(WeatherCondition::new, WeatherCondition::mode),
                    WeatherCondition::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, WeatherCondition> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, c) -> buf.writeEnum(c.mode()),
                    buf -> new WeatherCondition(buf.readEnum(Mode.class))
            ), WeatherCondition::validate);
}