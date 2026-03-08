package net.sievert.jolcraft.data.recipe.param.input.custom.entity.selector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionalMatcher;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Selector wrapper for {@link EntityIngredient}.
 *
 * Matching:
 * - selector gate AND entry gate
 * - OR across entries
 */
public record EntitySelector(
        Conditions conditions,
        List<Entry> entries
) implements SelfValidating<EntitySelector>, ConditionGate, ConditionalMatcher<Entity>, RegistryIntrospectionSource {

    public static final EntitySelector EMPTY =
            new EntitySelector(Conditions.EMPTY, List.of());

    // ---------------------------------------------------------------------
    // ENTRY
    // ---------------------------------------------------------------------

    public record Entry(Conditions conditions, EntityIngredient ingredient)
            implements SelfValidating<Entry>, RegistryIntrospectionSource {

        public Entry {
            conditions = (conditions != null) ? conditions : Conditions.EMPTY;
        }

        private Conditions conditionsSafe() {
            return conditions != null ? conditions : Conditions.EMPTY;
        }

        private EntityIngredient ingredientSafe() {
            return ingredient != null ? ingredient : EntityIngredient.EMPTY;
        }

        private static final Codec<Entry> RAW_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Conditions.CODEC
                                .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                                .forGetter(Entry::conditionsSafe),
                        EntityIngredient.CODEC
                                .fieldOf(JolCraftParameterIds.PARAMETER)
                                .forGetter(Entry::ingredientSafe)
                ).apply(inst, Entry::new));

        public static final Codec<Entry> CODEC = ParamCodecs.validated(RAW_CODEC);

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {
                            Conditions.STREAM_CODEC.encode(buf, v.conditionsSafe());
                            EntityIngredient.STREAM_CODEC.encode(buf, v.ingredientSafe());
                        },
                        buf -> new Entry(
                                Conditions.STREAM_CODEC.decode(buf),
                                EntityIngredient.STREAM_CODEC.decode(buf)
                        )
                );

        @Override
        public @NotNull DataResult<Entry> validate() {
            DataResult<Conditions> cv = conditionsSafe().validate();
            if (cv.error().isPresent()) {
                String msg = cv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
            }

            if (ingredient == null) {
                return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.PARAMETER + "'");
            }

            DataResult<EntityIngredient> iv = ingredient.validate();
            if (iv.error().isPresent()) {
                String msg = iv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.PARAMETER + " invalid: " + msg);
            }

            return SelfValidating.ok(this);
        }

        @Override
        public @NotNull List<RegistryIntrospection> introspections() {
            if (conditionsSafe() != Conditions.EMPTY) return List.of();

            EntityIngredient ing = ingredientSafe();
            if (ing == EntityIngredient.EMPTY) return List.of();

            return ing.asList();
        }
    }

    // ---------------------------------------------------------------------
    // CODEC / STREAM
    // ---------------------------------------------------------------------

    private static final Codec<EntitySelector> RAW_CODEC =
            Codec.either(
                    EntityIngredient.CODEC,
                    RecordCodecBuilder.<EntitySelector>create(inst -> inst.group(
                            Conditions.CODEC
                                    .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                                    .forGetter(EntitySelector::conditionsSafe),
                            Entry.CODEC.listOf()
                                    .optionalFieldOf(JolCraftParameterIds.ENTRIES, List.of())
                                    .forGetter(EntitySelector::entriesSafe)
                    ).apply(inst, EntitySelector::new))
            ).xmap(
                    either -> either.map(
                            ing -> new EntitySelector(Conditions.EMPTY, List.of(new Entry(Conditions.EMPTY, ing))),
                            full -> full
                    ),
                    (EntitySelector sel) -> {
                        EntitySelector s = (sel == null) ? EMPTY : sel;

                        boolean structured =
                                s.conditionsSafe() != Conditions.EMPTY || hasAnyEntryConditions(s.entriesSafe());

                        if (structured) return Either.right(s);

                        List<Entry> list = s.entriesSafe();
                        if (list.size() == 1) {
                            Entry e = list.getFirst();
                            EntityIngredient ing = (e == null) ? EntityIngredient.EMPTY : e.ingredientSafe();
                            return Either.left(ing);
                        }

                        ArrayList<EntityIngredient.Target> targets = new ArrayList<>();
                        for (Entry e : list) {
                            if (e == null) continue;
                            EntityIngredient ing = e.ingredientSafe();
                            if (ing == EntityIngredient.EMPTY) continue;
                            targets.addAll(ing.targets());
                        }
                        return Either.left(targets.isEmpty() ? EntityIngredient.EMPTY : new EntityIngredient(targets));
                    }
            );

    public static final Codec<EntitySelector> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_ENTRIES_STREAM = 2048;

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySelector> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> {
                        Conditions.STREAM_CODEC.encode(buf, v.conditionsSafe());

                        List<Entry> list = v.entriesSafe();
                        buf.writeVarInt(list.size());
                        for (Entry e : list) {
                            Entry.STREAM_CODEC.encode(buf, e != null ? e : new Entry(Conditions.EMPTY, EntityIngredient.EMPTY));
                        }
                    },
                    buf -> {
                        Conditions cond = Conditions.STREAM_CODEC.decode(buf);

                        int size = buf.readVarInt();
                        if (size <= 0) return new EntitySelector(cond, List.of());

                        int capped = Math.min(size, MAX_ENTRIES_STREAM);
                        ArrayList<Entry> list = new ArrayList<>(Math.min(capped, 64));

                        for (int i = 0; i < capped; i++) list.add(Entry.STREAM_CODEC.decode(buf));
                        for (int i = capped; i < size; i++) Entry.STREAM_CODEC.decode(buf);

                        return new EntitySelector(cond, list);
                    }
            );

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public EntitySelector {
        conditions = (conditions != null) ? conditions : Conditions.EMPTY;
        entries = (entries == null || entries.isEmpty()) ? List.of() : sanitizeList(entries);
    }

    private Conditions conditionsSafe() {
        return conditions != null ? conditions : Conditions.EMPTY;
    }

    private List<Entry> entriesSafe() {
        return entries != null ? entries : List.of();
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EntitySelector> validate() {
        DataResult<Conditions> cv = conditionsSafe().validate();
        if (cv.error().isPresent()) {
            String msg = cv.error().map(DataResult.Error::message).orElse("");
            return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
        }

        List<Entry> list = entriesSafe();
        if (list.isEmpty()) {
            return SelfValidating.invalid(JolCraftDictionary.SELECTOR + " must not be empty");
        }

        for (int i = 0; i < list.size(); i++) {
            Entry e = list.get(i);
            if (e == null) continue;

            DataResult<Entry> ev = e.validate();
            if (ev.error().isPresent()) {
                String msg = ev.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.ENTRIES + "[" + i + "] invalid: " + msg);
            }
        }

        return SelfValidating.ok(this);
    }

    // ---------------------------------------------------------------------
    // MATCHING
    // ---------------------------------------------------------------------

    @Override
    public boolean matches(@NotNull WorldContext ctx, @NotNull Entity entity) {

        if (!conditionsSafe().test(ctx)) {
            return false;
        }

        List<Entry> list = entriesSafe();
        if (list.isEmpty()) return false;

        for (Entry e : list) {
            if (e == null) continue;

            if (!e.conditionsSafe().test(ctx)) {
                continue;
            }

            EntityIngredient ing = e.ingredientSafe();
            if (ing != EntityIngredient.EMPTY && ing.matches(entity)) {
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        if (conditionsSafe() != Conditions.EMPTY) return List.of();

        List<Entry> list = entriesSafe();
        if (list.isEmpty()) return List.of();

        return RegistryIntrospectionSource.mergeByRegistry(list);
    }

    // ---------------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------------

    private static boolean hasAnyEntryConditions(List<Entry> entries) {
        for (Entry e : entries) {
            if (e != null && e.conditionsSafe() != Conditions.EMPTY) return true;
        }
        return false;
    }

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}