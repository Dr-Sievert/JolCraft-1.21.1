package net.sievert.jolcraft.world.recipe.param.base;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Static type definition for one concrete variant in a polymorphic param family.
 *
 * Owns:
 * - stable type name for JSON dispatch
 * - stable byte discriminator for stream dispatch
 * - subtype codec
 * - subtype stream codec
 *
 * Contains only real registered variants.
 */
public record ParamTypeDef<T>(
        @NotNull ResourceLocation typeId,
        byte discriminator,
        @NotNull Codec<? extends T> codec,
        @NotNull StreamCodec<RegistryFriendlyByteBuf, ? extends T> streamCodec
) {}