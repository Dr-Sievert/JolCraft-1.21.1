package net.sievert.jolcraft.data.recipe.param.output.hook;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Hook(ResourceLocation id) implements SelfValidating<Hook> {

    private static final Codec<Hook> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC
                            .fieldOf(JolCraftParameterIds.ID)
                            .forGetter(Hook::id)
            ).apply(instance, Hook::new));

    public static final Codec<Hook> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Hook> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> buf.writeResourceLocation(v.id()),
                    buf -> new Hook(buf.readResourceLocation())
            );

    public Hook {
        if (id == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.ID + "'");
        }
    }

    @Override
    public @NotNull DataResult<Hook> validate() {
        return SelfValidating.ok(this);
    }

    public void apply(
            @NotNull WorldContext ctx,
            @NotNull ItemTransformSourceResolver resolver,
            @NotNull List<Output> outputs
    ) {
        Hooks.apply(id, ctx, resolver, outputs);
    }
}