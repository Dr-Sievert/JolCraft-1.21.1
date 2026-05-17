package net.sievert.jolcraft.param.custom.entity.input.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.base.RegistryTarget;

public record EntityTarget(
        RegistryTarget<EntityType<?>> target
) implements ParamData<EntityTarget> {

    public EntityTarget {
        if (target == null) {
            throw new IllegalArgumentException("EntityTarget target cannot be null");
        }
    }

    @Override
    public DataResult<EntityTarget> validate() {
        return ParamValidations.wrap(this, target.validate(), JolCraftParameterIds.TARGET);
    }

    @Override
    public Codec<EntityTarget> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityTarget> streamCodec() {
        return STREAM_CODEC;
    }

    public static final Codec<EntityTarget> CODEC =
            ParamCodecs.validated(
                    ParamCodecs.registryTargetValue(Registries.ENTITY_TYPE)
                            .xmap(EntityTarget::new, EntityTarget::target),
                    EntityTarget::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityTarget> STREAM_CODEC =
            ParamCodecs.validatedStream(
                    ParamCodecs.registryTargetValueStream(Registries.ENTITY_TYPE)
                            .map(EntityTarget::new, EntityTarget::target),
                    EntityTarget::validate
            );
}