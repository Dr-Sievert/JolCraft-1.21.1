package net.sievert.jolcraft.param.custom.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.biome.Biome;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.base.RegistryTarget;
import net.sievert.jolcraft.param.custom.condition.base.ConditionParam;
import net.sievert.jolcraft.param.runtime.WorldAnchor;
import net.sievert.jolcraft.param.runtime.WorldContext;

public record BiomeCondition(RegistryTarget<Biome> target)
        implements ConditionParam, ParamData<BiomeCondition> {

    public static final String KEY = JolCraftParameterIds.BIOME;

    public BiomeCondition {
        if (target == null) throw new IllegalArgumentException("BiomeCondition target cannot be null");
    }

    @Override public String key() { return KEY; }

    @SuppressWarnings("deprecation")
    @Override
    public boolean matches(WorldContext ctx) {
        var pos = WorldAnchor.resolve(ctx);
        if (pos == null) return false;

        Holder<Biome> current = ctx.level().getBiome(pos);
        return target.value().map(current::is, current::is);
    }

    @Override
    public DataResult<BiomeCondition> validate() {
        return ParamValidations.wrap(this, target.validate(), KEY);
    }

    @Override public Codec<BiomeCondition> codec() { return CODEC; }
    @Override public StreamCodec<RegistryFriendlyByteBuf, BiomeCondition> streamCodec() { return STREAM_CODEC; }

    public static final Codec<BiomeCondition> CODEC =
            ParamCodecs.validated(
                    ParamCodecs.registryTargetValue(Registries.BIOME)
                            .xmap(BiomeCondition::new, BiomeCondition::target),
                    BiomeCondition::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, BiomeCondition> STREAM_CODEC =
            ParamCodecs.validatedStream(
                    ParamCodecs.registryTargetValueStream(Registries.BIOME)
                            .map(BiomeCondition::new, BiomeCondition::target),
                    BiomeCondition::validate
            );
}