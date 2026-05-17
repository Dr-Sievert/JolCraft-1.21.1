package net.sievert.jolcraft.param.custom.item.input.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.base.RegistryTarget;

public record ItemTarget(
        RegistryTarget<Item> target
) implements ParamData<ItemTarget> {

    public ItemTarget {
        if (target == null) {
            throw new IllegalArgumentException("ItemTarget target cannot be null");
        }
    }

    @Override
    public DataResult<ItemTarget> validate() {
        return ParamValidations.wrap(this, target.validate(), JolCraftParameterIds.TARGET);
    }

    @Override
    public Codec<ItemTarget> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemTarget> streamCodec() {
        return STREAM_CODEC;
    }

    public static final Codec<ItemTarget> CODEC =
            ParamCodecs.validated(
                    ParamCodecs.registryTargetValue(Registries.ITEM)
                            .xmap(ItemTarget::new, ItemTarget::target),
                    ItemTarget::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemTarget> STREAM_CODEC =
            ParamCodecs.validatedStream(
                    ParamCodecs.registryTargetValueStream(Registries.ITEM)
                            .map(ItemTarget::new, ItemTarget::target),
                    ItemTarget::validate
            );
}