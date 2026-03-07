package net.sievert.jolcraft.data.recipe.param.input.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Polymorphic wrapper for heterogeneous {@link InputParam} entries.
 *
 * Gating:
 * - Top-level {@link #conditions()} gates the entire input block (entrypoint gate).
 * - Each {@link Entry} may have its own {@link Conditions} gate (entry-level gate).
 *
 * Strict server-only runtime:
 * - WorldContext is always required and never null.
 *
 * Matching semantics:
 * - ANY semantics (OR-group): at least one gated entry must match.
 * - No entries => false.
 */
public record Inputs<S>(
        Conditions conditions,
        List<Entry<S>> entries
) implements SelfValidating<Inputs<S>>, ConditionGate, RegistryIntrospectionSource {

    private static final InputParam<?, ?> MISSING_PARAM =
            new InputDispatch.Invalid(InputDispatch.TYPE_MISSING);

    @SuppressWarnings("unchecked")
    private static <S> InputParam<?, S> missingParam() {
        return (InputParam<?, S>) MISSING_PARAM;
    }

    // ---------------------------------------------------------------------
    // SENTINEL
    // ---------------------------------------------------------------------

    public static final Inputs<Object> EMPTY = new Inputs<>(Conditions.EMPTY, List.of());

    // ---------------------------------------------------------------------
    // ENTRY
    // ---------------------------------------------------------------------

    public record Entry<S>(Conditions conditions, InputParam<?, S> param)
            implements SelfValidating<Entry<S>>, RegistryIntrospectionSource {

        public Entry {
            conditions = (conditions != null) ? conditions : Conditions.EMPTY;
        }

        private Conditions conditionsSafe() {
            return conditions != null ? conditions : Conditions.EMPTY;
        }

        private InputParam<?, S> paramSafe() {
            return param != null ? param : missingParam();
        }

        @SuppressWarnings("unchecked")
        private static <S> Codec<Entry<S>> rawCodec() {
            Codec<Entry<?>> built = RecordCodecBuilder.create(inst -> inst.group(
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(Entry::conditionsSafe),
                    InputDispatch.CODEC
                            .fieldOf(JolCraftParameterIds.PARAMETER)
                            .forGetter(Entry::paramSafe)
            ).apply(inst, (c, p) -> new Entry<>(c, (InputParam<?, ?>) p)));

            return (Codec<Entry<S>>) (Codec<?>) built;
        }

        public static <S> Codec<Entry<S>> codec() {
            return ParamCodecs.validated(rawCodec());
        }

        @SuppressWarnings("unchecked")
        public static <S> StreamCodec<RegistryFriendlyByteBuf, Entry<S>> streamCodec() {
            return StreamCodec.of(
                    (buf, v) -> {
                        Conditions.STREAM_CODEC.encode(buf, v.conditionsSafe());
                        InputDispatch.STREAM_CODEC.encode(buf, ((Entry<?>) v).paramSafe());
                    },
                    buf -> new Entry<>(
                            Conditions.STREAM_CODEC.decode(buf),
                            (InputParam<?, S>) InputDispatch.STREAM_CODEC.decode(buf)
                    )
            );
        }

        @Override
        public @NotNull List<RegistryIntrospection> introspections() {
            ArrayList<RegistryIntrospection> out = new ArrayList<>(8);
            out.addAll(conditionsSafe().introspections());

            InputParam<?, S> p = paramSafe();
            if (p instanceof RegistryIntrospectionSource src) {
                out.addAll(src.introspections());
            }
            return out.isEmpty() ? List.of() : List.copyOf(out);
        }

        @Override
        public @NotNull DataResult<Entry<S>> validate() {
            DataResult<Conditions> cv = conditionsSafe().validate();
            if (cv.error().isPresent()) {
                String msg = cv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
            }

            InputParam<?, S> p = paramSafe();
            if (p instanceof InputDispatch.Invalid(net.minecraft.resources.ResourceLocation unknownType)) {
                boolean missing = InputDispatch.TYPE_MISSING.equals(unknownType);
                return SelfValidating.invalid(
                        missing
                                ? "missing required field '" + JolCraftParameterIds.PARAMETER + "'"
                                : JolCraftParameterIds.PARAMETER + " has unknown type: " + unknownType
                );
            }

            DataResult<?> pv = p.validate();
            if (pv.error().isPresent()) {
                String msg = pv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.PARAMETER + " invalid: " + msg);
            }

            return SelfValidating.ok(this);
        }
    }

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <S> Codec<Inputs<S>> rawCodec() {
        Codec<List<Entry<?>>> entryListCodec = (Codec) Entry.codec().listOf();

        Codec<Inputs<?>> built = RecordCodecBuilder.create(inst -> inst.group(
                Conditions.CODEC
                        .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                        .forGetter(Inputs::conditionsSafe),

                entryListCodec
                        .optionalFieldOf(JolCraftParameterIds.ENTRIES, List.of())
                        .forGetter(v -> (List<Entry<?>>) (List<?>) v.entriesSafe())

        ).apply(inst, (Conditions c, List<Entry<?>> e) -> new Inputs<>(c, (List) e)));

        return (Codec<Inputs<S>>) (Codec<?>) built;
    }

    public static <S> Codec<Inputs<S>> codec() {
        return ParamCodecs.validated(rawCodec());
    }

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final int MAX_ENTRIES_STREAM = 2048;

    public static <S> StreamCodec<RegistryFriendlyByteBuf, Inputs<S>> streamCodec() {
        return StreamCodec.of(
                (buf, v) -> {
                    Conditions.STREAM_CODEC.encode(buf, v.conditionsSafe());

                    List<Entry<S>> list = v.entriesSafe();
                    buf.writeVarInt(list.size());

                    StreamCodec<RegistryFriendlyByteBuf, Entry<S>> esc = Entry.streamCodec();

                    for (Entry<S> e : list) {
                        Entry<S> safe = (e != null) ? e : new Entry<>(Conditions.EMPTY, Inputs.<S>missingParam());
                        esc.encode(buf, safe);
                    }
                },
                buf -> {
                    Conditions cond = Conditions.STREAM_CODEC.decode(buf);

                    int size = buf.readVarInt();
                    if (size <= 0) return new Inputs<>(cond, List.of());

                    int capped = Math.min(size, MAX_ENTRIES_STREAM);
                    ArrayList<Entry<S>> list = new ArrayList<>(Math.min(capped, 64));

                    StreamCodec<RegistryFriendlyByteBuf, Entry<S>> esc = Entry.streamCodec();

                    for (int i = 0; i < capped; i++) {
                        list.add(esc.decode(buf));
                    }
                    for (int i = capped; i < size; i++) {
                        esc.decode(buf);
                    }

                    return new Inputs<>(cond, sanitizeList(list));
                }
        );
    }

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public Inputs {
        conditions = (conditions != null) ? conditions : Conditions.EMPTY;
        entries = (entries == null || entries.isEmpty()) ? List.of() : sanitizeList(entries);
    }

    private Conditions conditionsSafe() {
        return conditions != null ? conditions : Conditions.EMPTY;
    }

    private List<Entry<S>> entriesSafe() {
        return entries != null ? entries : List.of();
    }

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION (flatten)
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        ArrayList<RegistryIntrospectionSource> src = new ArrayList<>(1 + entriesSafe().size());
        src.add(conditionsSafe());

        for (Entry<S> e : entriesSafe()) {
            if (e == null) continue;
            src.add(e);
        }

        return RegistryIntrospectionSource.mergeByRegistry(src);
    }

    // ---------------------------------------------------------------------
    // RUNTIME
    // ---------------------------------------------------------------------

    public boolean matches(@NotNull WorldContext ctx, @Nullable S subject) {
        if (!conditionsSafe().test(ctx)) {
            return false;
        }

        List<Entry<S>> list = entriesSafe();
        if (list.isEmpty()) {
            return false;
        }

        for (Entry<S> e : list) {
            if (e == null) continue;
            if (!e.conditionsSafe().test(ctx)) continue;

            InputParam<?, S> p = e.paramSafe();
            if (p.matches(ctx, subject)) return true;
        }

        return false;
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<Inputs<S>> validate() {
        DataResult<Conditions> cv = conditionsSafe().validate();
        if (cv.error().isPresent()) {
            String msg = cv.error().map(DataResult.Error::message).orElse("");
            return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
        }

        List<Entry<S>> list = entriesSafe();
        for (int i = 0; i < list.size(); i++) {
            Entry<S> e = list.get(i);
            if (e == null) {
                return SelfValidating.invalid(JolCraftParameterIds.ENTRIES + " contains null at index " + i);
            }
            DataResult<Entry<S>> ev = e.validate();
            if (ev.error().isPresent()) {
                String msg = ev.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.ENTRIES + "[" + i + "] invalid: " + msg);
            }
        }

        return SelfValidating.ok(this);
    }
}