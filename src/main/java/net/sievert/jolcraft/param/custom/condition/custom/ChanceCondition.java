package net.sievert.jolcraft.param.custom.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.base.ConditionParam;
import net.sievert.jolcraft.param.runtime.WorldContext;

public record ChanceCondition(double chance)
        implements ConditionParam, ParamData<ChanceCondition> {

    public static final String KEY = JolCraftParameterIds.CHANCE;

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public boolean matches(WorldContext ctx) {
        return ctx.random().nextDouble() < chance;
    }

    @Override
    public Codec<ChanceCondition> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ChanceCondition> streamCodec() {
        return STREAM_CODEC;
    }

    public static final Codec<ChanceCondition> CODEC =
            ParamCodecs.validated(
                    Codec.DOUBLE.xmap(ChanceCondition::new, ChanceCondition::chance),
                    ChanceCondition::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ChanceCondition> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, v) -> buf.writeDouble(v.chance()),
                    buf -> new ChanceCondition(buf.readDouble())
            ), ChanceCondition::validate);

    @Override
    public DataResult<ChanceCondition> validate() {
        return Double.isFinite(chance) && chance >= 0.0D && chance <= 1.0D
                ? ParamValidations.ok(this)
                : ParamValidations.invalid(KEY + " must be finite and in range [0.0, 1.0]");
    }
}