package net.sievert.jolcraft.data.recipe.param.output.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputDispatch;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.ResolvedOutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.data.recipe.param.quantity.draw.DrawRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record PoolEntry(
        OutputParam output,
        DrawRule pool,
        WeightParam weight
) implements SelfValidating<PoolEntry>, RegistryIntrospectionSource, ResolvedOutputParam {

    private static final DrawRule DEFAULT_POOL =
            new DrawRule(IntRange.ONE, net.sievert.jolcraft.data.recipe.param.condition.Conditions.EMPTY);

    public PoolEntry {
        output = output != null ? output : OutputDispatch.None.INSTANCE;
        pool = pool != null ? pool : DEFAULT_POOL;
        weight = weight != null ? weight : WeightParam.ONE;
    }

    public boolean isSinglePick() {
        return pool.rolls().isOne();
    }

    private static final Codec<PoolEntry> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    OutputDispatch.CODEC.fieldOf(JolCraftParameterIds.OUTPUT).forGetter(PoolEntry::output),
                    DrawRule.CODEC.optionalFieldOf(JolCraftParameterIds.POOL, DEFAULT_POOL).forGetter(PoolEntry::pool),
                    WeightParam.CODEC.optionalFieldOf(JolCraftParameterIds.WEIGHT, WeightParam.ONE).forGetter(PoolEntry::weight)
            ).apply(instance, PoolEntry::new));

    public static final Codec<PoolEntry> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, PoolEntry> STREAM_CODEC =
            StreamCodec.composite(
                    OutputDispatch.STREAM_CODEC, PoolEntry::output,
                    DrawRule.STREAM_CODEC, PoolEntry::pool,
                    WeightParam.STREAM_CODEC, PoolEntry::weight,
                    PoolEntry::new
            );

    @Override
    public @NotNull List<Output> generateResolved(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver
    ) {
        OutputParam raw = output;
        OutputParam leaf = OutputParam.unwrap(raw);

        if (leaf instanceof ResolvedOutputParam resolved) {
            return resolved.generateResolved(ctx, resolver);
        }

        return raw.generate(ctx);
    }

    @Override
    public @NotNull DataResult<PoolEntry> validate() {
        OutputParam raw = output;
        OutputParam leaf = OutputParam.unwrap(raw);

        if (leaf instanceof OutputDispatch.None) {
            return DataResult.error(() -> JolCraftParameterIds.OUTPUT + " cannot be none");
        }

        {
            DataResult<?> ov = raw.validate();
            Optional<? extends DataResult.Error<?>> oErr = ov.error();
            if (oErr.isPresent()) {
                String msg = oErr.map(DataResult.Error::message).orElse("invalid");
                return DataResult.error(() -> JolCraftParameterIds.OUTPUT + " invalid: " + msg);
            }
        }

        {
            DataResult<WeightParam> wv = weight.validate();
            var wErr = wv.error();
            if (wErr.isPresent()) {
                return DataResult.error(() -> JolCraftParameterIds.WEIGHT + " invalid: " + wErr.get().message());
            }
        }

        {
            DataResult<DrawRule> pv = pool.validate();
            var pErr = pv.error();
            if (pErr.isPresent()) {
                return DataResult.error(() -> JolCraftParameterIds.POOL + " invalid: " + pErr.get().message());
            }
        }

        return DataResult.success(this);
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        ArrayList<RegistryIntrospectionSource> src = new ArrayList<>(3);

        src.add(pool);

        OutputParam raw = output;
        if (raw instanceof RegistryIntrospectionSource s) {
            src.add(s);
        }

        OutputParam leaf = OutputParam.unwrap(raw);
        if (leaf != raw && leaf instanceof RegistryIntrospectionSource s2) {
            src.add(s2);
        }

        if (src.size() == 1) {
            return src.getFirst().introspections();
        }

        return RegistryIntrospectionSource.mergeByRegistry(src);
    }
}