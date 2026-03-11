package net.sievert.jolcraft.data.recipe.param.output.pool;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import net.sievert.jolcraft.data.recipe.param.quantity.DrawRule;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record PoolEntry(
        OutputParam output,
        @Nullable DrawRule pool,
        WeightParam weight
) implements SelfValidating<PoolEntry>, RegistryIntrospectionSource, ResolvedOutputParam {

    private static final Codec<PoolEntry> RAW_CODEC = new Codec<>() {

        @Override
        public <T> DataResult<Pair<PoolEntry, T>> decode(DynamicOps<T> ops, T input) {

            WeightParam weight = WeightParam.ONE;
            DrawRule pool = null;

            var mapResult = ops.getMap(input);
            if (mapResult.result().isPresent()) {

                var map = mapResult.result().get();

                T weightNode = map.get(ops.createString(JolCraftParameterIds.WEIGHT));
                if (weightNode != null) {
                    var w = WeightParam.CODEC.parse(ops, weightNode);
                    if (w.result().isPresent()) {
                        weight = w.result().get();
                    }
                }

                T poolNode = map.get(ops.createString(JolCraftParameterIds.POOL));
                if (poolNode != null) {
                    var p = DrawRule.CODEC.parse(ops, poolNode);
                    if (p.result().isPresent()) {
                        pool = p.result().get();
                    }
                }
            }

            final WeightParam finalWeight = weight;
            final DrawRule finalPool = pool;

            return OutputParam.CODEC.decode(ops, input)
                    .map(pair -> Pair.of(
                            new PoolEntry(pair.getFirst(), finalPool, finalWeight),
                            pair.getSecond()
                    ));
        }

        @Override
        public <T> DataResult<T> encode(PoolEntry entry, DynamicOps<T> ops, T prefix) {

            if (entry.isBareOutput()) {
                return OutputParam.CODEC.encode(entry.output(), ops, prefix);
            }

            return OutputParam.CODEC.encode(entry.output(), ops, prefix)
                    .flatMap(base -> {

                        T result = base;

                        if (entry.pool() != null) {
                            result = ops.mergeToMap(
                                    result,
                                    ops.createString(JolCraftParameterIds.POOL),
                                    DrawRule.CODEC.encodeStart(ops, entry.pool())
                                            .result()
                                            .orElseThrow()
                            ).result().orElse(result);
                        }

                        if (!WeightParam.ONE.equals(entry.weight())) {
                            result = ops.mergeToMap(
                                    result,
                                    ops.createString(JolCraftParameterIds.WEIGHT),
                                    WeightParam.CODEC.encodeStart(ops, entry.weight())
                                            .result()
                                            .orElseThrow()
                            ).result().orElse(result);
                        }

                        return DataResult.success(result);
                    });
        }
    };

    public static final Codec<PoolEntry> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, PoolEntry> STREAM_CODEC =
            StreamCodec.of(
                    (buf, entry) -> {
                        OutputParam.STREAM_CODEC.encode(buf, entry.output());
                        buf.writeBoolean(entry.pool() != null);
                        if (entry.pool() != null) {
                            DrawRule.STREAM_CODEC.encode(buf, entry.pool());
                        }
                        WeightParam.STREAM_CODEC.encode(buf, entry.weight());
                    },
                    buf -> {
                        OutputParam output = OutputParam.STREAM_CODEC.decode(buf);
                        DrawRule pool = buf.readBoolean() ? DrawRule.STREAM_CODEC.decode(buf) : null;
                        WeightParam weight = WeightParam.STREAM_CODEC.decode(buf);
                        return new PoolEntry(output, pool, weight);
                    }
            );

    public PoolEntry {
        if (output == null) {
            throw new IllegalArgumentException("pool entry output cannot be null");
        }
        weight = weight != null ? weight : WeightParam.ONE;
    }

    public boolean isBareOutput() {
        return pool == null && WeightParam.ONE.equals(weight);
    }

    @Override
    public @NotNull DataResult<PoolEntry> validate() {

        DataResult<?> ov = output.validate();
        if (ov.error().isPresent()) {
            String msg = ov.error().map(DataResult.Error::message).orElse("invalid");
            return DataResult.error(() -> JolCraftParameterIds.OUTPUT + " invalid: " + msg);
        }

        if (pool != null) {
            DataResult<?> pv = pool.validate();
            if (pv.error().isPresent()) {
                String msg = pv.error().map(DataResult.Error::message).orElse("invalid");
                return DataResult.error(() -> JolCraftParameterIds.POOL + " invalid: " + msg);
            }
        }

        DataResult<?> wv = weight.validate();
        if (wv.error().isPresent()) {
            String msg = wv.error().map(DataResult.Error::message).orElse("invalid");
            return DataResult.error(() -> JolCraftParameterIds.WEIGHT + " invalid: " + msg);
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