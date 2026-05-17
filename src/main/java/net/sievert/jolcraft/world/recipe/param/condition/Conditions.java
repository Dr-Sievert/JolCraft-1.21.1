package net.sievert.jolcraft.world.recipe.param.condition;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public record Conditions(List<Condition> conditions)
        implements SelfValidating<Conditions>, RegistryIntrospectionSource {

    public static final Conditions EMPTY = new Conditions(List.of());

    private static final int MAX_STREAM_SIZE = 2048;

    private static final Codec<Conditions> RAW_CODEC =
            Condition.CODEC.listOf().xmap(Conditions::new, Conditions::conditions);

    public static final Codec<Conditions> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Conditions> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        List<Condition> list = value.conditions();
                        buf.writeVarInt(list.size());
                        for (Condition condition : list) {
                            Condition.STREAM_CODEC.encode(buf, condition);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size < 0) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.CONDITIONS + " size must be >= 0 (got " + size + ")"
                            );
                        }
                        if (size == 0) {
                            return EMPTY;
                        }
                        if (size > MAX_STREAM_SIZE) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.CONDITIONS + " size exceeds max " + MAX_STREAM_SIZE + " (got " + size + ")"
                            );
                        }

                        ArrayList<Condition> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(Condition.STREAM_CODEC.decode(buf));
                        }
                        return new Conditions(list);
                    }
            );

    public Conditions(List<Condition> conditions) {
        this.conditions = sanitize(conditions);
    }

    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    public static @NotNull Conditions merge(
            @Nullable Conditions a,
            @Nullable Conditions b
    ) {
        if (a == null || a.isEmpty()) return b != null ? b : EMPTY;
        if (b == null || b.isEmpty()) return a;

        ArrayList<Condition> merged = new ArrayList<>(a.conditions().size() + b.conditions().size());
        merged.addAll(a.conditions());
        merged.addAll(b.conditions());
        return new Conditions(merged);
    }

    public static @NotNull DataResult<Conditions> mergeExplicitAndInline(
            @Nullable Conditions explicit,
            @Nullable Conditions inline
    ) {
        if ((explicit == null || explicit.isEmpty()) && (inline == null || inline.isEmpty())) {
            return DataResult.success(EMPTY);
        }
        if (explicit == null || explicit.isEmpty()) {
            return normalizeInlineCompatible(inline);
        }
        if (inline == null || inline.isEmpty()) {
            return normalizeInlineCompatible(explicit);
        }

        ArrayList<Condition> merged = new ArrayList<>(explicit.conditions().size() + inline.conditions().size());
        merged.addAll(explicit.conditions());
        merged.addAll(inline.conditions());
        return normalizeInlineCompatible(new Conditions(merged));
    }

    public @NotNull Conditions merged(@Nullable Conditions other) {
        return merge(this, other);
    }

    private static @Nullable String inlineKeyOrNull(@NotNull Condition condition) {
        try {
            return Condition.inlineKey(condition);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static @NotNull DataResult<Conditions> normalizeInlineCompatible(@NotNull Conditions input) {
        if (input.isEmpty()) {
            return DataResult.success(EMPTY);
        }

        ArrayList<Condition> normalized = new ArrayList<>(input.conditions().size());

        for (Condition condition : input.conditions()) {
            String key = inlineKeyOrNull(condition);
            if (key == null) {
                normalized.add(condition);
                continue;
            }

            Condition existing = null;
            for (Condition already : normalized) {
                String existingKey = inlineKeyOrNull(already);
                if (key.equals(existingKey)) {
                    existing = already;
                    break;
                }
            }

            if (existing == null) {
                normalized.add(condition);
                continue;
            }

            if (!existing.equals(condition)) {
                return DataResult.error(() ->
                        "conflicting condition definitions for inline key '" + key + "'"
                );
            }
        }

        return DataResult.success(normalized.isEmpty() ? EMPTY : new Conditions(normalized));
    }

    private static @NotNull List<Condition> sanitize(@Nullable List<Condition> in) {
        if (in == null || in.isEmpty()) {
            return List.of();
        }

        ArrayList<Condition> safe = new ArrayList<>(in.size());
        for (Condition condition : in) {
            if (condition != null) {
                safe.add(condition);
            }
        }
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    public record Extracted<T>(Conditions conditions, T strippedInput) {}

    public static <T> boolean hasInlineConditionKeys(
            @NotNull DynamicOps<T> ops,
            T input
    ) {
        var mapResult = ops.getMapValues(input);
        if (mapResult.result().isEmpty()) {
            return false;
        }

        for (Pair<T, T> pair : mapResult.result().orElse(Stream.empty()).toList()) {
            var keyResult = ops.getStringValue(pair.getFirst());
            if (keyResult.result().isPresent() && Condition.isInlineConditionKey(keyResult.result().get())) {
                return true;
            }
        }
        return false;
    }

    public static <T> @NotNull DataResult<Extracted<T>> extractInlineConditions(
            @NotNull DynamicOps<T> ops,
            T input,
            @NotNull Set<String> reservedKeys
    ) {
        var mapResult = ops.getMapValues(input);
        if (mapResult.result().isEmpty()) {
            return DataResult.success(new Extracted<>(EMPTY, input));
        }

        ArrayList<Condition> found = new ArrayList<>();
        ArrayList<Pair<T, T>> kept = new ArrayList<>();
        Set<String> seenInlineKeys = new LinkedHashSet<>();

        for (Pair<T, T> pair : mapResult.result().orElse(Stream.empty()).toList()) {
            var keyResult = ops.getStringValue(pair.getFirst());
            if (keyResult.result().isEmpty()) {
                kept.add(pair);
                continue;
            }

            String key = keyResult.result().get();

            if (reservedKeys.contains(key)) {
                kept.add(pair);
                continue;
            }

            if (!Condition.isInlineConditionKey(key)) {
                kept.add(pair);
                continue;
            }

            if (!seenInlineKeys.add(key)) {
                return DataResult.error(() -> "duplicate inline condition key '" + key + "'");
            }

            DataResult<Condition> decoded = Condition.decodeInlineField(ops, key, pair.getSecond());
            if (decoded.error().isPresent()) {
                String msg = decoded.error().map(DataResult.Error::message).orElse("invalid inline condition");
                return DataResult.error(() -> "invalid inline condition '" + key + "': " + msg);
            }

            found.add(decoded.result().orElseThrow());
        }

        T stripped = ops.createMap(kept.stream());

        Conditions extracted = found.isEmpty() ? EMPTY : new Conditions(found);
        return normalizeInlineCompatible(extracted).map(normalized ->
                new Extracted<>(normalized, stripped)
        );
    }

    public static <T> @NotNull DataResult<T> encodeInlineConditions(
            @NotNull DynamicOps<T> ops,
            @NotNull Conditions conditions,
            T base,
            @NotNull Set<String> disallowedKeys
    ) {
        if (conditions.isEmpty()) {
            return DataResult.success(base);
        }

        DataResult<Conditions> normalizedResult = normalizeInlineCompatible(conditions);
        if (normalizedResult.error().isPresent()) {
            return DataResult.error(() ->
                    normalizedResult.error().map(DataResult.Error::message).orElse("invalid inline conditions")
            );
        }

        Conditions normalized = normalizedResult.result().orElse(EMPTY);
        Set<String> seenKeys = new LinkedHashSet<>();
        T result = base;

        for (Condition condition : normalized.conditions()) {
            String key = Condition.inlineKey(condition);

            if (disallowedKeys.contains(key)) {
                return DataResult.error(() ->
                        "cannot inline condition '" + key + "' because that key is reserved in this scope");
            }
            if (!seenKeys.add(key)) {
                return DataResult.error(() ->
                        "cannot inline duplicate condition key '" + key + "'");
            }

            DataResult<T> encodedValue = Condition.encodeInlineField(ops, condition);
            if (encodedValue.error().isPresent()) {
                return DataResult.error(() ->
                        encodedValue.error().map(DataResult.Error::message).orElse("failed to encode inline condition")
                );
            }

            result = ops.mergeToMap(result, ops.createString(key), encodedValue.result().orElseThrow())
                    .result()
                    .orElse(result);
        }

        return DataResult.success(result);
    }

    @Override
    public @NotNull DataResult<Conditions> validate() {
        DataResult<Conditions> normalized = normalizeInlineCompatible(this);
        if (normalized.error().isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.CONDITIONS + " invalid: " +
                            normalized.error().map(DataResult.Error::message).orElse("invalid")
            );
        }

        for (int i = 0; i < conditions.size(); i++) {
            Condition condition = conditions.get(i);
            DataResult<Condition> child = condition.validate();
            var err = child.error();
            if (err.isPresent()) {
                final int idx = i;
                final String msg = err.get().message();
                return DataResult.error(() ->
                        JolCraftParameterIds.CONDITIONS + " invalid child at index " + idx + ": " + msg
                );
            }
        }

        return DataResult.success(this);
    }

    public boolean test(@NotNull WorldContext ctx) {
        if (conditions.isEmpty()) return true;

        for (Condition condition : conditions) {
            if (!condition.test(ctx)) return false;
        }
        return true;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return conditions.isEmpty()
                ? List.of()
                : RegistryIntrospectionSource.mergeByRegistry(conditions);
    }
}