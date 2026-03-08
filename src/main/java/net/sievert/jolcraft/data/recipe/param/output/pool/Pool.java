package net.sievert.jolcraft.data.recipe.param.output.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
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
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.data.recipe.param.quantity.draw.DrawRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record Pool(
        IntRange rolls,
        Conditions conditions,
        List<PoolEntry> entries
) implements SelfValidating<Pool>, ConditionGate, RegistryIntrospectionSource, ResolvedOutputParam {

    private static final int MAX_TOTAL_OUTPUTS = 4096;
    private static final int MAX_ENTRIES_STREAM = 2048;


    @Override
    public @NotNull Conditions conditions() {
        return conditionsSafe();
    }

    public boolean isSingleRoll() {
        return rollsSafe().isOne();
    }

    private static final Codec<Pool> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.ROLLS, IntRange.ONE)
                            .forGetter(Pool::rollsSafe),

                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(Pool::conditionsSafe),

                    PoolEntry.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.ENTRIES, List.of())
                            .forGetter(Pool::entriesSafe)
            ).apply(instance, Pool::new));

    public static final Codec<Pool> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Pool> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        IntRange.STREAM_CODEC.encode(buf, value.rollsSafe());
                        Conditions.STREAM_CODEC.encode(buf, value.conditionsSafe());

                        List<PoolEntry> list = value.entriesSafe();
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

                        return new Pool(rolls, conditions, List.copyOf(list));
                    }
            );

    public Pool(IntRange rolls, Conditions conditions, List<PoolEntry> entries) {
        this.rolls = rolls != null ? rolls : IntRange.ONE;
        this.conditions = conditions != null ? conditions : Conditions.EMPTY;
        this.entries = entries != null ? List.copyOf(entries) : List.of();
    }

    private IntRange rollsSafe() {
        return rolls != null ? rolls : IntRange.ONE;
    }

    private Conditions conditionsSafe() {
        return conditions != null ? conditions : Conditions.EMPTY;
    }

    private List<PoolEntry> entriesSafe() {
        return entries != null ? entries : List.of();
    }


    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        ArrayList<RegistryIntrospectionSource> src = new ArrayList<>(1 + entriesSafe().size());

        src.add(conditionsSafe());

        for (PoolEntry e : entriesSafe()) {
            if (e != null) src.add(e);
        }

        return src.size() == 1
                ? src.getFirst().introspections()
                : RegistryIntrospectionSource.mergeByRegistry(src);
    }

    @Override
    public @NotNull DataResult<Pool> validate() {
        DataResult<IntRange> rv = IntRange.validateRange(rollsSafe())
                .mapError(msg -> JolCraftParameterIds.ROLLS + " invalid: " + msg);

        return rv.error()
                .<DataResult<Pool>>map(e -> DataResult.error(e::message))
                .orElseGet(() -> {

                    DataResult<Conditions> cv = conditionsSafe().validate()
                            .mapError(msg -> JolCraftParameterIds.CONDITIONS + " invalid: " + msg);

                    return cv.error()
                            .<DataResult<Pool>>map(e -> DataResult.error(e::message))
                            .orElseGet(() -> {

                                List<PoolEntry> list = entriesSafe();
                                for (int i = 0; i < list.size(); i++) {
                                    PoolEntry pe = list.get(i);
                                    if (pe == null) {
                                        int idx = i;
                                        return DataResult.error(() ->
                                                JolCraftParameterIds.ENTRIES + " contains null at index " + idx
                                        );
                                    }

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

        List<PoolEntry> list = entriesSafe();
        if (list.isEmpty()) return List.of();

        RandomSource random = ctx.random();

        int poolRolls = Math.max(0, rollsSafe().roll(random));
        if (poolRolls == 0) return List.of();
        if (poolRolls > MAX_TOTAL_OUTPUTS) poolRolls = MAX_TOTAL_OUTPUTS;

        ArrayList<Output> out = new ArrayList<>(Math.min(poolRolls, 64));

        for (int i = 0; i < poolRolls; i++) {
            if (out.size() >= MAX_TOTAL_OUTPUTS) break;

            PoolEntry chosen = pickWeighted(list, ctx, random);
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
        DrawRule rule = entry.pool();
        IntRange r = (rule != null && rule.rolls() != null) ? rule.rolls() : IntRange.ONE;

        int execs = Math.max(0, r.roll(random));
        if (execs == 0) return 0;
        if (execs > MAX_TOTAL_OUTPUTS) execs = MAX_TOTAL_OUTPUTS;

        return execs;
    }

    private static PoolEntry pickWeighted(
            @NotNull List<PoolEntry> list,
            @NotNull WorldContext ctx,
            @NotNull RandomSource random
    ) {
        long total = 0L;

        for (PoolEntry e : list) {
            if (e == null) return null;

            DrawRule rule = e.pool();
            Conditions entryCond = (rule != null) ? rule.conditions() : Conditions.EMPTY;
            if (!entryCond.test(ctx)) continue;

            WeightParam wParam = e.weight();
            int w = (wParam != null) ? wParam.safe() : 0;

            if (w > 0) {
                total += w;
                if (total >= (long) Integer.MAX_VALUE) {
                    total = Integer.MAX_VALUE;
                    break;
                }
            }
        }

        if (total <= 0L) return null;

        int roll = random.nextInt((int) total);
        long acc = 0L;

        for (PoolEntry e : list) {
            DrawRule rule = e.pool();
            Conditions entryCond = (rule != null) ? rule.conditions() : Conditions.EMPTY;
            if (!entryCond.test(ctx)) continue;

            WeightParam wParam = e.weight();
            int w = (wParam != null) ? wParam.safe() : 0;
            if (w <= 0) continue;

            acc += w;
            if ((long) roll < acc) {
                return e;
            }
        }

        return null;
    }
}