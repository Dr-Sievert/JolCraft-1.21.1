package net.sievert.jolcraft.data.recipe.param.output.pool;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.ResolvedOutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.data.recipe.param.quantity.draw.DrawRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record PoolEntry(
        OutputParam output,
        @Nullable DrawRule pool,
        @Nullable WeightParam weight
) implements SelfValidating<PoolEntry>, RegistryIntrospectionSource, ResolvedOutputParam {

    private record FullRaw(
            OutputParam output,
            Optional<DrawRule> pool,
            Optional<WeightParam> weight
    ) {}

    private static final Codec<FullRaw> FULL_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    OutputParam.CODEC
                            .fieldOf(JolCraftParameterIds.OUTPUT)
                            .forGetter(FullRaw::output),

                    DrawRule.CODEC
                            .optionalFieldOf(JolCraftParameterIds.POOL)
                            .forGetter(FullRaw::pool),

                    WeightParam.CODEC
                            .optionalFieldOf(JolCraftParameterIds.WEIGHT)
                            .forGetter(FullRaw::weight)
            ).apply(instance, FullRaw::new));

    private static final Codec<PoolEntry> RAW_CODEC =
            Codec.either(OutputParam.CODEC, FULL_CODEC).xmap(
                    either -> either.map(
                            output -> new PoolEntry(output, null, null),
                            full -> new PoolEntry(
                                    full.output(),
                                    full.pool().orElse(null),
                                    full.weight().orElse(null)
                            )
                    ),
                    entry -> entry.isBareOutput()
                            ? Either.left(entry.output())
                            : Either.right(new FullRaw(
                            entry.output(),
                            Optional.ofNullable(entry.pool()),
                            Optional.ofNullable(entry.weight())
                    ))
            );

    public static final Codec<PoolEntry> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, PoolEntry> STREAM_CODEC =
            StreamCodec.composite(
                    OutputParam.STREAM_CODEC, PoolEntry::output,
                    DrawRule.STREAM_CODEC.apply(ByteBufCodecs::optional), e -> Optional.ofNullable(e.pool()),
                    WeightParam.STREAM_CODEC.apply(ByteBufCodecs::optional), e -> Optional.ofNullable(e.weight()),
                    (output, pool, weight) -> new PoolEntry(output, pool.orElse(null), weight.orElse(null))
            );

    public PoolEntry {
        if (output == null) {
            throw new IllegalArgumentException("pool entry output cannot be null");
        }
    }

    public boolean isBareOutput() {
        return pool == null && weight == null;
    }

    @Override
    public @NotNull DataResult<PoolEntry> validate() {
        DataResult<?> ov;
        try {
            ov = output.validate();
        } catch (Exception e) {
            return DataResult.error(() -> JolCraftParameterIds.OUTPUT + " validation threw: " + e.getMessage());
        }

        if (ov.error().isPresent()) {
            String msg = ov.error().map(DataResult.Error::message).orElse("invalid");
            return DataResult.error(() -> JolCraftParameterIds.OUTPUT + " invalid: " + msg);
        }

        if (pool != null) {
            DataResult<?> pv;
            try {
                pv = pool.validate();
            } catch (Exception e) {
                return DataResult.error(() -> JolCraftParameterIds.POOL + " validation threw: " + e.getMessage());
            }

            if (pv.error().isPresent()) {
                String msg = pv.error().map(DataResult.Error::message).orElse("invalid");
                return DataResult.error(() -> JolCraftParameterIds.POOL + " invalid: " + msg);
            }
        }

        if (weight != null) {
            DataResult<?> wv;
            try {
                wv = weight.validate();
            } catch (Exception e) {
                return DataResult.error(() -> JolCraftParameterIds.WEIGHT + " validation threw: " + e.getMessage());
            }

            if (wv.error().isPresent()) {
                String msg = wv.error().map(DataResult.Error::message).orElse("invalid");
                return DataResult.error(() -> JolCraftParameterIds.WEIGHT + " invalid: " + msg);
            }
        }

        return DataResult.success(this);
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return output instanceof RegistryIntrospectionSource src ? src.introspections() : List.of();
    }

    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return generateResolved(ctx, null);
    }

    @Override
    public @NotNull List<Output> generateResolved(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver
    ) {
        try {
            return output instanceof ResolvedOutputParam resolved
                    ? resolved.generateResolved(ctx, resolver)
                    : output.generate(ctx);
        } catch (Exception ignored) {
            return List.of();
        }
    }

    public boolean isSinglePick() {
        if (pool == null) {
            return true;
        }

        IntRange rolls = pool.rolls();
        return rolls == null || rolls.isOne();
    }
}