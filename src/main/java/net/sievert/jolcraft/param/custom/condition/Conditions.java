package net.sievert.jolcraft.param.custom.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamMatching;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.base.ConditionParam;
import net.sievert.jolcraft.param.custom.condition.custom.*;
import net.sievert.jolcraft.param.runtime.WorldContext;

import java.util.List;

public record Conditions(List<ConditionParam> conditions)
        implements ConditionParam, ParamData<Conditions> {

    public static final String KEY = JolCraftParameterIds.CONDITIONS;
    public static final Conditions EMPTY = new Conditions(List.of());

    public Conditions {
        conditions = ParamValidations.sanitizeList(conditions);
    }

    @Override
    public String key() {
        return KEY;
    }

    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    @Override
    public boolean matches(WorldContext ctx) {
        return ParamMatching.allMatch(conditions, ctx);
    }

    @Override
    public DataResult<Conditions> validate() {
        return ParamValidations.children(
                this,
                conditions,
                JolCraftParameterIds.CONDITIONS,
                condition -> condition instanceof ParamData<?> data
                        ? data.validate()
                        : ParamValidations.ok(condition)
        );
    }

    @Override
    public Codec<Conditions> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Conditions> streamCodec() {
        return STREAM_CODEC;
    }

    private static final Codec<List<ConditionParam>> RAW_CODEC =
            ParamCodecs.multi(
                    List.of(
                            ParamCodecs.entry(BiomeCondition.KEY, BiomeCondition.CODEC),
                            ParamCodecs.entry(ChanceCondition.KEY, ChanceCondition.CODEC),
                            ParamCodecs.entry(DimensionCondition.KEY, DimensionCondition.CODEC),
                            ParamCodecs.entry(PlayerLevelCondition.KEY, PlayerLevelCondition.CODEC),
                            ParamCodecs.entry(TimeCondition.KEY, TimeCondition.CODEC),
                            ParamCodecs.entry(WeatherCondition.KEY, WeatherCondition.CODEC)
                    ),
                    ConditionParam::key
            );

    public static final Codec<Conditions> CODEC =
            ParamCodecs.validated(
                    RAW_CODEC.xmap(Conditions::new, Conditions::conditions),
                    Conditions::validate
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<ConditionParam>> RAW_STREAM_CODEC =
            ParamCodecs.multiStream(
                    List.of(
                            ParamCodecs.streamEntry(BiomeCondition.KEY, BiomeCondition.STREAM_CODEC),
                            ParamCodecs.streamEntry(ChanceCondition.KEY, ChanceCondition.STREAM_CODEC),
                            ParamCodecs.streamEntry(DimensionCondition.KEY, DimensionCondition.STREAM_CODEC),
                            ParamCodecs.streamEntry(PlayerLevelCondition.KEY, PlayerLevelCondition.STREAM_CODEC),
                            ParamCodecs.streamEntry(TimeCondition.KEY, TimeCondition.STREAM_CODEC),
                            ParamCodecs.streamEntry(WeatherCondition.KEY, WeatherCondition.STREAM_CODEC)
                    ),
                    ConditionParam::key
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Conditions> STREAM_CODEC =
            ParamCodecs.validatedStream(
                    RAW_STREAM_CODEC.map(Conditions::new, Conditions::conditions),
                    Conditions::validate
            );
}