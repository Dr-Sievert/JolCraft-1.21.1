package net.sievert.jolcraft.param.custom.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.base.RegistryTarget;
import net.sievert.jolcraft.param.custom.condition.base.ConditionParam;
import net.sievert.jolcraft.param.runtime.WorldContext;

public record DimensionCondition(RegistryTarget<Level> target)
        implements ConditionParam, ParamData<DimensionCondition> {

    public static final String KEY = JolCraftParameterIds.DIMENSION;

    public DimensionCondition {
        if (target == null) throw new IllegalArgumentException("DimensionCondition target cannot be null");
    }

    @Override public String key() { return KEY; }

    @Override
    public boolean matches(WorldContext ctx) {
        var currentKey = ctx.level().dimension();

        return target.value().map(
                dimension -> dimension.unwrapKey().map(currentKey::equals).orElse(false),
                tag -> ctx.level().registryAccess()
                        .lookup(Registries.DIMENSION)
                        .flatMap(lookup -> lookup.get(currentKey))
                        .map(holder -> holder.is(tag))
                        .orElse(false)
        );
    }

    @Override
    public DataResult<DimensionCondition> validate() {
        return ParamValidations.wrap(this, target.validate(), KEY);
    }

    @Override public Codec<DimensionCondition> codec() { return CODEC; }
    @Override public StreamCodec<RegistryFriendlyByteBuf, DimensionCondition> streamCodec() { return STREAM_CODEC; }

    public static final Codec<DimensionCondition> CODEC =
            ParamCodecs.validated(
                    ParamCodecs.registryTargetValue(Registries.DIMENSION)
                            .xmap(DimensionCondition::new, DimensionCondition::target),
                    DimensionCondition::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionCondition> STREAM_CODEC =
            ParamCodecs.validatedStream(
                    ParamCodecs.registryTargetValueStream(Registries.DIMENSION)
                            .map(DimensionCondition::new, DimensionCondition::target),
                    DimensionCondition::validate
            );
}