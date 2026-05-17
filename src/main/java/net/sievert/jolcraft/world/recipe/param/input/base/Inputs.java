package net.sievert.jolcraft.world.recipe.param.input.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Polymorphic wrapper for heterogeneous {@link InputParam} entries.
 *
 * Gating:
 * - Top-level {@link #conditions()} gates the entire input block.
 * - Each {@link Entry} may have its own {@link Conditions} gate.
 *
 * Matching semantics:
 * - ANY semantics (OR-group): at least one gated entry must match.
 * - No entries => false.
 */
public record Inputs<S>(
        Conditions conditions,
        List<Entry<S>> entries
) implements SelfValidating<Inputs<S>>, ConditionGate, RegistryIntrospectionSource {

    public static final Inputs<Object> EMPTY = new Inputs<>(Conditions.EMPTY, List.of());
    private static final int MAX_ENTRIES_STREAM = 2048;

    public record Entry<S>(Conditions conditions, InputParam<?, S> param)
            implements SelfValidating<Entry<S>>, RegistryIntrospectionSource {

        @SuppressWarnings("unchecked")
        private static <S> Codec<Entry<S>> rawCodec() {
            Codec<Entry<?>> built = RecordCodecBuilder.create(inst -> inst.group(
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(v -> v.conditions),
                    InputParam.CODEC
                            .fieldOf(JolCraftParameterIds.PARAMETER)
                            .forGetter(v -> (InputParam<?, ?>) v.param)
            ).apply(inst, (c, p) -> new Entry<>(c, (InputParam<?, ?>) p)));

            return (Codec<Entry<S>>) (Codec<?>) built;
        }

        public static <S> Codec<Entry<S>> codec() {
            return ParamCodecs.validated(rawCodec());
        }

        @SuppressWarnings("unchecked")
        public static <S> StreamCodec<RegistryFriendlyByteBuf, Entry<S>> streamCodec() {
            return StreamCodec.of(
                    (buf, value) -> {
                        Conditions.STREAM_CODEC.encode(buf, value.conditions);
                        InputParam.STREAM_CODEC.encode(buf, (InputParam<?, ?>) value.param);
                    },
                    buf -> new Entry<>(
                            Conditions.STREAM_CODEC.decode(buf),
                            (InputParam<?, S>) InputParam.STREAM_CODEC.decode(buf)
                    )
            );
        }

        public Entry {
            conditions = conditions != null ? conditions : Conditions.EMPTY;
        }

        @Override
        public @NotNull List<RegistryIntrospection> introspections() {
            if (param instanceof RegistryIntrospectionSource ris) {
                return ris.introspections();
            }
            return List.of();
        }

        @Override
        public @NotNull DataResult<Entry<S>> validate() {
            if (param == null) {
                return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.PARAMETER + "'");
            }

            DataResult<Conditions> cv = conditions.validate();
            if (cv.error().isPresent()) {
                String msg = cv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
            }

            DataResult<?> pv = param.validate();
            if (pv.error().isPresent()) {
                String msg = pv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.PARAMETER + " invalid: " + msg);
            }

            return SelfValidating.ok(this);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <S> Codec<Inputs<S>> rawCodec() {
        Codec<List<Entry<?>>> entryListCodec = (Codec) Entry.codec().listOf();

        Codec<Inputs<?>> built = RecordCodecBuilder.create(inst -> inst.group(
                Conditions.CODEC
                        .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                        .forGetter(v -> v.conditions),
                entryListCodec
                        .optionalFieldOf(JolCraftParameterIds.ENTRIES, List.of())
                        .forGetter(v -> (List<Entry<?>>) (List<?>) v.entries)
        ).apply(inst, (Conditions c, List<Entry<?>> e) -> new Inputs<>(c, (List) e)));

        return (Codec<Inputs<S>>) (Codec<?>) built;
    }

    public static <S> Codec<Inputs<S>> codec() {
        return ParamCodecs.validated(rawCodec());
    }

    public static <S> StreamCodec<RegistryFriendlyByteBuf, Inputs<S>> streamCodec() {
        return StreamCodec.of(
                (buf, value) -> {
                    Conditions.STREAM_CODEC.encode(buf, value.conditions);
                    buf.writeVarInt(value.entries.size());

                    StreamCodec<RegistryFriendlyByteBuf, Entry<S>> esc = Entry.streamCodec();
                    for (Entry<S> entry : value.entries) {
                        esc.encode(buf, entry);
                    }
                },
                buf -> {
                    Conditions conditions = Conditions.STREAM_CODEC.decode(buf);
                    int size = buf.readVarInt();

                    if (size < 0) {
                        throw new IllegalArgumentException(
                                JolCraftParameterIds.ENTRIES + " size must be >= 0 (got " + size + ")"
                        );
                    }
                    if (size == 0) {
                        return new Inputs<>(conditions, List.of());
                    }
                    if (size > MAX_ENTRIES_STREAM) {
                        throw new IllegalArgumentException(
                                JolCraftParameterIds.ENTRIES + " size exceeds max " + MAX_ENTRIES_STREAM + " (got " + size + ")"
                        );
                    }

                    ArrayList<Entry<S>> list = new ArrayList<>(size);
                    StreamCodec<RegistryFriendlyByteBuf, Entry<S>> esc = Entry.streamCodec();
                    for (int i = 0; i < size; i++) {
                        list.add(esc.decode(buf));
                    }
                    return new Inputs<>(conditions, list);
                }
        );
    }

    public Inputs {
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        entries = sanitizeList(entries);
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        if (entries.isEmpty()) return List.of();
        return RegistryIntrospectionSource.mergeByRegistry(entries);
    }

    public boolean matches(@NotNull WorldContext ctx, @Nullable S subject) {
        if (!conditions.test(ctx)) return false;
        if (entries.isEmpty()) return false;

        for (Entry<S> entry : entries) {
            if (!entry.conditions.test(ctx)) continue;
            if (entry.param.matches(ctx, subject)) return true;
        }
        return false;
    }

    @Override
    public @NotNull DataResult<Inputs<S>> validate() {
        DataResult<Conditions> cv = conditions.validate();
        if (cv.error().isPresent()) {
            String msg = cv.error().map(DataResult.Error::message).orElse("");
            return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
        }

        for (int i = 0; i < entries.size(); i++) {
            Entry<S> entry = entries.get(i);
            DataResult<Entry<S>> ev = entry.validate();
            if (ev.error().isPresent()) {
                String msg = ev.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.ENTRIES + "[" + i + "] invalid: " + msg);
            }
        }

        return SelfValidating.ok(this);
    }

    private static <T> @NotNull List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();

        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) {
            if (t != null) safe.add(t);
        }
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}