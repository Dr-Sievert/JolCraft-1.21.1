package net.sievert.jolcraft.data.recipe.param.output.base;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.data.recipe.param.output.pool.PoolEntry;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pools;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record Outputs(Pools pools)
        implements ResolvedOutputParam, SelfValidating<Outputs>, RegistryIntrospectionSource {

    public static final Pools EMPTY_POOLS = new Pools(List.of());
    public static final Outputs EMPTY = new Outputs(EMPTY_POOLS);

    public static @NotNull Outputs empty() {
        return EMPTY;
    }

    public static @NotNull Outputs wrapSingle(@NotNull OutputParam out) {
        PoolEntry entry = new PoolEntry(out, null, WeightParam.ONE);
        Pool pool = new Pool(IntRange.ONE, Conditions.EMPTY, List.of(entry));
        return new Outputs(new Pools(List.of(pool)));
    }

    private static @NotNull Outputs wrapBareList(@NotNull List<OutputParam> outputs) {
        if (outputs.isEmpty()) {
            return EMPTY;
        }

        ArrayList<PoolEntry> entries = new ArrayList<>(outputs.size());
        for (OutputParam output : outputs) {
            if (output != null) {
                entries.add(new PoolEntry(output, null, WeightParam.ONE));
            }
        }

        if (entries.isEmpty()) {
            return EMPTY;
        }

        return new Outputs(new Pools(List.of(
                new Pool(IntRange.ONE, Conditions.EMPTY, entries)
        )));
    }

    private boolean isSingleBareOutput() {
        List<Pool> ps = poolsSafe().pools();
        if (ps.size() != 1) return false;

        Pool pool = ps.getFirst();
        if (!pool.isBareEntryList()) return false;

        return pool.entries().size() == 1;
    }

    private boolean isSingleBarePoolList() {
        List<Pool> ps = poolsSafe().pools();
        return ps.size() == 1 && ps.getFirst().isBareEntryList();
    }

    private @Nullable OutputParam singleBareOutputOrNull() {
        if (!isSingleBareOutput()) return null;
        return poolsSafe().pools().getFirst().entries().getFirst().output();
    }

    private @NotNull List<OutputParam> bareOutputsOrEmpty() {
        if (!isSingleBarePoolList()) return List.of();

        List<PoolEntry> entries = poolsSafe().pools().getFirst().entries();
        if (entries.isEmpty()) return List.of();

        ArrayList<OutputParam> out = new ArrayList<>(entries.size());
        for (PoolEntry entry : entries) {
            OutputParam output = entry.output();
            if (output != null) out.add(output);
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    @SuppressWarnings("unchecked")
    public static @NotNull Codec<Outputs> codecShorthand(
            @NotNull Codec<? extends OutputParam> singleParamCodec
    ) {
        Codec<OutputParam> leaf = (Codec<OutputParam>) singleParamCodec;
        Codec<List<OutputParam>> leafList = leaf.listOf();

        return Codec.either(
                leaf,
                Codec.either(
                        leafList,
                        Codec.either(
                                Outputs.CODEC,
                                Codec.either(
                                        Pools.CODEC,
                                        Pool.CODEC.listOf()
                                )
                        )
                )
        ).xmap(
                either -> either.map(
                        Outputs::wrapSingle,
                        rest -> rest.map(
                                Outputs::wrapBareList,
                                outputsOrPools -> outputsOrPools.map(
                                        o -> o,
                                        poolsOrList -> poolsOrList.map(
                                                Outputs::new,
                                                list -> new Outputs(new Pools(list))
                                        )
                                )
                        )
                ),
                outputs -> {
                    OutputParam single = outputs.singleBareOutputOrNull();
                    if (single != null) {
                        return Either.left(single);
                    }

                    List<OutputParam> list = outputs.bareOutputsOrEmpty();
                    if (!list.isEmpty()) {
                        return Either.right(Either.left(list));
                    }

                    return Either.right(Either.right(Either.right(Either.right(outputs.poolsSafe().pools()))));
                }
        );
    }

    private static final Codec<Outputs> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Pools.CODEC
                            .optionalFieldOf(JolCraftParameterIds.POOLS, EMPTY_POOLS)
                            .forGetter(Outputs::poolsSafe)
            ).apply(instance, Outputs::new));

    public static final Codec<Outputs> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Outputs> STREAM_CODEC =
            StreamCodec.composite(
                    Pools.STREAM_CODEC, Outputs::poolsSafe,
                    Outputs::new
            );

    public Outputs(Pools pools) {
        this.pools = pools != null ? pools : EMPTY_POOLS;
    }

    private @NotNull Pools poolsSafe() {
        return pools != null ? pools : EMPTY_POOLS;
    }

    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return generateResolved(ctx, null);
    }

    @Override
    public @NotNull List<Output> generateResolved(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver
    ) {
        return poolsSafe().generateResolved(ctx, resolver);
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return poolsSafe().introspections();
    }

    @Override
    public @NotNull DataResult<Outputs> validate() {
        return poolsSafe().validate()
                .mapError(msg -> JolCraftParameterIds.POOLS + " invalid: " + msg)
                .map(ignored -> this);
    }

    public @NotNull Outputs merge(@NotNull Outputs other) {
        if (other == EMPTY || other.poolsSafe().pools().isEmpty()) return this;
        if (this == EMPTY || this.poolsSafe().pools().isEmpty()) return other;

        ArrayList<Pool> merged = new ArrayList<>(this.poolsSafe().pools().size() + other.poolsSafe().pools().size());
        merged.addAll(this.poolsSafe().pools());
        merged.addAll(other.poolsSafe().pools());

        return new Outputs(new Pools(merged));
    }

    public static boolean anyItemOutputRequiresInputSource(@NotNull Outputs outputs) {
        return outputs.pools().anyParam(op ->
                op instanceof ItemOutput io && io.transforms().requiresInputSource()
        );
    }
}