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
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.ResolvedOutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public record PoolEntry(
        OutputParam output,
        Conditions conditions,
        IntRange rolls,
        WeightParam weight
) implements SelfValidating<PoolEntry>, RegistryIntrospectionSource, ResolvedOutputParam {

    private static final Set<String> RESERVED_KEYS = Set.of(
            JolCraftParameterIds.OUTPUT,
            JolCraftParameterIds.WEIGHT,
            JolCraftParameterIds.ROLLS,
            JolCraftParameterIds.CONDITIONS
    );

    private static final Codec<PoolEntry> RAW_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<PoolEntry, T>> decode(DynamicOps<T> ops, T input) {
            boolean hasExplicitOutput = ops.getMap(input).result().map(map ->
                    map.get(ops.createString(JolCraftParameterIds.OUTPUT)) != null
            ).orElse(false);

            if (!hasExplicitOutput) {
                return OutputParam.CODEC.decode(ops, input)
                        .map(pair -> Pair.of(
                                new PoolEntry(pair.getFirst(), Conditions.EMPTY, IntRange.ONE, WeightParam.ONE),
                                pair.getSecond()
                        ));
            }

            return Conditions.extractInlineConditions(ops, input, RESERVED_KEYS).flatMap(extracted -> {
                T stripped = extracted.strippedInput();

                WeightParam weight = WeightParam.ONE;
                IntRange rolls = IntRange.ONE;
                Conditions explicitConditions = Conditions.EMPTY;
                T outputPayload = null;

                var mapValuesResult = ops.getMapValues(stripped);
                if (mapValuesResult.error().isPresent()) {
                    return DataResult.error(() -> "pool entry must be a map-like object");
                }

                for (Pair<T, T> pair : mapValuesResult.result().orElse(Stream.empty()).toList()) {
                    var keyResult = ops.getStringValue(pair.getFirst());
                    if (keyResult.result().isEmpty()) {
                        continue;
                    }

                    String key = keyResult.result().get();

                    if (JolCraftParameterIds.OUTPUT.equals(key)) {
                        outputPayload = pair.getSecond();
                        continue;
                    }

                    if (JolCraftParameterIds.WEIGHT.equals(key)) {
                        var w = WeightParam.CODEC.parse(ops, pair.getSecond());
                        if (w.error().isPresent()) {
                            return DataResult.error(() ->
                                    JolCraftParameterIds.WEIGHT + " invalid: " +
                                            w.error().map(DataResult.Error::message).orElse("invalid")
                            );
                        }
                        weight = w.result().orElse(WeightParam.ONE);
                        continue;
                    }

                    if (JolCraftParameterIds.ROLLS.equals(key)) {
                        var r = IntRange.CODEC.parse(ops, pair.getSecond());
                        if (r.error().isPresent()) {
                            return DataResult.error(() ->
                                    JolCraftParameterIds.ROLLS + " invalid: " +
                                            r.error().map(DataResult.Error::message).orElse("invalid")
                            );
                        }
                        rolls = r.result().orElse(IntRange.ONE);
                        continue;
                    }

                    if (JolCraftParameterIds.CONDITIONS.equals(key)) {
                        var c = Conditions.CODEC.parse(ops, pair.getSecond());
                        if (c.error().isPresent()) {
                            return DataResult.error(() ->
                                    JolCraftParameterIds.CONDITIONS + " invalid: " +
                                            c.error().map(DataResult.Error::message).orElse("invalid")
                            );
                        }
                        explicitConditions = c.result().orElse(Conditions.EMPTY);
                    }
                }

                if (outputPayload == null) {
                    return DataResult.error(() ->
                            "missing required field '" + JolCraftParameterIds.OUTPUT + "'"
                    );
                }

                DataResult<Conditions> merged =
                        Conditions.mergeExplicitAndInline(explicitConditions, extracted.conditions());

                if (merged.error().isPresent()) {
                    return DataResult.error(() ->
                            merged.error().map(DataResult.Error::message).orElse("invalid entry conditions")
                    );
                }

                final WeightParam finalWeight = weight;
                final IntRange finalRolls = rolls;
                final Conditions finalConditions = merged.result().orElse(Conditions.EMPTY);

                return OutputParam.CODEC.decode(ops, outputPayload)
                        .map(pair -> Pair.of(
                                new PoolEntry(pair.getFirst(), finalConditions, finalRolls, finalWeight),
                                pair.getSecond()
                        ));
            });
        }

        @Override
        public <T> DataResult<T> encode(PoolEntry entry, DynamicOps<T> ops, T prefix) {
            if (entry.isBareOutput()) {
                return OutputParam.CODEC.encode(entry.output(), ops, prefix);
            }

            T result = ops.createMap(Stream.empty());

            DataResult<T> encodedOutput = OutputParam.CODEC.encodeStart(ops, entry.output());
            if (encodedOutput.error().isPresent()) {
                return DataResult.error(() ->
                        encodedOutput.error().map(DataResult.Error::message).orElse("invalid output")
                );
            }

            result = ops.mergeToMap(
                    result,
                    ops.createString(JolCraftParameterIds.OUTPUT),
                    encodedOutput.result().orElseThrow()
            ).result().orElse(result);

            if (!WeightParam.ONE.equals(entry.weight())) {
                result = ops.mergeToMap(
                        result,
                        ops.createString(JolCraftParameterIds.WEIGHT),
                        WeightParam.CODEC.encodeStart(ops, entry.weight()).result().orElseThrow()
                ).result().orElse(result);
            }

            if (!entry.rolls().isOne()) {
                result = ops.mergeToMap(
                        result,
                        ops.createString(JolCraftParameterIds.ROLLS),
                        IntRange.CODEC.encodeStart(ops, entry.rolls()).result().orElseThrow()
                ).result().orElse(result);
            }

            if (entry.conditions().isEmpty()) {
                return DataResult.success(result);
            }

            DataResult<T> flattened = Conditions.encodeInlineConditions(
                    ops,
                    entry.conditions(),
                    result,
                    RESERVED_KEYS
            );

            if (flattened.error().isEmpty()) {
                return flattened;
            }

            result = ops.mergeToMap(
                    result,
                    ops.createString(JolCraftParameterIds.CONDITIONS),
                    Conditions.CODEC.encodeStart(ops, entry.conditions()).result().orElseThrow()
            ).result().orElse(result);

            return DataResult.success(result);
        }
    };

    public static final Codec<PoolEntry> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, PoolEntry> STREAM_CODEC =
            StreamCodec.composite(
                    OutputParam.STREAM_CODEC, PoolEntry::output,
                    Conditions.STREAM_CODEC, PoolEntry::conditions,
                    IntRange.STREAM_CODEC, PoolEntry::rolls,
                    WeightParam.STREAM_CODEC, PoolEntry::weight,
                    PoolEntry::new
            );

    public PoolEntry {
        if (output == null) {
            throw new IllegalArgumentException("pool entry output cannot be null");
        }
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        rolls = rolls != null ? rolls : IntRange.ONE;
        weight = weight != null ? weight : WeightParam.ONE;
    }

    public boolean isBareOutput() {
        if (!conditions.isEmpty()) return false;
        if (!rolls.isOne()) return false;
        if (!WeightParam.ONE.equals(weight)) return false;
        if (!output.conditions().isEmpty()) return false;
        if (!output.hooks().isEmpty()) return false;
        return OutputParam.unwrap(output) == output;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean gatePasses(@NotNull WorldContext ctx) {
        return conditions.test(ctx);
    }

    public int rollExecutions(@NotNull net.minecraft.util.RandomSource random) {
        return Math.max(0, rolls.roll(random));
    }

    @Override
    public @NotNull DataResult<PoolEntry> validate() {
        DataResult<?> ov = output.validate();
        if (ov.error().isPresent()) {
            String msg = ov.error().map(DataResult.Error::message).orElse("invalid");
            return DataResult.error(() -> JolCraftParameterIds.OUTPUT + " invalid: " + msg);
        }

        DataResult<Conditions> cv = conditions.validate();
        if (cv.error().isPresent()) {
            String msg = cv.error().map(DataResult.Error::message).orElse("invalid");
            return DataResult.error(() -> JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
        }

        DataResult<IntRange> rv = IntRange.validateRange(rolls);
        if (rv.error().isPresent()) {
            String msg = rv.error().map(DataResult.Error::message).orElse("invalid");
            return DataResult.error(() -> JolCraftParameterIds.ROLLS + " invalid: " + msg);
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
        ArrayList<RegistryIntrospectionSource> src = new ArrayList<>(2);
        if (!conditions.isEmpty()) {
            src.add(conditions);
        }
        if (output instanceof RegistryIntrospectionSource ris) {
            src.add(ris);
        }
        return src.isEmpty() ? List.of() : RegistryIntrospectionSource.mergeByRegistry(src);
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
        } catch (Exception e) {
            JolCraftLogs.error(JolCraftLogTags.RECIPE, "PoolEntry.generateResolved failed", e);
            return List.of();
        }
    }

    public boolean isSinglePick() {
        return rolls.isOne();
    }
}