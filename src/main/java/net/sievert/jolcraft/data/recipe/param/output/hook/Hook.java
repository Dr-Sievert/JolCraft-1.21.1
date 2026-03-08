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
                    (buf, v) -> {
                        ResourceLocation rl = v.id;
                        buf.writeBoolean(rl != null);
                        if (rl != null) buf.writeResourceLocation(rl);
                    },
                    buf -> {
                        boolean present = buf.readBoolean();
                        return present ? new Hook(buf.readResourceLocation()) : new Hook(null);
                    }
            );

    @Override
    public @NotNull DataResult<Hook> validate() {
        if (id == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.ID + "'");
        }
        return SelfValidating.ok(this);
    }

    public void apply(
            @NotNull WorldContext ctx,
            @NotNull ItemTransformSourceResolver resolver,
            @NotNull List<Output> outputs
    ) {
        if (id == null) return;
        Hooks.apply(id, ctx, resolver, outputs);
    }
}