package net.sievert.jolcraft.world.recipe.param.input.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeRegistry;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.input.custom.entity.EntityInput;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.param.runtime.WorldContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Atomic input param contract.
 *
 * Strict polymorphic dispatch:
 * - no sentinels
 * - unknown JSON type ids fail decode
 * - unknown stream discriminators fail decode
 */
public interface InputParam<T extends InputParam<T, S>, S> extends SelfValidating<T> {

    ParamTypeRegistry<InputParam<?, ?>> REGISTRY =
            ParamTypeRegistry.<InputParam<?, ?>>builder()
                    .add(ItemInput.TYPE_DEF)
                    .add(EntityInput.TYPE_DEF)
                    .build();

    Codec<InputParam<?, ?>> CODEC =
            REGISTRY.codec(JolCraftParameterIds.TYPE, InputParam::typeId);

    StreamCodec<RegistryFriendlyByteBuf, InputParam<?, ?>> STREAM_CODEC =
            REGISTRY.streamCodec(InputParam::typeId);

    @NotNull ResourceLocation typeId();

    /**
     * Fail-closed matching against the provided context and explicit subject.
     *
     * Contract:
     * - ctx is never null.
     * - subject may be null: implementations MUST treat null as non-matching.
     * - No hidden subject sourcing.
     */
    boolean matches(@NotNull WorldContext ctx, @Nullable S subject);

    @Override
    @NotNull DataResult<T> validate();
}
