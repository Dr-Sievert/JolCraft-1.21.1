package net.sievert.jolcraft.param.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ParamData<T> {

    Codec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    DataResult<T> validate();
}