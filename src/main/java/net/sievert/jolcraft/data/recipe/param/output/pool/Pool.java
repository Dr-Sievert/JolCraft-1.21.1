package net.sievert.jolcraft.data.recipe.param.output.pool;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.ResolvedOutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record Pool(
        IntRange rolls,
        Conditions conditions,
        List<PoolEntry> entries
) implements SelfValidating<Pool>, ConditionGate, RegistryIntrospectionSource, ResolvedOutputParam {

    private static final int MAX_TOTAL_OUTPUTS = 4096;
    private static final int MAX_ENTRIES_STREAM = 2048;

    private static final Set<String> RESERVED_KEYS = Set.of(
            JolCraftParameterIds.ROLLS,
            JolCraftParameterIds.CONDITIONS,
            JolCraftParameterIds.ENTRIES
    );

    private record FullRaw(
            IntRange rolls,
            Conditions conditions,
            List<PoolEntry> entries
    ) {}

    private static final Codec<FullRaw> FULL_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.ROLLS, IntRange.ONE)
                            .forGetter(FullRaw::rolls),
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(FullRaw::conditions),
                    PoolEntry.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.ENTRIES, List.of())
                            .forGetter(FullRaw::entries)
            ).apply(instance, FullRaw::new));

    private static final Codec<Either<List<PoolEntry>, FullRaw>> RAW_CODEC =
            Codec.either(PoolEntry.CODEC.listOf(), new Codec<>() {
                @Override
                public <T> DataResult<com.mojang.datafixers.util.Pair<FullRaw, T>> decode(DynamicOps<T> ops, T input) {
                    return Conditions.extractInlineConditions(ops, input, RESERVED_KEYS).flatMap(extracted ->
                            FULL_CODEC.decode(ops, extracted.strippedInput()).flatMap(pair ->
                                    Conditions.mergeExplicitAndInline(pair.getFirst().conditions(), extracted.conditions())
                                            .map(merged -> com.mojang.datafixers.util.Pair.of(
                                                    new FullRaw(
                                                            pair.getFirst().rolls(),
                                                            merged,
                                                            pair.getFirst().entries()
                                                    ),
                                                    pair.getSecond()
                                            ))
                            )
                    );
                }

                @Override
                public <T> DataResult<T> encode(FullRaw input, DynamicOps<T> ops, T prefix) {
                    T base = FULL_CODEC.encodeStart(ops, input).result().orElse(prefix);

                    if (input.conditions() == null || input.conditions().isEmpty()) {
                        return DataResult.success(base);
                    }

                    T noExplicit = ops.remove(base, JolCraftParameterIds.CONDITIONS);
                    DataResult<T> flattened = Conditions.encodeInlineConditions(ops, input.conditions(), noExplicit, RESERVED_KEYS);
                    return flattened.error().isPresent() ? DataResult.success(base) : flattened;
                }
            });

    public static final Codec<Pool> CODEC =
            ParamCodecContract.create(RAW_CODEC, Pool::fromRaw, Pool::toRaw);

    public static final StreamCodec<RegistryFriendlyByteBuf, Pool> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        IntRange.STREAM_CODEC.encode(buf, value.rolls());
                        Conditions.STREAM_CODEC.encode(buf, value.conditions());

                        List<PoolEntry> list = value.entries();
                        buf.writeVarInt(list.size());
                        for (PoolEntry e : list) {
                            PoolEntry.STREAM_CODEC.encode(buf, e);
                        }
                    },
                    buf -> {
                        IntRange rolls = IntRange.STREAM_CODEC.decode(buf);
                        Conditions conditions = Conditions.STREAM_CODEC.decode(buf);

                        int size = buf.readVarInt();
                        if (size < 0) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.ENTRIES + " size must be >= 0 (got " + size + ")"
                            );
                        }
                        if (size == 0) {
                            return new Pool(rolls, conditions, List.of());
                        }
                        if (size > MAX_ENTRIES_STREAM) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.ENTRIES + " size exceeds max " + MAX_ENTRIES_STREAM + " (got " + size + ")"
                            );
                        }

                        ArrayList<PoolEntry> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(PoolEntry.STREAM_CODEC.decode(buf));
                        }

                        return new Pool(rolls, conditions, list);
                    }
            );

    public Pool {
        rolls = rolls != null ? rolls : IntRange.ONE;
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        entries = sanitizeEntries(entries);
    }

    private static @NotNull DataResult<Pool> fromRaw(@NotNull Either<List<PoolEntry>, FullRaw> raw) {
        return DataResult.success(raw.map(
                list -> new Pool(IntRange.ONE, Conditions.EMPTY, list),
                full -> new Pool(full.rolls(), full.conditions(), full.entries())
        ));
    }

    private static @NotNull Either<List<PoolEntry>, FullRaw> toRaw(@NotNull Pool pool) {
        if (pool.isBareEntryList()) {
            return Either.left(pool.entries());
        }
        return Either.right(new FullRaw(pool.rolls(), pool.conditions(), pool.entries()));
    }

    private static @NotNull List<PoolEntry> sanitizeEntries(@Nullable List<PoolEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return List.of();
        }

        ArrayList<PoolEntry> safe = new ArrayList<>(entries.size());
        for (PoolEntry entry : entries) {
            if (entry != null) {
                safe.add(entry);
            }
        }
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditions;
    }

    public boolean isSingleRoll() {
        return rolls.isOne();
    }

    public boolean isBareEntryList() {
        if (!rolls.isOne()) return false;
        if (!conditions.isEmpty()) return false;
        for (PoolEntry entry : entries) {
            if (!entry.isBareOutput()) return false;
        }
        return true;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        if (entries.isEmpty()) return List.of();

        ArrayList<RegistryIntrospectionSource> src = new ArrayList<>(entries.size() + 1);
        if (!conditions.isEmpty()) {
            src.add(conditions);
        }
        src.addAll(entries);

        return RegistryIntrospectionSource.mergeByRegistry(src);
    }

    @Override
    public @NotNull DataResult<Pool> validate() {
        DataResult<IntRange> rv = IntRange.validateRange(rolls)
                .mapError(msg -> JolCraftParameterIds.ROLLS + " invalid: " + msg);

        return rv.error()
                .<DataResult<Pool>>map(e -> DataResult.error(e::message))
                .orElseGet(() -> {
                    DataResult<Conditions> cv = conditions.validate()
                            .mapError(msg -> JolCraftParameterIds.CONDITIONS + " invalid: " + msg);

                    return cv.error()
                            .<DataResult<Pool>>map(e -> DataResult.error(e::message))
                            .orElseGet(() -> {
                                for (int i = 0; i < entries.size(); i++) {
                                    PoolEntry pe = entries.get(i);
                                    int idx = i;
                                    DataResult<PoolEntry> ev = pe.validate()
                                            .mapError(msg -> JolCraftParameterIds.ENTRIES
                                                    + " invalid entry at index " + idx + ": " + msg);

                                    DataResult<Pool> err = ev.error()
                                            .<DataResult<Pool>>map(e -> DataResult.error(e::message))
                                            .orElse(null);

                                    if (err != null) {
                                        return err;
                                    }
                                }

                                return DataResult.success(this);
                            });
                });
    }

    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return generateResolved(ctx, null);
    }

    @Override
    public @NotNull List<Output> generateResolved(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver
    ) {
        if (!gatePasses(ctx)) return List.of();
        if (entries.isEmpty()) return List.of();

        RandomSource random = ctx.random();

        int poolRolls = Math.max(0, rolls.roll(random));
        if (poolRolls == 0) return List.of();
        if (poolRolls > MAX_TOTAL_OUTPUTS) poolRolls = MAX_TOTAL_OUTPUTS;

        ArrayList<Output> out = new ArrayList<>(Math.min(poolRolls, 64));

        for (int i = 0; i < poolRolls; i++) {
            if (out.size() >= MAX_TOTAL_OUTPUTS) break;

            PoolEntry chosen = pickWeighted(entries, ctx, random);
            if (chosen == null) break;

            int execs = rollEntryExecs(chosen, random);
            if (execs <= 0) continue;

            for (int r = 0; r < execs; r++) {
                if (out.size() >= MAX_TOTAL_OUTPUTS) break;

                List<Output> gen = chosen.generateResolved(ctx, resolver);
                if (gen.isEmpty()) continue;

                int remaining = MAX_TOTAL_OUTPUTS - out.size();
                if (gen.size() <= remaining) {
                    out.addAll(gen);
                } else {
                    out.addAll(gen.subList(0, remaining));
                    break;
                }
            }
        }

        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    private static int rollEntryExecs(
            @NotNull PoolEntry entry,
            @NotNull RandomSource random
    ) {
        int execs = Math.max(0, entry.rollExecutions(random));
        if (execs == 0) return 0;
        if (execs > MAX_TOTAL_OUTPUTS) execs = MAX_TOTAL_OUTPUTS;
        return execs;
    }

    private static @Nullable PoolEntry pickWeighted(
            @NotNull List<PoolEntry> list,
            @NotNull WorldContext ctx,
            @NotNull RandomSource random
    ) {
        ArrayList<PoolEntry> eligible = new ArrayList<>(list.size());
        long total = 0L;

        for (PoolEntry e : list) {
            if (!e.gatePasses(ctx)) continue;

            eligible.add(e);
            total += e.weight().value();

            if (total >= Integer.MAX_VALUE) {
                total = Integer.MAX_VALUE;
                break;
            }
        }

        if (eligible.isEmpty() || total <= 0L) {
            return null;
        }

        int roll = random.nextInt((int) total);
        long acc = 0L;

        for (PoolEntry e : eligible) {
            acc += e.weight().value();
            if ((long) roll < acc) {
                return e;
            }
        }

        return eligible.getLast();
    }
}