package net.sievert.jolcraft.data.recipe.param.input.custom.item.selector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
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
import java.util.Optional;

/**
 * Structural selector wrapper for {@link ItemIngredient}.
 *
 * - Selector conditions gate the whole selector.
 * - Entry conditions gate a specific entry.
 * - Matching is OR across gated entries.
 */
public record ItemSelector(
        Conditions conditions,
        List<Entry> entries
) implements SelfValidating<ItemSelector>, ConditionGate, ConditionalMatcher<ItemStack>, RegistryIntrospectionSource {

    public static final ItemSelector EMPTY = new ItemSelector(Conditions.EMPTY, List.of());

    // ---------------------------------------------------------------------
    // ENTRY
    // ---------------------------------------------------------------------

    public record Entry(Conditions conditions, ItemIngredient ingredient) implements SelfValidating<Entry> {

        public Entry {
            conditions = (conditions != null) ? conditions : Conditions.EMPTY;
        }

        private Conditions conditionsSafe() {
            return conditions != null ? conditions : Conditions.EMPTY;
        }

        public ItemIngredient ingredientSafe() {
            return ingredient != null ? ingredient : ItemIngredient.EMPTY;
        }

        private static final Codec<Entry> RAW_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Conditions.CODEC
                                .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                                .forGetter(Entry::conditionsSafe),
                        ItemIngredient.CODEC
                                .fieldOf(JolCraftParameterIds.PARAMETER)
                                .forGetter(Entry::ingredientSafe)
                ).apply(inst, Entry::new));

        public static final Codec<Entry> CODEC = ParamCodecs.validated(RAW_CODEC);

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {
                            Conditions.STREAM_CODEC.encode(buf, v.conditionsSafe());
                            ItemIngredient.STREAM_CODEC.encode(buf, v.ingredientSafe());
                        },
                        buf -> new Entry(
                                Conditions.STREAM_CODEC.decode(buf),
                                ItemIngredient.STREAM_CODEC.decode(buf)
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

            DataResult<ItemIngredient> iv = ingredient.validate();
            if (iv.error().isPresent()) {
                String msg = iv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.PARAMETER + " invalid: " + msg);
            }

            return SelfValidating.ok(this);
        }
    }

    // ---------------------------------------------------------------------
    // CODEC / STREAM
    // ---------------------------------------------------------------------

    private static final Codec<ItemSelector> RAW_CODEC =
            Codec.either(
                    ItemIngredient.CODEC,
                    RecordCodecBuilder.<ItemSelector>create(inst -> inst.group(
                            Conditions.CODEC
                                    .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                                    .forGetter(ItemSelector::conditionsSafe),
                            Entry.CODEC.listOf()
                                    .optionalFieldOf(JolCraftParameterIds.ENTRIES, List.of())
                                    .forGetter(ItemSelector::entriesSafe)
                    ).apply(inst, ItemSelector::new))
            ).xmap(
                    either -> either.map(
                            ing -> new ItemSelector(Conditions.EMPTY, List.of(new Entry(Conditions.EMPTY, ing))),
                            full -> full
                    ),
                    (ItemSelector sel) -> {
                        ItemSelector s = (sel == null) ? EMPTY : sel;

                        boolean structured =
                                s.conditionsSafe() != Conditions.EMPTY || hasAnyEntryConditions(s.entriesSafe());

                        if (structured) {
                            return Either.right(s);
                        }

                        List<Entry> list = s.entriesSafe();
                        if (list.size() == 1) {
                            Entry e = list.getFirst();
                            ItemIngredient ing = (e == null) ? ItemIngredient.EMPTY : e.ingredientSafe();
                            return Either.left(ing);
                        }

                        ArrayList<ItemIngredient.Target> targets = new ArrayList<>();
                        for (Entry e : list) {
                            if (e == null) continue;
                            ItemIngredient ing = e.ingredientSafe();
                            if (ing == ItemIngredient.EMPTY) continue;
                            targets.addAll(ing.targets());
                        }

                        return Either.left(ItemIngredient.ofTargets(targets));
                    }
            );

    public static final Codec<ItemSelector> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_ENTRIES_STREAM = 2048;

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSelector> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> {
                        Conditions.STREAM_CODEC.encode(buf, v.conditionsSafe());

                        List<Entry> list = v.entriesSafe();
                        buf.writeVarInt(list.size());
                        for (Entry e : list) {
                            Entry.STREAM_CODEC.encode(buf, e != null ? e : new Entry(Conditions.EMPTY, ItemIngredient.EMPTY));
                        }
                    },
                    buf -> {
                        Conditions cond = Conditions.STREAM_CODEC.decode(buf);

                        int size = buf.readVarInt();
                        if (size <= 0) return new ItemSelector(cond, List.of());

                        int capped = Math.min(size, MAX_ENTRIES_STREAM);
                        ArrayList<Entry> list = new ArrayList<>(Math.min(capped, 64));

                        for (int i = 0; i < capped; i++) list.add(Entry.STREAM_CODEC.decode(buf));
                        for (int i = capped; i < size; i++) Entry.STREAM_CODEC.decode(buf);

                        return new ItemSelector(cond, list);
                    }
            );

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public ItemSelector {
        conditions = (conditions != null) ? conditions : Conditions.EMPTY;
        entries = (entries == null || entries.isEmpty()) ? List.of() : sanitizeList(entries);
    }

    private Conditions conditionsSafe() {
        return conditions != null ? conditions : Conditions.EMPTY;
    }

    public List<Entry> entriesSafe() {
        return entries != null ? entries : List.of();
    }

    // ---------------------------------------------------------------------
    // FACTORIES
    // ---------------------------------------------------------------------

    public static ItemSelector of(@NotNull ItemLike item) {
        return new ItemSelector(
                Conditions.EMPTY,
                List.of(new Entry(Conditions.EMPTY, ItemIngredient.of(item)))
        );
    }

    public static ItemSelector of(@NotNull ItemIngredient ingredient) {
        return new ItemSelector(
                Conditions.EMPTY,
                List.of(new Entry(Conditions.EMPTY, ingredient))
        );
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<ItemSelector> validate() {
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
    public boolean matches(@NotNull WorldContext ctx, @NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;

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

            ItemIngredient ing = e.ingredientSafe();
            if (ing != ItemIngredient.EMPTY && ing.matches(stack)) {
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

        if (list.size() == 1) {
            Entry e = list.getFirst();
            if (e != null && e.conditionsSafe() == Conditions.EMPTY) {
                ItemIngredient ing = e.ingredientSafe();
                if (ing != null && ing != ItemIngredient.EMPTY) {
                    RegistryIntrospection ii = ing.introspection();

                    Optional<Holder<?>> hOpt = ii.singleConcreteOpt();
                    if (ii.exactlyOneConcrete() && hOpt.isPresent()) {
                        @SuppressWarnings("unchecked")
                        Holder<Item> h = (Holder<Item>) hOpt.get();
                        return List.of(RegistryIntrospection.single(Registries.ITEM, h));
                    }

                    Optional<TagKey<?>> tOpt = ii.singleTagOpt();
                    if (ii.exactlyOneTag() && tOpt.isPresent()) {
                        return List.of(RegistryIntrospection.singleTag(Registries.ITEM, tOpt.get()));
                    }

                    if (ii.hasAnyTag() && ii.holderCount() == 0) {
                        return List.of(RegistryIntrospection.anyTag(Registries.ITEM));
                    }

                    return List.of(RegistryIntrospection.mixed(Registries.ITEM, ii.holderCount(), ii.hasAnyTag()));
                }
            }
        }

        int holders = 0;
        boolean anyTag = false;

        for (Entry e : list) {
            if (e == null) continue;

            ItemIngredient ing = e.ingredientSafe();
            if (ing == null || ing == ItemIngredient.EMPTY) continue;

            RegistryIntrospection ii = ing.introspection();
            holders += Math.max(0, ii.holderCount());
            anyTag |= ii.hasAnyTag();
        }

        if (holders == 0 && anyTag) return List.of(RegistryIntrospection.anyTag(Registries.ITEM));
        return List.of(RegistryIntrospection.mixed(Registries.ITEM, holders, anyTag));
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