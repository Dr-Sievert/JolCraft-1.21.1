package net.sievert.jolcraft.data.recipe.param.output.hook;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Hook(ResourceLocation id) implements SelfValidating<Hook> {

    private record Raw(ResourceLocation id) {}

    private static final Codec<Raw> RAW_CODEC =
            Codec.either(
                    ResourceLocation.CODEC,
                    RecordCodecBuilder.<Raw>create(instance -> instance.group(
                            ResourceLocation.CODEC
                                    .fieldOf(JolCraftParameterIds.ID)
                                    .forGetter(Raw::id)
                    ).apply(instance, Raw::new))
            ).xmap(
                    either -> either.map(Raw::new, raw -> raw),
                    raw -> Either.left(raw.id())
            );

    public static final Codec<Hook> CODEC =
            ParamCodecContract.<Raw, Hook>create(
                    RAW_CODEC,
                    raw -> DataResult.success(new Hook(raw.id())),
                    hook -> new Raw(hook.id())
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Hook> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> buf.writeResourceLocation(value.id()),
                    buf -> new Hook(buf.readResourceLocation())
            );

    public Hook {
        if (id == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.ID + "'");
        }
    }

    @Override
    public @NotNull DataResult<Hook> validate() {
        if (!Hooks.isRegistered(id)) {
            return DataResult.error(() -> Hooks.unknownHookError(id));
        }
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