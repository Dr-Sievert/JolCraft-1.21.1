package net.sievert.jolcraft.world.recipe.param.input.custom.item.selector;

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
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.world.recipe.param.condition.ConditionalMatcher;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ItemSelector(
        Conditions conditions,
        List<Entry> entries
) implements SelfValidating<ItemSelector>, ConditionGate, ConditionalMatcher<ItemStack>, RegistryIntrospectionSource {

    public record Entry(Conditions conditions, ItemIngredient ingredient) implements SelfValidating<Entry> {
        public Entry {
            conditions = conditions != null ? conditions : Conditions.EMPTY;
            if (ingredient == null) {
                throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.PARAMETER + "'");
            }
        }

        private static final Codec<Entry> RAW_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY).forGetter(Entry::conditions),
                        ItemIngredient.CODEC.fieldOf(JolCraftParameterIds.PARAMETER).forGetter(Entry::ingredient)
                ).apply(inst, Entry::new));

        public static final Codec<Entry> CODEC = ParamCodecs.validated(RAW_CODEC);

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {
                            Conditions.STREAM_CODEC.encode(buf, v.conditions());
                            ItemIngredient.STREAM_CODEC.encode(buf, v.ingredient());
                        },
                        buf -> new Entry(
                                Conditions.STREAM_CODEC.decode(buf),
                                ItemIngredient.STREAM_CODEC.decode(buf)
                        )
                );

        @Override
        public @NotNull DataResult<Entry> validate() {
            DataResult<Conditions> cv = conditions.validate();
            if (cv.error().isPresent()) {
                String msg = cv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
            }

            DataResult<ItemIngredient> iv = ingredient.validate();
            if (iv.error().isPresent()) {
                String msg = iv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.PARAMETER + " invalid: " + msg);
            }

            return SelfValidating.ok(this);
        }
    }

    private static final Codec<ItemSelector> RAW_CODEC =
            Codec.either(
                    ItemIngredient.CODEC,
                    RecordCodecBuilder.<ItemSelector>create(inst -> inst.group(
                            Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY).forGetter(ItemSelector::conditions),
                            Entry.CODEC.listOf().optionalFieldOf(JolCraftParameterIds.ENTRIES, List.of()).forGetter(ItemSelector::entries)
                    ).apply(inst, ItemSelector::new))
            ).xmap(
                    either -> either.map(
                            ing -> new ItemSelector(Conditions.EMPTY, List.of(new Entry(Conditions.EMPTY, ing))),
                            full -> full
                    ),
                    sel -> {
                        boolean structured = sel.conditions() != Conditions.EMPTY || hasAnyEntryConditions(sel.entries());
                        if (structured) return Either.right(sel);

                        if (sel.entries().size() == 1) {
                            return Either.left(sel.entries().getFirst().ingredient());
                        }

                        ArrayList<ItemIngredient.Target> targets = new ArrayList<>();
                        for (Entry e : sel.entries()) {
                            targets.addAll(e.ingredient().targets());
                        }
                        return Either.left(new ItemIngredient(targets));
                    }
            );

    public static final Codec<ItemSelector> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_ENTRIES_STREAM = 2048;

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSelector> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> {
                        Conditions.STREAM_CODEC.encode(buf, v.conditions());
                        buf.writeVarInt(v.entries().size());
                        for (Entry e : v.entries()) {
                            Entry.STREAM_CODEC.encode(buf, e);
                        }
                    },
                    buf -> {
                        Conditions cond = Conditions.STREAM_CODEC.decode(buf);
                        int size = buf.readVarInt();

                        if (size < 0) throw new IllegalArgumentException("negative entry size: " + size);
                        if (size == 0) return new ItemSelector(cond, List.of());
                        if (size > MAX_ENTRIES_STREAM) {
                            throw new IllegalArgumentException("entry size exceeds max " + MAX_ENTRIES_STREAM + " (got " + size + ")");
                        }

                        ArrayList<Entry> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(Entry.STREAM_CODEC.decode(buf));
                        }
                        return new ItemSelector(cond, list);
                    }
            );

    public ItemSelector {
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        entries = sanitizeList(entries);
    }

    public static @NotNull ItemSelector of(@NotNull ItemLike item) {
        return new ItemSelector(Conditions.EMPTY, List.of(new Entry(Conditions.EMPTY, ItemIngredient.of(item))));
    }

    public static @NotNull ItemSelector of(@NotNull ItemIngredient ingredient) {
        return new ItemSelector(Conditions.EMPTY, List.of(new Entry(Conditions.EMPTY, ingredient)));
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditions;
    }

    @Override
    public @NotNull DataResult<ItemSelector> validate() {
        DataResult<Conditions> cv = conditions.validate();
        if (cv.error().isPresent()) {
            String msg = cv.error().map(DataResult.Error::message).orElse("");
            return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + msg);
        }

        if (entries.isEmpty()) {
            return SelfValidating.invalid(JolCraftDictionary.SELECTOR + " must not be empty");
        }

        for (int i = 0; i < entries.size(); i++) {
            DataResult<Entry> ev = entries.get(i).validate();
            if (ev.error().isPresent()) {
                String msg = ev.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.ENTRIES + "[" + i + "] invalid: " + msg);
            }
        }

        return SelfValidating.ok(this);
    }

    @Override
    public boolean matches(@NotNull WorldContext ctx, @NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!conditions.test(ctx)) return false;
        if (entries.isEmpty()) return false;

        for (Entry e : entries) {
            if (!e.conditions().test(ctx)) continue;
            if (e.ingredient().matches(stack)) return true;
        }

        return false;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        if (conditions != Conditions.EMPTY) return List.of();
        if (entries.isEmpty()) return List.of();

        if (entries.size() == 1) {
            Entry e = entries.getFirst();
            if (e.conditions() == Conditions.EMPTY) {
                RegistryIntrospection ii = e.ingredient().introspection();

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

        int holders = 0;
        boolean anyTag = false;

        for (Entry e : entries) {
            RegistryIntrospection ii = e.ingredient().introspection();
            holders += Math.max(0, ii.holderCount());
            anyTag |= ii.hasAnyTag();
        }

        if (holders == 0 && anyTag) {
            return List.of(RegistryIntrospection.anyTag(Registries.ITEM));
        }
        return List.of(RegistryIntrospection.mixed(Registries.ITEM, holders, anyTag));
    }

    private static boolean hasAnyEntryConditions(List<Entry> entries) {
        for (Entry e : entries) {
            if (e != null && e.conditions() != Conditions.EMPTY) return true;
        }
        return false;
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