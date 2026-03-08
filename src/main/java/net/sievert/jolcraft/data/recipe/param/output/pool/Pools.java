package net.sievert.jolcraft.data.recipe.param.output.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Plural container for {@link Pool}.
 */
public record Pools(List<Pool> pools)
        implements SelfValidating<Pools>, RegistryIntrospectionSource, ResolvedOutputParam {

    private static final int MAX_TOTAL_OUTPUTS = 4096;
    private static final int MAX_POOLS_STREAM = 2048;

    private static final Codec<Pools> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Pool.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.POOLS, List.of())
                            .forGetter(Pools::poolsSafe)
            ).apply(instance, Pools::new));

    public static final Codec<Pools> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Pools> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        List<Pool> list = value.poolsSafe();
                        buf.writeVarInt(list.size());
                        for (Pool p : list) {
                            Pool.STREAM_CODEC.encode(buf, p);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size < 0) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.POOLS + " size must be >= 0 (got " + size + ")"
                            );
                        }
                        if (size == 0) return new Pools(List.of());
                        if (size > MAX_POOLS_STREAM) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.POOLS + " size exceeds max " + MAX_POOLS_STREAM + " (got " + size + ")"
                            );
                        }

                        ArrayList<Pool> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(Pool.STREAM_CODEC.decode(buf));
                        }

                        return new Pools(List.copyOf(list));
                    }
            );

    public Pools(List<Pool> pools) {
        this.pools = pools != null ? List.copyOf(pools) : List.of();
    }

    public List<Pool> poolsSafe() {
        return pools != null ? pools : List.of();
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        List<Pool> list = poolsSafe();
        if (list.isEmpty()) return List.of();

        ArrayList<RegistryIntrospectionSource> src = new ArrayList<>(list.size());
        for (Pool p : list) {
            if (p != null) src.add(p);
        }

        return src.isEmpty() ? List.of() : RegistryIntrospectionSource.mergeByRegistry(src);
    }

    @Override
    public @NotNull DataResult<Pools> validate() {
        if (pools == null) {
            return DataResult.error(() -> JolCraftParameterIds.POOLS + " cannot be null");
        }

        for (int i = 0; i < pools.size(); i++) {
            Pool p = pools.get(i);
            if (p == null) {
                int idx = i;
                return DataResult.error(() -> JolCraftParameterIds.POOLS + " contains null at index " + idx);
            }

            DataResult<Pool> pv = p.validate();
            Optional<DataResult.Error<Pool>> err = pv.error();
            if (err.isPresent()) {
                int idx = i;
                String msg = err.get().message();
                return DataResult.error(() ->
                        JolCraftParameterIds.POOLS + " invalid pool at index " + idx + ": " + msg
                );
            }
        }

        return DataResult.success(this);
    }

    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return generateResolved(ctx, null);
    }

    @Override
    public @NotNull List<Output> generateResolved(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver
    ) {
        List<Pool> list = poolsSafe();
        if (list.isEmpty()) return List.of();

        ArrayList<Output> out = new ArrayList<>(64);

        for (Pool p : list) {
            if (p == null) return List.of();

            List<Output> gen = p.generateResolved(ctx, resolver);
            if (gen.isEmpty()) continue;

            int remaining = MAX_TOTAL_OUTPUTS - out.size();
            if (remaining <= 0) break;

            if (gen.size() <= remaining) {
                out.addAll(gen);
            } else {
                out.addAll(gen.subList(0, remaining));
                break;
            }
        }

        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    public boolean anyParam(@NotNull Predicate<OutputParam> test) {
        List<Pool> ps = poolsSafe();
        for (Pool p : ps) {
            if (p == null) continue;
            List<PoolEntry> es = p.entries();
            if (es == null || es.isEmpty()) continue;

            for (PoolEntry e : es) {
                if (e == null) continue;
                OutputParam op = OutputParam.unwrap(e.output());
                if (op != null && test.test(op)) return true;
            }
        }
        return false;
    }
}