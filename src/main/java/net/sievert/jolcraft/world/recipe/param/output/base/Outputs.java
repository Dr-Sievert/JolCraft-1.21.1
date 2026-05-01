package net.sievert.jolcraft.world.recipe.param.output.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.condition.Condition;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.world.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.world.recipe.param.output.pool.PoolEntry;
import net.sievert.jolcraft.world.recipe.param.output.pool.Pools;
import net.sievert.jolcraft.world.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.world.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record Outputs(Conditions conditions, Pools pools)
        implements ResolvedOutputParam, SelfValidating<Outputs>, RegistryIntrospectionSource {

    public static final Pools EMPTY_POOLS = new Pools(List.of());
    public static final Outputs EMPTY = new Outputs(Conditions.EMPTY, EMPTY_POOLS);

    private static final Set<String> RESERVED_KEYS = Set.of(
            JolCraftParameterIds.CONDITIONS,
            JolCraftParameterIds.POOLS
    );

    private record RawObject(
            Conditions conditions,
            Pools pools
    ) {}

    private static final Codec<RawObject> RAW_OBJECT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(RawObject::conditions),
                    Pools.CODEC
                            .optionalFieldOf(JolCraftParameterIds.POOLS, EMPTY_POOLS)
                            .forGetter(RawObject::pools)
            ).apply(instance, RawObject::new));

    private static final Codec<Outputs> RAW_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<com.mojang.datafixers.util.Pair<Outputs, T>> decode(DynamicOps<T> ops, T input) {
            return Conditions.extractInlineConditions(ops, input, RESERVED_KEYS).flatMap(extracted ->
                    RAW_OBJECT_CODEC.decode(ops, extracted.strippedInput()).flatMap(pair ->
                            Conditions.mergeExplicitAndInline(pair.getFirst().conditions(), extracted.conditions())
                                    .map(merged -> com.mojang.datafixers.util.Pair.of(
                                            new Outputs(merged, pair.getFirst().pools()),
                                            pair.getSecond()
                                    ))
                    )
            );
        }

        @Override
        public <T> DataResult<T> encode(Outputs input, DynamicOps<T> ops, T prefix) {
            T base = RAW_OBJECT_CODEC.encodeStart(
                    ops,
                    new RawObject(
                            input.conditions().isEmpty() ? Conditions.EMPTY : input.conditions(),
                            input.poolsSafe()
                    )
            ).result().orElse(prefix);

            if (input.conditions().isEmpty()) {
                return DataResult.success(base);
            }

            T noExplicit = ops.remove(base, JolCraftParameterIds.CONDITIONS);
            DataResult<T> flattened = Conditions.encodeInlineConditions(ops, input.conditions(), noExplicit, RESERVED_KEYS);
            return flattened.error().isPresent()
                    ? DataResult.success(base)
                    : flattened;
        }
    };

    public static final Codec<Outputs> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Outputs> STREAM_CODEC =
            StreamCodec.composite(
                    Conditions.STREAM_CODEC, Outputs::conditions,
                    Pools.STREAM_CODEC, Outputs::poolsSafe,
                    Outputs::new
            );

    public static @NotNull Outputs empty() {
        return EMPTY;
    }

    public static @NotNull Outputs wrapSingle(@NotNull OutputParam out) {
        PoolEntry entry = new PoolEntry(out, Conditions.EMPTY, IntRange.ONE, WeightParam.ONE);
        Pool pool = new Pool(IntRange.ONE, Conditions.EMPTY, List.of(entry));
        return new Outputs(Conditions.EMPTY, new Pools(List.of(pool)));
    }

    private static @NotNull Outputs wrapBareList(@NotNull List<OutputParam> outputs) {
        if (outputs.isEmpty()) {
            return EMPTY;
        }

        ArrayList<PoolEntry> entries = new ArrayList<>(outputs.size());
        for (OutputParam output : outputs) {
            if (output != null) {
                entries.add(new PoolEntry(output, Conditions.EMPTY, IntRange.ONE, WeightParam.ONE));
            }
        }

        if (entries.isEmpty()) {
            return EMPTY;
        }

        return new Outputs(
                Conditions.EMPTY,
                new Pools(List.of(new Pool(IntRange.ONE, Conditions.EMPTY, entries)))
        );
    }

    private boolean isSingleBareOutput() {
        if (hasTopLevelConditions()) {
            return false;
        }

        List<Pool> ps = poolsSafe().pools();
        if (ps.size() != 1) return false;

        Pool pool = ps.getFirst();
        if (!pool.isBareEntryList()) return false;

        return pool.entries().size() == 1;
    }

    private boolean isSingleBarePoolList() {
        if (hasTopLevelConditions()) {
            return false;
        }

        List<Pool> ps = poolsSafe().pools();
        return ps.size() == 1 && ps.getFirst().isBareEntryList();
    }

    private boolean hasTopLevelConditions() {
        return !conditions().isEmpty();
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
            if (output != null) {
                out.add(output);
            }
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    private static <T> boolean looksExplicitOutputsObject(@NotNull DynamicOps<T> ops, T input) {
        return ops.getMap(input).result().map(map ->
                map.get(ops.createString(JolCraftParameterIds.CONDITIONS)) != null ||
                        map.get(ops.createString(JolCraftParameterIds.POOLS)) != null
        ).orElse(false);
    }

    @SuppressWarnings("unchecked")
    public static @NotNull Codec<Outputs> codecShorthand(
            @NotNull Codec<? extends OutputParam> singleParamCodec
    ) {
        Codec<OutputParam> leaf = (Codec<OutputParam>) singleParamCodec;
        Codec<List<OutputParam>> leafList = leaf.listOf();

        return new Codec<>() {
            @Override
            public <T> DataResult<com.mojang.datafixers.util.Pair<Outputs, T>> decode(DynamicOps<T> ops, T input) {
                boolean mapLike = ops.getMap(input).result().isPresent();

                if (mapLike && looksExplicitOutputsObject(ops, input)) {
                    return Outputs.CODEC.decode(ops, input);
                }

                DataResult<com.mojang.datafixers.util.Pair<OutputParam, T>> directLeaf = leaf.decode(ops, input);
                if (directLeaf.result().isPresent()) {
                    var pair = directLeaf.result().orElseThrow();
                    return DataResult.success(com.mojang.datafixers.util.Pair.of(
                            Outputs.wrapSingle(pair.getFirst()),
                            pair.getSecond()
                    ));
                }

                return Codec.either(
                        leafList,
                        Codec.either(
                                Outputs.CODEC,
                                Codec.either(
                                        Pools.CODEC,
                                        Pool.CODEC.listOf()
                                )
                        )
                ).decode(ops, input).map(pair -> com.mojang.datafixers.util.Pair.of(
                        pair.getFirst().map(
                                Outputs::wrapBareList,
                                outputsOrPools -> outputsOrPools.map(
                                        o -> o,
                                        poolsOrList -> poolsOrList.map(
                                                pools -> new Outputs(Conditions.EMPTY, pools),
                                                list -> new Outputs(Conditions.EMPTY, new Pools(list))
                                        )
                                )
                        ),
                        pair.getSecond()
                ));
            }

            @Override
            public <T> DataResult<T> encode(Outputs outputs, DynamicOps<T> ops, T prefix) {
                if (outputs.hasTopLevelConditions()) {
                    return Outputs.CODEC.encode(outputs, ops, prefix);
                }

                OutputParam single = outputs.singleBareOutputOrNull();
                if (single != null) {
                    return leaf.encode(single, ops, prefix);
                }

                List<OutputParam> list = outputs.bareOutputsOrEmpty();
                if (!list.isEmpty()) {
                    return leafList.encode(list, ops, prefix);
                }

                return Pool.CODEC.listOf().encode(outputs.poolsSafe().pools(), ops, prefix);
            }
        };
    }

    public Outputs(Conditions conditions, Pools pools) {
        this.conditions = conditions != null ? conditions : Conditions.EMPTY;
        this.pools = pools != null ? pools : EMPTY_POOLS;
    }

    private @NotNull Pools poolsSafe() {
        return pools != null ? pools : EMPTY_POOLS;
    }

    public boolean isEmpty() {
        return poolsSafe().pools().isEmpty();
    }

    public int poolCount() {
        return poolsSafe().pools().size();
    }

    public int entryCount() {
        int total = 0;
        for (Pool pool : poolsSafe().pools()) {
            total += pool.entries().size();
        }
        return total;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasAnyEntries() {
        return entryCount() > 0;
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
            if (!conditions().test(ctx)) {
                return List.of();
            }

            return poolsSafe().generateResolved(ctx, resolver);
        } catch (Exception e) {
            JolCraftLogs.error(JolCraftLogTags.RECIPE, "Outputs.generateResolved failed", e);
            return List.of();
        }
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return RegistryIntrospectionSource.mergeByRegistry(List.of(
                conditions(),
                poolsSafe()
        ));
    }

    @Override
    public @NotNull DataResult<Outputs> validate() {
        return conditions().validate()
                .mapError(msg -> JolCraftParameterIds.CONDITIONS + " invalid: " + msg)
                .flatMap(ignored -> poolsSafe().validate()
                        .mapError(msg -> JolCraftParameterIds.POOLS + " invalid: " + msg))
                .map(ignored -> this);
    }

    public @NotNull Outputs merge(@NotNull Outputs other) {
        if (other == EMPTY || (!other.hasTopLevelConditions() && other.poolsSafe().pools().isEmpty())) {
            return this;
        }
        if (this == EMPTY || (!this.hasTopLevelConditions() && this.poolsSafe().pools().isEmpty())) {
            return other;
        }

        ArrayList<Condition> mergedConditions =
                new ArrayList<>(this.conditions().conditions().size() + other.conditions().conditions().size());
        mergedConditions.addAll(this.conditions().conditions());
        mergedConditions.addAll(other.conditions().conditions());

        ArrayList<Pool> mergedPools =
                new ArrayList<>(this.poolsSafe().pools().size() + other.poolsSafe().pools().size());
        mergedPools.addAll(this.poolsSafe().pools());
        mergedPools.addAll(other.poolsSafe().pools());

        return new Outputs(new Conditions(mergedConditions), new Pools(mergedPools));
    }

    public static boolean anyItemOutputRequiresInputSource(@NotNull Outputs outputs) {
        return outputs.pools().anyParam(op ->
                op instanceof ItemOutput io && io.transforms().requiresInputSource()
        );
    }
}